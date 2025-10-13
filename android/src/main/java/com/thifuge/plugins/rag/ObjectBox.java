package com.thifuge.plugins.rag;

import android.content.Context;

import io.objectbox.BoxStore;

public class ObjectBox {
    private static BoxStore store;

    public static void init(Context context) {
        store = MyObjectBox.builder()
                .androidContext(context)
                .build();
    }

    public static BoxStore get() {
        return store;
    }
}
