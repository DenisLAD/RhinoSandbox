/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox;

import com.sc.rhinosandbox.utils.RhinoUtils;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author lucifer
 */
public class App {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("log4j.properties"));

        PropertyConfigurator.configure(props);

        RhinoSandbox.INSTANCE.call("test", RhinoUtils.readFile("main.js"));
    }
}
