package com.thifuge.plugins.rag;

public class LlamaNative {
    static  {
        System.loadLibrary("llama-cpp-arm64");
    }

    public static native String nativeHello();
}
