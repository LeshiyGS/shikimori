//package org.shikimori.library.loaders.httpquery;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
//
///**
// * Created by Феофилактов on 27.03.2015.
// */
//public class DbBase extends SQLiteOpenHelper {
//
//    Context c;
//    public Cursor data;
//    public SQLBuilder Core = null;
//    private String table = null;
//    public static final String ZERO = "0";
//    public static final String ONE = "1";
//    public static String NEW = "new";
//    public static String ID = "_id";
//    // запросы к базе
//    public static String EQUAL = "=";
//    public static String NOTEQUAL = "!=";
//    public static String ISNULL = " IS NULL ";
//    public static String NOTNULL = " NOT NULL ";
//    public static String LESS = "<";
//    public static String MORE = ">";
//    public static String LIKE = " LIKE ";
//    public static String LESSANDEQUAL = "<=";
//    public static String MOREANDEQUAL = ">=";
//
//
//    public DbBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//        c = context;
//    }
//
//    public Context getContext() {
//        return c;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }
//
//    public static String getColumsNames(SQLiteDatabase db, String table) {
//        String names = "";
//        Cursor ti = db.rawQuery("PRAGMA table_info(" + table + ")", null);
//        if (ti.moveToFirst()) {
//            do {
//                names += " " + ti.getString(1);
//            } while (ti.moveToNext());
//        }
//        ti.close();
//        return names;
//    }
//
//    public static String getTableNames(SQLiteDatabase db) {
//        String result = "";
//        try {
//            StringBuilder sb = new StringBuilder();
//            sb.append("SELECT name FROM sqlite_master ");
//            sb.append("WHERE type IN ('table','view') AND name NOT LIKE 'sqlite_%' ");
//            sb.append("UNION ALL ");
//            sb.append("SELECT name FROM sqlite_temp_master ");
//            sb.append("WHERE type IN ('table','view') ");
//            sb.append("ORDER BY 1");
//
//            Cursor c = db.rawQuery(sb.toString(), null);
//            c.moveToFirst();
//
//            while (c.moveToNext()) {
//                result += " " + c.getString(c.getColumnIndex("name"));
//            }
//            c.close();
//        } catch (SQLiteException e) {
//        }
//        return result;
//    }
//
//
//    public static String getValue(Cursor cursor, String field) {
//        return cursor.getString(cursor.getColumnIndexOrThrow(field));
//    }
//
//    public static int getInt(Cursor cursor, String field) {
//        return cursor.getInt(cursor.getColumnIndexOrThrow(field));
//    }
//}
