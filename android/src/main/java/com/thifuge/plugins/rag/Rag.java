package com.thifuge.plugins.rag;

import com.getcapacitor.Logger;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class Rag {

    private BoxStore boxStore;

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

  public Rag(BoxStore boxStore) {
      this.boxStore = boxStore;
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

    public String runRagInference(String prompt, String model) {
        Logger.info("Entry in RAG-Inference: \n Prompt: " + prompt + "\n Model: " + model);
        
        // --- HIER PASSIERT DIE RAG-MAGIE ---
        // a) Retrieval: Wir "holen" unser Dokument (in diesem Fall ist es fix).
        //    Später würde hier die ObjectBox-Suche stattfinden.
        String kontext = DUMMY_DOKUMENT;

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
    public  long addDocument(String text) {
        Logger.info("Entry in AddDocument Method!!!!!!!!!!!!!!!!");

        if (this.boxStore == null) return -1;

        // Hole die "Box" für unsere DocumentChunk-Entität
        Box<DocumentChunk> chunkBox = this.boxStore.boxFor(DocumentChunk.class);

        // Erstelle ein neues Chunk-Objekt und speichere es
        DocumentChunk newChunk = new DocumentChunk(text);
        chunkBox.put(newChunk);
        return newChunk.id;
    }

    /**
     * Durchsucht die Datenbank nach Chunks, die den Suchbegriff enthalten.
     * @param query Der Suchbegriff.
     * @return Eine Liste der passenden DocumentChunk-Objekte.
     */
    public List<DocumentChunk> searchDocuments(String query) {
        if (this.boxStore == null) return java.util.Collections.emptyList();
        Box<DocumentChunk> chunkBox = this.boxStore.boxFor(DocumentChunk.class);

        // Führt eine einfache Textsuche durch (noch keine Vektorsuche)
        return chunkBox.query(DocumentChunk_.content.contains(query, io.objectbox.query.QueryBuilder.StringOrder.CASE_INSENSITIVE))
                .build()
                .find();
    }



}
