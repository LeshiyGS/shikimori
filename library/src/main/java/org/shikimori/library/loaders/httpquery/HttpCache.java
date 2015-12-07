//package org.shikimori.library.loaders.httpquery;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.database.sqlite.SQLiteDatabase;
//import android.widget.TextView;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// */
//public class HttpCache extends DbBase{
//
//    public static SQLiteDatabase DB;
//    public Cursor data;
//    public SQLBuilder Core = null;
//    private String table = null;
//    //public db dbHelper = null;
//    //public static SQLiteDatabase DB = null;
//    public static final String DATABASE_NAME = "httpcache";
//    public static final int DATABASE_VERSION = 2; // old 7
//
//    public void open_db() {
//        if (!isOpen())
//            DB = this.getWritableDatabase();
//        Core = new SQLBuilder(DB).setTable(table);
//    }
//
//    public boolean isOpen() {
//        return DB != null && DB.isOpen();
//    }
//
//    public SQLiteDatabase getDb() {
//        return DB;
//    }
//
//    public HttpCache(Context context, String table_name) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        table = table_name;
//        open_db();
//    }
//
//    public void closeCursor() {
//        if(data!=null)
//            data.close();
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        DbCache.create(db);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        DbCache.update(db, oldVersion, newVersion);
//    }
//
//    public static void getMSGExeption(Context c, Exception e, String title) {
//        String errors = e.toString();
//        Dialog d = new Dialog(c);
//        d.setTitle(title);
//        TextView tv = new TextView(c);
//        tv.setText(errors);
//        d.setContentView(tv);
//        d.show();
//    }
//
//    public boolean first() {
//        return data.moveToFirst();
//    }
//
//    public SQLBuilder getValuesFromJson(JSONObject obj, SQLBuilder sql, String[] columns) {
//        try {
//            for (int i = 0; i < columns.length; i++) {
//                if (obj.has(columns[i]))
//                    sql.set(columns[i], obj.getString(columns[i]));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return sql;
//    }
//
//    public boolean dbIsEmpty() {
//        Cursor _cur = Core.init().select(table, new String[]{"_id"})
//                //.where(DELETED, SQLBuilder.NOTEQUAL, db.ONE)
//                .limit("1")
//                .getCursor();
//
//        boolean notempty = _cur.moveToFirst();
//        _cur.close();
//        return !notempty;
//    }
//
//    public String getId() {
//        return getValue(data, ID);
//    }
//
//    public Cursor getData() {
//        return data;
//    }
//
//    public void deleteFromTable(String ids) {
//        Core.init().delete()
//                .whereIn(ID, ids)
//                .execute();
//    }
//
//    public boolean ifExist(String id) {
//        return DatabaseUtils.longForQuery(getDb(), "select count(*) from " + table + " where _ID=? limit 1", new String[]{id}) > 0;
//    }
//
//}
