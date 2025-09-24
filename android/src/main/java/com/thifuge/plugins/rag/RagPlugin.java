package com.thifuge.plugins.rag;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Rag")
public class RagPlugin extends Plugin {

    private Rag implementation = new Rag();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    // Hier in RagPlugin.java
    @PluginMethod
    public void addTwoNumbers(PluginCall call) {
        int value1 = call.getInt("value1");
        int value2 = call.getInt("value2");
       
        JSObject ret = new JSObject();
        ret.put("value", implementation.addTwoNumbers(value1, value2));
        call.resolve(ret);
    }
}
