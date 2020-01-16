/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.utils;

import com.sc.rhinosandbox.annotations.RhinoFunction;
import com.sc.rhinosandbox.misc.RhinoBaseFunction;

/**
 *
 * @author lucifer
 */
@RhinoFunction("print")
public class RhinoPrint extends RhinoBaseFunction {

    @Override
    protected Object call() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getArgumentsCount(); i++) {
            sb.append(getArgument(i, String.class));
        }
        System.out.print(sb.toString());
        return null;
    }

}
