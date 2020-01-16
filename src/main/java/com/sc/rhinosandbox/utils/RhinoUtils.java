/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.utils;

import com.sc.rhinosandbox.annotations.RhinoFunction;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author lucifer
 */
public class RhinoUtils {

    @RhinoFunction("readFile")
    public static String readFile(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader isr = new BufferedReader(new FileReader(fileName));
        while (isr.ready()) {
            sb.append(isr.readLine()).append("\r");
        }

        return sb.toString();
    }
}
