/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 *
 * @author lucifer
 */
public class SandboxContextFactory extends ContextFactory {

    private Context.ClassShutterSetter classShutterContainer = null;

    @Override
    protected Context makeContext() {
        Context ctx = super.makeContext();
        ctx.setWrapFactory(new SandboxWrapFactory());
        classShutterContainer = ctx.getClassShutterSetter();
        classShutterContainer.setClassShutter(new SandboxClassShutter(SandboxRegistry.instance().getAllowedClasses()));
        return ctx;
    }

}
