package com.chatIA.chatbotIA.database_SQLITE;

/*
public class ImageDatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public ImageDatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Save or update the image URI in the database
    public long saveImageUri(String uri) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_URI, uri);

        // Check if there is already an image URI in the database
        if (getImageUri() != null) {
            return database.update(DatabaseHelper.TABLE_IMAGES, values, null, null);
        } else {
            return database.insert(DatabaseHelper.TABLE_IMAGES, null, values);
        }
    }

    // Get the current image URI from the database
    public String getImageUri() {
        String[] columns = { DatabaseHelper.COLUMN_URI };
        Cursor cursor = database.query(DatabaseHelper.TABLE_IMAGES, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String uri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URI));
            cursor.close();
            return uri;
        } else {
            return null;
        }
    }
}
*/