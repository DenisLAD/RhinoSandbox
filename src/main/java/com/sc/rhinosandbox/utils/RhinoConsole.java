/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.utils;

import com.sc.rhinosandbox.annotations.RhinoClass;
import com.sc.rhinosandbox.annotations.RhinoFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lucifer
 */
@RhinoClass("console")
public class RhinoConsole {

    private final static Logger logger = LoggerFactory.getLogger(RhinoConsole.class);

    public static void log(Object... args) {
        logger.info(buildString(args));
    }

    public static void info(Object... args) {
        logger.info(buildString(args));
    }

    public static void warn(Object... args) {
        logger.warn(buildString(args));
    }

    public static void error(Object... args) {
        logger.error(buildString(args));
    }

    private static String buildString(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg != null ? arg.toString() : null).append(" ");
        }
        return sb.toString();
    }

}
