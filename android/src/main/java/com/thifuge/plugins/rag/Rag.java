package com.thifuge.plugins.rag;

import com.getcapacitor.Logger;

public class Rag {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }

    // Hier in Rag.java
    public int addTwoNumbers(int value1, int value2) {

        Logger.info("Summe: ", Integer.toString(value1+value2));
        return value1+value2;
    }
}
