package com.thifuge.plugins.rag;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.List;
import java.util.Map;

import ai.annadata.plugin.capacitor.LlamaCpp;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.ObjectWithScore;

@CapacitorPlugin(name = "Rag")
public class RagPlugin extends Plugin {

    private BoxStore boxStore;
    private Rag implementation;

        // ======================================================================
    // 2. Halte eine Instanz der LlamaCpp-Implementierung
    //    und die ID für unseren Kontext.
    // ======================================================================
    private LlamaCpp llamaCpp;
    private static final int CONTEXT_ID = 0; // Wir verwenden eine feste ID für unseren Kontext
    private boolean isContextInitialized = false;

    /**
     * Wird beim Start der App aufgerufen. Perfekt, um die Datenbank zu initialisieren.
     */
    @Override
    public void load() {
        super.load();

        // 1. Initialisiere ObjectBox
        if (boxStore == null) {
            // MyObjectBox wird automatisch generiert, nachdem du das Projekt gebaut hast
            boxStore = MyObjectBox.builder()
                    .androidContext(getContext().getApplicationContext())
                    .build();
        }

        this.llamaCpp = new LlamaCpp(getContext());

        // 2. Erstelle die Instanz unserer Logik-Klasse und übergebe den BoxStore
        implementation = new Rag(boxStore, this.llamaCpp);
    }

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

    @PluginMethod
    public void runRagInference(PluginCall call) {
        String prompt = call.getString("prompt");
        String model = call.getString("model");
       
        if(prompt == null || prompt.isEmpty()){
            call.reject("Ein 'prompt' mit der Frage wird benötigt.");
            return;
        }

        if(model == null || model.isEmpty()) {
            call.reject("Ein 'model' für die Inferenz wird benötigt.");
            return;
        }

       Runnable getEmbedding = () -> {
            getLlamaCpp().embedding(CONTEXT_ID, prompt, null, (embeddingResult) -> {
               if (embeddingResult.isSuccess()) {
                   Map<String, Object> data = embeddingResult.getData();
                   List<Double> embeddingList = (List<Double>) data.get("embedding");
                   float[] queryVector = new float[embeddingList.size()];
                   for (int i = 0; i < embeddingList.size(); i++) {
                       queryVector[i] = embeddingList.get(i).floatValue();
                   }
                   List<ObjectWithScore<DocumentChunk>> chunkList = implementation.searchDocs(prompt, queryVector);
                   String kontext = "";
                   for (ObjectWithScore<DocumentChunk> chunk : chunkList) {
                      kontext += chunk.get().content;
                   }

                   String finalPrompt = implementation.runRagInference(prompt, model, kontext);

                   JSObject ragCallData = new JSObject();
                   ragCallData.put("prompt", finalPrompt);
                   ragCallData.put("n_predict", call.getInt("n_predict", 150));
                   call.getData().put("model", model);
                   call.getData().put("prompt", finalPrompt);
                   call.getData().put("n_predict", 150);
                   this.runInference(call);
               } else {
                   call.reject(embeddingResult.getError().getMessage());
               }
            });
       };

        if (!isContextInitialized) {
            JSObject initParams = call.getData();
            getLlamaCpp().initContext(CONTEXT_ID, initParams, result -> {
                if (result.isSuccess()) {
                    isContextInitialized = true;
                    getEmbedding.run();
                } else {
                    call.reject("Llama-Kontext konnte nicht initialisiert werden: " + result.getError().getMessage());
                }
            });
        } else {
            getEmbedding.run();
        }
    }

//    @PluginMethod
//    public void addDocument(PluginCall call) {
//        String text = call.getString("text");
//        if (text == null || text.isEmpty()) {
//            call.reject("Bitte gib einen 'text' zum Hinzufügen an.");
//            return;
//        }
//
//        // 1. Asynchron das Embedding für den neuen Text erstellen
//        getLlamaCpp().embedding(CONTEXT_ID, text, null, embeddingResult -> {
//            if (!embeddingResult.isSuccess()) {
//                call.reject("Embedding fehlgeschlagen: " + embeddingResult.getError().getMessage());
//                return ;
//            }
//
//            // 2. Den Vektor aus dem Ergebnis extrahieren
//            Map<String, Object> data = embeddingResult.getData();
//            List<Double> embeddingList = (List<Double>) data.get("embedding");
//            float[] vector = new float[embeddingList.size()];
//            for (int i = 0; i < embeddingList.size(); i++) {
//                vector[i] = embeddingList.get(i).floatValue();
//            }
//
//            // 3. Den Chunk mit Text UND Vektor in ObjectBox speichern
//            Box<DocumentChunk> chunkBox = boxStore.boxFor(DocumentChunk.class);
//            DocumentChunk newChunk = new DocumentChunk(text, vector);
//            chunkBox.put(newChunk);
//
//            // 4. Erfolgreich antworten
//            JSObject result = new JSObject();
//            result.put("success", true);
//            result.put("id", newChunk.id);
//            call.resolve(result);
//        });
//    }

