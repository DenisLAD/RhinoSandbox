/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox;

/**
 *
 * @author lucifer
 */
public class App {

    public static void main(String[] args) {
        RhinoSandbox.INSTANCE.call("test", "for(let i=0; i<1000; i++) { print('Data: ');println(i); }; console.log('Done...');");
    }
}
