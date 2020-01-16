/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.rhino;

import com.sc.rhinosandbox.annotations.AllowedInRhino;
import com.sc.rhinosandbox.annotations.RhinoClass;
import com.sc.rhinosandbox.annotations.RhinoFunction;
import com.sc.rhinosandbox.misc.RhinoBaseFunction;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.Modifier;
import org.mozilla.javascript.BaseFunction;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lucifer
 */
public class SandboxRegistry {

    private static SandboxRegistry instance;
    private final List<String> allowedClasses;
    private final Map<String, Class<?>> classes;
    private final Map<String, BaseFunction> functions;
    private final Reflections reflections;

    private static final Logger logger = LoggerFactory.getLogger(SandboxRegistry.class);

    public static SandboxRegistry instance() {
        return instance != null ? instance : (instance = new SandboxRegistry());
    }

    private SandboxRegistry() {
        allowedClasses = new ArrayList<>();
        classes = new HashMap<>();
        functions = new HashMap<>();
        reflections = new Reflections(".*",
                new TypeAnnotationsScanner(),
                new TypeElementsScanner(),
                new SubTypesScanner(),
                new FieldAnnotationsScanner(),
                new MethodAnnotationsScanner()
        );
        scanForAllowedClasses();
        scanForClasses();
        scanForFunctions();
    }

    public Collection<String> getAllowedClasses() {
        return allowedClasses;
    }

    public Map<String, Class<?>> getClasses() {
        return classes;
    }

    public Map<String, BaseFunction> getFunctions() {
        return functions;
    }

    public Reflections getReflections() {
        return reflections;
    }

    private void scanForAllowedClasses() {
        allowedClasses.clear();
        reflections
                .getTypesAnnotatedWith(AllowedInRhino.class)
                .forEach((cls) -> {
                    logger.info("Class edded to allowed list: " + cls.getName());
                    allowedClasses.add(cls.getName());
                });
        reflections
                .getTypesAnnotatedWith(RhinoClass.class)
                .forEach((cls) -> {
                    logger.info("Class edded to allowed list: " + cls.getName());
                    allowedClasses.add(cls.getName());
                });
    }

    private void scanForClasses() {
        classes.clear();
        reflections
                .getTypesAnnotatedWith(RhinoClass.class)
                .forEach((cls) -> {
                    validate(classes, cls.getAnnotation(RhinoClass.class).value(), "class");
                    logger.info("Class edded as JS object: " + cls.getAnnotation(RhinoClass.class).value() + " (" + cls.getName() + ")");
                    classes.put(cls.getAnnotation(RhinoClass.class).value(), cls);
                });
    }

    private void scanForFunctions() {
        functions.clear();
        scanForTypeFunctions();
        scanForStaticFunctions();
    }

    private void scanForTypeFunctions() {
        reflections
                .getTypesAnnotatedWith(RhinoFunction.class)
                .forEach((cls) -> {
                    validate(functions, cls.getAnnotation(RhinoFunction.class).value(), "function");
                    if (!(BaseFunction.class.isAssignableFrom(cls))) {
                        throw new IllegalStateException("Declared class in not an instance of BaseFunction: " + cls.getName());
                    }
                    try {
                        logger.info("Class added as JS function: " + cls.getAnnotation(RhinoFunction.class).value() + " (" + cls.getName() + ")");
                        functions.put(cls.getAnnotation(RhinoFunction.class).value(), (BaseFunction) cls.newInstance());
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
    }

    private void scanForStaticFunctions() {
        reflections
                .getMethodsAnnotatedWith(RhinoFunction.class)
                .forEach((method) -> {
                    validate(functions, method.getAnnotation(RhinoFunction.class).value(), "function");
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("Method should be static: " + method.getDeclaringClass().getName() + "." + method.getName());
                    }
                    if (!Modifier.isPublic(method.getModifiers())) {
                        throw new IllegalStateException("Method should be public: " + method.getDeclaringClass().getName() + "." + method.getName());
                    }
                    logger.info("Static function added as JS function: " + method.getAnnotation(RhinoFunction.class).value() + " (" + method.getDeclaringClass().getName() + "." + method.getName() + ")");
                    functions.put(method.getAnnotation(RhinoFunction.class).value(), new StaticWrapper(method));

                });
    }

    private void validate(Map<String, ?> classes, String value, String type) {
        if (classes.containsKey(value)) {
            throw new IllegalStateException("Cannot add `" + value + "` to " + type + " list");
        }
    }

    private class StaticWrapper extends RhinoBaseFunction {

        private final Method method;

        public StaticWrapper(Method method) {
            this.method = method;
        }

        @Override
        protected Object call() {
            try {
                if (method.isVarArgs()) {
                    return method.invoke(null, getArguments());
                }

                Object[] args = new Object[method.getParameterCount()];
                for (int i = 0; i < args.length; i++) {
                    args[i] = getArgument(i, method.getParameters()[i].getType());
                }

                return method.invoke(null, args);

            } catch (Exception e) {
                throw new RuntimeException("Error while executing method", e);
            }
        }

    }

}
