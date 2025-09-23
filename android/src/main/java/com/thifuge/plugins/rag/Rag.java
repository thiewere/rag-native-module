package com.thifuge.plugins.rag;

import com.getcapacitor.Logger;

public class Rag {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
