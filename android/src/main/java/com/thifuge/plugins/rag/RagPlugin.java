package com.thifuge.plugins.rag;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.Map;

import ai.annadata.plugin.capacitor.LlamaCpp;

@CapacitorPlugin(name = "Rag")
public class RagPlugin extends Plugin {

    private Rag implementation = new Rag();

        // ======================================================================
    // 2. Halte eine Instanz der LlamaCpp-Implementierung
    //    und die ID für unseren Kontext.
    // ======================================================================
    private LlamaCpp llamaCpp;
    private static final int CONTEXT_ID = 0; // Wir verwenden eine feste ID für unseren Kontext
    private boolean isContextInitialized = false;

      /**
     * Stellt sicher, dass die LlamaCpp-Instanz initialisiert ist (Lazy Loading).
     */
    private LlamaCpp getLlamaCpp() {
        if (this.llamaCpp == null) {
            // Initialisiert die LlamaCpp-Klasse mit dem Android-Kontext des Plugins
            this.llamaCpp = new LlamaCpp(getContext());
        }
        return this.llamaCpp;
    }

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

    // ======================================================================
    // 3. Korrekte Implementierung der runInference-Methode
    // ======================================================================
    @PluginMethod
    public void runInference(PluginCall call) {
        // Schritt 1: Prüfen, ob der Kontext (das Modell) bereits geladen ist.
        if (isContextInitialized) {
            // Wenn ja, direkt die Inferenz starten.
            performCompletion(call);
        } else {
            // Wenn nein, zuerst den Kontext initialisieren.
            // Die Parameter für die Initialisierung kommen direkt aus dem Aufruf.
            JSObject initParams = call.getData();
            
            getLlamaCpp().initContext(CONTEXT_ID, initParams, result -> {
                if (result.isSuccess()) {
                    // Kontext erfolgreich initialisiert.
                    this.isContextInitialized = true;
                    // Jetzt die Inferenz starten.
                    performCompletion(call);
                } else {
                    // Fehler bei der Initialisierung.
                    call.reject("Llama-Kontext konnte nicht initialisiert werden: " + result.getError().getMessage());
                }
            });
        }
    }

    /**
     * Hilfsmethode, die die eigentliche Inferenz (completion) durchführt.
     * Wird aufgerufen, nachdem der Kontext garantiert initialisiert ist.
     */
    private void performCompletion(PluginCall call) {
        // Die Parameter für die Inferenz kommen ebenfalls direkt aus dem Aufruf.
        JSObject completionParams = call.getData();

        getLlamaCpp().completion(CONTEXT_ID, completionParams, result -> {
            if (result.isSuccess()) {
                // Inferenz war erfolgreich.
                Map<String, Object> data = result.getData();
                JSObject ret = new JSObject();
                // Die `LlamaCpp.java` gibt eine Map zurück. Wir müssen den "text" oder "content" Schlüssel finden.
                if (data.containsKey("text")) {
                    ret.put("text", data.get("text"));
                } else if (data.containsKey("content")) {
                    ret.put("text", data.get("content"));
                } else {
                    // Fallback, falls der Schlüssel anders heißt.
                    ret.put("text", data.toString());
                }
                call.resolve(ret);
            } else {
                // Fehler bei der Inferenz.
                call.reject("Llama-Inferenz fehlgeschlagen: " + result.getError().getMessage());
            }
        });
    }
}
