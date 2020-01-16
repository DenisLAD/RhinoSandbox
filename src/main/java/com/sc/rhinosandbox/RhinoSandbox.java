/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox;

import com.sc.rhinosandbox.rhino.SandboxContextFactory;
import com.sc.rhinosandbox.rhino.SandboxRegistry;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author lucifer
 */
public class RhinoSandbox {

    public final static RhinoSandbox INSTANCE = new RhinoSandbox();
    private ScriptableObject topLevel;

    private RhinoSandbox() {
        ContextFactory.initGlobal(new SandboxContextFactory());
        prepareTopLevelScope();
    }

    public Object call(final String name, final String script) {
        return ContextFactory.getGlobal().call(new InternalSnadboxEnviroment(name, script));
    }

    private void prepareTopLevelScope() {
        Context ctx;
        topLevel = (ctx = Context.enter()).initStandardObjects();

        SandboxRegistry.instance().getClasses().forEach((key, value) -> {
            topLevel.defineProperty(key, ctx.getWrapFactory().wrapJavaClass(ctx, topLevel, value), ScriptableObject.DONTENUM);
        });

        SandboxRegistry.instance().getFunctions().forEach((key, value) -> {
            topLevel.defineProperty(key, value, ScriptableObject.DONTENUM);
        });
        Context.exit();
    }

    private class InternalSnadboxEnviroment implements ContextAction {

        private final String name;
        private final String script;

        public InternalSnadboxEnviroment(String name, String script) {
            this.name = name;
            this.script = script;
        }

        @Override
        public Object run(Context cx) {
            cx.setOptimizationLevel(9);
            cx.setLanguageVersion(Context.VERSION_1_8);
            Scriptable so = prepareScope(cx);
            Object ret = cx.evaluateString(so, script, name, 1, null);
            finishScope(cx, so);
            return ret;
        }

        private Scriptable prepareScope(Context cx) {
            return cx.initStandardObjects(topLevel, false);
        }

        private void finishScope(Context cx, Scriptable so) {

        }

    }
}
