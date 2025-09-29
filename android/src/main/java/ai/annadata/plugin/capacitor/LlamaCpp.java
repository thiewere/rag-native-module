package ai.annadata.plugin.capacitor;

public class LlamaCpp {

    static {
        System.loadLibrary("llama-cpp-arm64");
    }

    public static native long initContextNative(String modelPath, String[] searchPaths, Object params);
    public static native void releaseContextNative(long contextId);
    public static native Object completionNative(long contextId, Object params);
}
