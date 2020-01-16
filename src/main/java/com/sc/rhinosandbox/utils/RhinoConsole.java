/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.utils;

import com.sc.rhinosandbox.annotations.RhinoClass;
import com.sc.rhinosandbox.annotations.RhinoFunction;

/**
 *
 * @author lucifer
 */
@RhinoClass("console")
public class RhinoConsole {

    @RhinoFunction("log")
    public static void log(String text) {
        System.out.println(text);
    }
}
