/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 *
 * @author lucifer
 */
public class SandboxWrapFactory extends WrapFactory {

    public SandboxWrapFactory() {
        setJavaPrimitiveWrap(false);
    }

    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
        return new SandboxNativeObject(scope, javaObject, staticType);
    }
     
    
    
}
