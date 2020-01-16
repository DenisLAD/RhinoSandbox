/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author lucifer
 */
public abstract class RhinoBaseFunction extends BaseFunction {

    private Context context;
    private Scriptable scope;
    private Scriptable self;
    private Object[] arguments;

    public Context getContext() {
        return context;
    }

    public Scriptable getScope() {
        return scope;
    }

    public Scriptable getSelf() {
        return self;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public int getArgumentsCount() {
        return arguments.length;
    }

    public static <T> T toType(Object val, Class<T> cls) {
        if (val == null) {
            return null;
        }
        if (cls == String.class) {
            val = val.toString();
            return (T) val;
        } else if (Number.class.isAssignableFrom(cls)) {
            if (val instanceof String) {
                val = Double.parseDouble((String) val);
            }
            Number num = (Number) val;
            if (cls == Integer.class || cls == int.class) {
                val = num.intValue();
            } else if (cls == Long.class || cls == long.class) {
                val = num.longValue();
            } else if (cls == Float.class || cls == float.class) {
                val = num.floatValue();
            } else if (cls == Double.class || cls == double.class) {
                val = num.doubleValue();
            } else if (cls == Byte.class || cls == byte.class) {
                val = num.byteValue();
            } else if (cls == Short.class || cls == short.class) {
                val = num.shortValue();
            }
        } else if (List.class.isAssignableFrom(cls)) {
            val = fromArray((Scriptable) val);
        } else if (Map.class.isAssignableFrom(cls)) {
            val = fromObject((Scriptable) val);
        }
        return (T) val;
    }

    public <T> T getArgument(int idx, Class<T> cls) {
        Object arg = getArgument(idx);
        return toType(arg, cls);
    }

    public Object getArgument(int idx) {
        if (idx >= getArgumentsCount() || idx < 0) {
            throw new IllegalStateException("Index " + idx + " not in range from 0 to " + getArgumentsCount());
        }
        return getArguments()[idx];
    }

    public static ScriptableObject newScriptable() {
        return Context.getCurrentContext().initStandardObjects();
    }

    protected static Scriptable wrap(Object obj) {
        return Context.getCurrentContext().getWrapFactory().wrapNewObject(Context.getCurrentContext(), Context.getCurrentContext().initStandardObjects(), obj);
    }

    protected static Object unwrap(Scriptable source) {
        if (source instanceof NativeJavaObject) {
            return ((NativeJavaObject) source).unwrap();
        }
        return source;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        this.context = cx;
        this.scope = scope;
        this.self = thisObj;
        this.arguments = args;
        return call();
    }

    public static Scriptable toArray(Collection<Object> collection) {
        return new NativeArray(collection.toArray());
    }

    public static Collection<Object> fromArray(Scriptable rhinoArray) {
        if (!(rhinoArray instanceof NativeArray)) {
            throw new IllegalStateException("Passed parameter is not an Rhino Array");
        }
        NativeArray arr = (NativeArray) rhinoArray;

        ArrayList<Object> list = new ArrayList<>();
        for (long i = 0; i < arr.getLength(); i++) {
            list.add(arr.get(i));
        }

        return list;
    }

    public static Scriptable toObject(Map<String, ?> map) {
        ScriptableObject so = newScriptable();

        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Collection) {
                value = toArray((Collection<Object>) value);
            }
            so.put(key, so, value);
        }

        return so;
    }

    public static Map<String, ?> fromObject(Scriptable so) {
        Map<String, Object> result = new HashMap<>();

        for (Object key : (Object[]) so.getIds()) {
            Object value = so.get(key.toString(), so);
            if (value instanceof NativeArray) {
                value = fromArray((NativeArray) value);
            }
            result.put(key.toString(), value);
        }

        return result;
    }

    protected abstract Object call();

}
