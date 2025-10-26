package com.thifuge.plugins.rag;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import ai.annadata.plugin.capacitor.LlamaCpp;

@CapacitorPlugin(name = "Rag")
public class RagPlugin extends Plugin {

    private Rag implementation = new Rag();

    private LlamaCpp llamaCpp;

    @Override
    public void load() {
        llamaCpp = new LlamaCpp(getContext());
    }


    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }
}
