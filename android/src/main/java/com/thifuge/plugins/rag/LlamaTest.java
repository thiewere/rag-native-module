package com.thifuge.plugins.rag;

import android.util.Log;

import ai.annadata.plugin.capacitor.LlamaCpp;

public class LlamaTest {
    public static void testInit() {
        // Modelpfad muss auf einem existierenden .gguf/.bin Modell liegen
        String modelPath = "/sdcard/Download/ggml-model.gguf";
        String[] searchPaths = new String[]{};

        try {
            long ctxId = LlamaCpp.initContextNative(modelPath, searchPaths, null);
            Log.i("LlamaTest", "initContextNative returned: " + ctxId);
        } catch (Throwable t) {
            Log.e("LlamaTest", "Exception calling initContextNative", t);
        }
    }
}
