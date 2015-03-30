package org.shikimori.library.loaders.httpquery;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class DbCache extends HttpCache {
    public static final String TABLE = "chache_table";
    public static final String ID = "_id";
    public static final String QUERY_ROW  = "query_row";
    public static final String QUERY_DATA = "query_data";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    String[] columns = new String[]{
            ID, QUERY_ROW, QUERY_DATA,
            CREATED_AT, UPDATED_AT
    };

    /**
     * Открытие db
     *
     * @param context
     */
    public DbCache(Context context) {
        super(context, TABLE);
    }

    private SQLBuilder select() {
        return Core.init().select(columns);
    }

    public boolean first() {
        return data.moveToFirst();
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
    }

    public static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        QUERY_ROW + " TEXT, " +
                        QUERY_DATA + " TEXT, " +
                        CREATED_AT + " INTEGER, " +
                        UPDATED_AT + " INTEGER" +
                        "); "
        );
        createIndex(db);
    }


    public static void update(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop(db);
        create(db);
        createIndex(db);
    }

    private static void createIndex(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS IDX_QUERY_ROW on " + TABLE + " (" + QUERY_ROW + ")");
    }

    /**
     * @return getValues
     */
    public String getId() {
        return getValue(data, ID);
    }

    public String getCreatedAt() {
        return getValue(data, CREATED_AT);
    }

    public String getUpdatedAt() {
        return getValue(data, UPDATED_AT);
    }

    public String getQueryRow() {
        return getValue(data, QUERY_ROW);
    }

    public String getQueryData() {
        return getValue(data, QUERY_DATA);
    }

    public DbCache findById(String id) {
        closeCursor();
        data = select().getItemById(id);
        data.moveToFirst();
        return this;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Cursor getData(String queryRow){
        clearOldCache();
        return select()
                .where(QUERY_ROW, DatabaseUtils.sqlEscapeString(queryRow))
                .getCursor();
    }

    void clearOldCache(){
        Core.init().delete()
            .where(CREATED_AT, LESS, String.valueOf(System.currentTimeMillis()))
            .execute();
    }

    public void delete(String queryRow){
        Core.init().delete()
            .where(QUERY_ROW, DatabaseUtils.sqlEscapeString(queryRow))
            .execute();
    }

    public void setData(String queryRow, String queryData, long timeCache){
        delete(queryRow);
        Core.init().insert()
            .set(QUERY_ROW, DatabaseUtils.sqlEscapeString(queryRow))
            .set(QUERY_DATA, DatabaseUtils.sqlEscapeString(queryData))
            .set(CREATED_AT, String.valueOf(System.currentTimeMillis() + timeCache))
            .execute();
    }

    public void invalidateCache(String prefix) {
        Core.init().delete()
            .where(QUERY_ROW, LIKE, DatabaseUtils.sqlEscapeString(prefix))
            .execute();
    }
}