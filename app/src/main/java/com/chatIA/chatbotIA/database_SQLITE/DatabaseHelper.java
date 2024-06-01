package com.chatIA.chatbotIA.database_SQLITE;

/*
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "images.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_IMAGES = "images";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_URI = "uri";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_IMAGES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_URI + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }
}
*/