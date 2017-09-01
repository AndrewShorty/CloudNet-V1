package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.document.Document;

import java.util.Collection;
import java.util.concurrent.FutureTask;

/**
 * Created by Tareko on 01.07.2017.
 */
public interface Database {

    String UNIQUE_NAME_KEY = "_database_id_unique";

    Database loadDocuments();
    Collection<Document> getDocuments();
    Document getDocument(String name);
    Database insert(Document...documents);
    Database delete(String name);
    Database delete(Document document);
    Document load(String name);
    boolean contains(Document document);
    boolean contains(String name);
    int size();
    boolean containsDoc(String name);

    Database insertAsync(Document...documents);
    Database deleteAsync(String name);

    FutureTask<Document> getDocumentAsync(String name);

    static DatabaseDocument createEmptyDocument(String name)
    {
        return new DatabaseDocument(name);
    }
}