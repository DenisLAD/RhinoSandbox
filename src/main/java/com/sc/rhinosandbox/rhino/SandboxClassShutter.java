/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.rhino;

import java.util.Collection;
import org.mozilla.javascript.ClassShutter;

/**
 *
 * @author lucifer
 */
public class SandboxClassShutter implements ClassShutter {

    private final Collection<String> allowedClasses;

    public SandboxClassShutter(Collection<String> allowedClasses) {
        this.allowedClasses = allowedClasses;
    }

    @Override
    public boolean visibleToScripts(String fullClassName) {
        return allowedClasses.contains(fullClassName);
    }

}
