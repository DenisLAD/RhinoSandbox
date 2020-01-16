/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.rhino;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author lucifer
 */
public class SandboxNativeObject extends NativeJavaObject {

    public SandboxNativeObject(Scriptable scope, Object javaObject, Class staticType) {
        super(scope, javaObject, staticType);
    }

    @Override
    public Object get(String name, Scriptable start) {
        if (name.equals("getClass")) {
            return Scriptable.NOT_FOUND;
        }
        return super.get(name, start);
    }

}
