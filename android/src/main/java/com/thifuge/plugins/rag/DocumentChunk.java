package com.thifuge.plugins.rag;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

// Die @Entity-Annotation markiert diese Klasse als speicherbares Objekt für ObjectBox
@Entity
public class DocumentChunk {

    // Jede Entität braucht eine ID. ObjectBox vergibt sie automatisch.
    @Id
    public long id;

    // Wir speichern den Inhalt des Text-Chunks.
    // @Index macht die Suche nach diesem Feld schneller.
    @Index
    public String content;



    // Standard-Konstrutor wird von ObjectBox benötigt.
    public DocumentChunk() {}

    public DocumentChunk(String content) {
        this.content = content;
    }
}