    @PluginMethod
    public void addDocument(PluginCall call) {
        String text = call.getString("text");
        String model = call.getString("model");
        if (text == null || text.isEmpty()) {
            call.reject("Bitte gib einen 'text' zum Hinzufügen an.");
            return;
        }

        Runnable createEmbedding = () -> {
            getLlamaCpp().embedding(CONTEXT_ID, text, null, (embeddingResult) -> {
                if (embeddingResult.isSuccess()){
                    System.out.println("Embedding erfolgreich!");
                    Map<String, Object> data = embeddingResult.getData();
                    List<Double> embeddingList = (List<Double>) data.get("embedding");
                    float[] vector = new float[embeddingList.size()];
                    for (int i = 0; i < embeddingList.size(); i++) {
                        vector[i] = embeddingList.get(i).floatValue();
                    }
                   long chunkId = implementation.addDocs(text, vector);
                    JSObject result = new JSObject();
                    result.put("success", true);
                    result.put("id", chunkId);
                    call.getData().put("model", model);
                    call.getData().put("id", chunkId);
                    call.resolve(result);
                } else {
                    System.out.println("Embedding fehlgeschlagen!");
                    call.reject(embeddingResult.getError().getMessage());
                }
            });
        };

        if (!isContextInitialized) {
            JSObject initParams = call.getData();
            getLlamaCpp().initContext(CONTEXT_ID, initParams, result -> {
                if (result.isSuccess()) {
                    isContextInitialized = true;
                    createEmbedding.run();
                } else {
                    call.reject("Llama-Kontext konnte nicht initialisiert werden: " + result.getError().getMessage());
                }
            });
        } else {
            createEmbedding.run();
        }


//        JSObject ret = new JSObject();
//        ret.put("id", implementation.addDocs(text));
//        ret.put("success", true);
//        call.resolve(ret);
    }

    @PluginMethod
    public void searchDocuments(PluginCall call) {
        String query = call.getString("query");
        if (query == null) {
            call.reject("Bitte gib eine 'query' für die Suche an.");
            return;
        }

        getLlamaCpp().embedding(CONTEXT_ID, query, null, (embeddingResult) -> {
           if (embeddingResult.isSuccess()){
               Map<String, Object> data = embeddingResult.getData();
               List<Double> embeddingList = (List<Double>) data.get("embedding");
               float[] queryVector = new float[embeddingList.size()];
               for(int i = 0; i < embeddingList.size(); i++) {
                   queryVector[i] = embeddingList.get(i).floatValue();
               }
               List<ObjectWithScore<DocumentChunk>> chunkList = implementation.searchDocs(query, queryVector);
               JSArray resultArray = new JSArray();
               for (ObjectWithScore<DocumentChunk> chunk : chunkList) {
                   JSObject chunkObject = new JSObject();
                   chunkObject.put("id", chunk.get().id);
                   chunkObject.put("content", chunk.get().content);
                   resultArray.put(chunkObject);
               }

               JSObject ret = new JSObject();
               ret.put("results", resultArray);
               call.resolve(ret);
           } else {
               call.reject(embeddingResult.getError().getMessage());
           }
        });

    }

//    @PluginMethod
//    public void searchDocuments(PluginCall call) {
//        String query = call.getString("query");
//        if (query == null) {
//            call.reject("Bitte gib eine 'query' für die Suche an.");
//            return;
//        }
//
//        List<DocumentChunk> results = implementation.searchDocuments(query);
//
//        // Konvetiere die Java-Liste in ein JSArray für Capacitor
//        JSArray resultArray = new JSArray();
//        for (DocumentChunk chunk : results) {
//            JSObject chunkObject = new JSObject();
//            chunkObject.put("id", chunk.id);
//            chunkObject.put("content", chunk.content);
//            resultArray.put(chunkObject);
//        }
//
//        JSObject ret = new JSObject();
//        ret.put("results", resultArray);
//        call.resolve(ret);
//
//    }
}
