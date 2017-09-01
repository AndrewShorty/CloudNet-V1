package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.document.Document;

/**
 * Created by Tareko on 01.07.2017.
 */
public interface DatabaseResult {

    Document get(DatabaseCommand databaseQuery, Document key);

}