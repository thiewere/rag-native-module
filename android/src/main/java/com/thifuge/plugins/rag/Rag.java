package com.thifuge.plugins.rag;

import com.getcapacitor.Logger;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

import ai.annadata.plugin.capacitor.LlamaCpp;
import io.objectbox.query.ObjectWithScore;
import io.objectbox.query.Query;

public class Rag {

    private BoxStore boxStore;
    private LlamaCpp llamaCpp;

    // ======================================================================
    // 1. Definiere das Dummy-Dokument (unsere Wissensdatenbank) hier
    // ======================================================================
    private static final String DUMMY_DOKUMENT = "Protokoll: Projekt Aurora\n" +
            "Datum: 05. Oktober 2025\n" +
            "Leitung: Dr. Elena Schmidt\n" +
            "Status: Aktiv\n" +
            "Zusammenfassung: Projekt Aurora ist eine interne Forschungsinitiative zur Entwicklung " +
            "eines neuartigen Quanten-Relais namens QX-7. Das Relais soll die " +
            "Kommunikationslatenz in suborbitalen Netzwerken um 90% reduzieren. " +
            "Das Kernteam besteht aus Dr. Schmidt, sowie den Ingenieuren Ben Carter und Maria Garcia.";

  public Rag(BoxStore boxStore, LlamaCpp llamaCpp) {
      this.boxStore = boxStore;
      this.llamaCpp = llamaCpp;
  }

  public long addDocs(String text, float[] embedding) {

      Logger.info("QueryVector:", embedding.toString());
      DocumentChunk documentChunk = new DocumentChunk(text, embedding);
      Box<DocumentChunk> chunkBox = boxStore.boxFor(DocumentChunk.class);
      long newId = chunkBox.put(documentChunk);
      return newId;
  }

  public List<ObjectWithScore<DocumentChunk>> searchDocs(String query, float[] queryVector) {
      if (query == null || query.isEmpty()) {
          return java.util.Collections.emptyList();
      }

//      float[] queryVector = createEmbedding(query); // Query in Vector umwandeln
      Logger.info("QueryVector:", queryVector.toString());
      Box<DocumentChunk> chunkBox = boxStore.boxFor(DocumentChunk.class);
//      Query<DocumentChunk> queryBuilder = chunkBox.query(DocumentChunk_.content.contains(query)).build();
      Query<DocumentChunk> queryBuilder = chunkBox
              .query(DocumentChunk_.embedding.nearestNeighbors(queryVector, 2))
              .build();
      List<ObjectWithScore<DocumentChunk>> results = queryBuilder.findWithScores();
//      List<DocumentChunk> result = queryBuilder.find();

      return results;
  }
    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }

    // Hier in Rag.java
    public int addTwoNumbers(int value1, int value2) {

        Logger.info("Summe: ", Integer.toString(value1+value2));
        return value1+value2;
    }

    public String runRagInference(String prompt, String model, String kontext) {
        Logger.info("Entry in RAG-Inference: \n Prompt: " + prompt + "\n Model: " + model);
        
        // --- HIER PASSIERT DIE RAG-MAGIE ---
        // a) Retrieval: Wir "holen" unser Dokument (in diesem Fall ist es fix).
        //    Später würde hier die ObjectBox-Suche stattfinden.
//        String kontext = DUMMY_DOKUMENT; //DUMMY-KONTEXT
//        List<DocumentChunk> kontext = this.searchDocuments(prompt);


        // b) Augmentation: Wir bauen den finalen Prompt für das LLM.
        String finalPrompt = "Kontext:\n---\n" +
                    kontext +
                    "\n---\n" +
                    "Basierend NUR auf dem oben stehenden Kontext, beantworte die folgende Frage.\n" +
                    "Frage: " + prompt; 
        
        // c) Generation: Wir verwenden unsere bestehende Logik, aber mit dem neuen Prompt.
        //    Wir erstellen ein neues JSObject, um den alten 'call' nicht zu verändern.
        return finalPrompt; 
    }

    /**
     * Fügt einen neuen Text-Chunk zur Datenbank hinzu.
     * @param text Der Inhalt des Dokuments/Chunks.
     * @return Die ID des neu erstellten Objekts.
     */
//    public  long addDocument(String text) {
//        Logger.info("Entry in AddDocument Method!!!!!!!!!!!!!!!!");
//
//        if (this.boxStore == null || this.llamaCpp == null) return -1;
//
//        // 1. Embedding für den Text erzeugen (Dies ist ein synchroner Platzhalter)
//        // HINWEIS: Die echte `embedding`-Methode ist asynchron!
//        // Wir müssen dies in RagPlugin.java mit einem Callback handhaben.
//        // Fürs Erste simulieren wir es hier.
//        float[] vector = createEmbedding(text); // Platzhalter für den Embedding-Aufruf
//
//
//        // Hole die "Box" für unsere DocumentChunk-Entität
//        Box<DocumentChunk> chunkBox = this.boxStore.boxFor(DocumentChunk.class);
//
//        // Erstelle ein neues Chunk-Objekt und speichere es
//        DocumentChunk newChunk = new DocumentChunk(text, vector);
//        chunkBox.put(newChunk);
//        return newChunk.id;
//    }

//    /**
//     * Durchsucht die Datenbank nach Chunks, die den Suchbegriff enthalten.
//     * @param query Der Suchbegriff.
//     * @return Eine Liste der passenden DocumentChunk-Objekte.
//     */
//    public List<DocumentChunk> searchDocuments(String query) {
//        if (this.boxStore == null || this.llamaCpp == null) return java.util.Collections.emptyList();
//
//        // 1. Embedding für die Suchanfrage erzeugen
//        float[] queryVector = createEmbedding(query); // Platzhalter
//
//        Box<DocumentChunk> chunkBox = this.boxStore.boxFor(DocumentChunk.class);
//
//        // Führt eine einfache Textsuche durch (noch keine Vektorsuche)
////        return chunkBox.query(DocumentChunk_.content.contains(query, io.objectbox.query.QueryBuilder.StringOrder.CASE_INSENSITIVE))
////                .build()
////                .find();
////        return chunkBox.query(DocumentChunk_.embedding.findNearest(queryVector, 3)).build().find();
//
//        return chunkBox.query(DocumentChunk_.embedding.nearestNeighbors(queryVector, 3)).build();
//    }

    // Platzhalter-Methode, die wir später durch den echten Aufruf ersetzen
    private float[] createEmbedding(String text) {
        // Hier würde der Aufruf an `llamaCpp.embedding(...)` stattfinden.
        // Da dieser asynchron ist, muss die eigentliche Logik in RagPlugin.java
        // mit Callbacks umgesetzt werden. Wir simulieren es fürs Erste.
        // In der echten Implementierung würde diese Methode in RagPlugin.java leben.

        // Erzeuge einen Dummy-Vektor der richtigen Dimension
        float[] dummyVector = new float[384];
        // Fülle ihn mit ein paar Werten, um Fehler zu verweiden
        for(int i = 0; i < 384; i++) {
            dummyVector[i] = (float) Math.random();
        }
        return dummyVector;
    }


}
