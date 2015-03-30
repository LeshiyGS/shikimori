package org.shikimori.library.loaders.httpquery;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;

public class SQLBuilder {

    private StringBuilder sqlQuery, insert_values;
    private SQLiteDatabase DB = null;
    private String TABLE_NAME = null;
    private String GROUP_BY = null;
    private String ORDER_BY = null;
    private String LIMIT = null;
    private String[] COLUMNS;
    private int notFirstSet = 0;
    private boolean insert = false;
    private String _table = "";

    private static String WHERE = " WHERE ";
    private static String IN = " IN (";
    private static String NOTIN = " NOT IN (";
    public static String AND = " AND ";
    public static String OR = " OR ";
    public static String EQUAL = "=";
    public static String NOTEQUAL = "!=";
    public static String ISNULL = " IS NULL ";
    public static String NOTNULL = " NOT NULL ";
    public static String LESS = "<";
    public static String MORE = ">";
    public static String LIKE = " LIKE ";
    public static String NOTLIKE = " NOT LIKE ";
    public static String LESSANDEQUAL = "<=";
    public static String MOREANDEQUAL = ">=";
    private String type_search = AND;


    public SQLBuilder(SQLiteDatabase db) {
        clearSQlQuery();
        DB = db;
    }

    public SQLBuilder init() {
        SQLBuilder q = new SQLBuilder(DB);
        if (!_table.isEmpty())
            q.setTable(_table);
        return q;
    }

    public SQLBuilder setTable(String table) {
        _table = table;
        return this;
    }

    public void clearSQlQuery() {
        sqlQuery = new StringBuilder();
        insert_values = new StringBuilder();
    }


    public SQLBuilder delete(String table_name) {
        sqlQuery.append("DELETE FROM ");
        sqlQuery.append(table_name);
        return this;
    }

    public SQLBuilder delete() {
        return delete(_table);
    }

    /**
     * @return
     */
    public SQLBuilder select(String table_name, String[] columns) {
        TABLE_NAME = table_name;
        COLUMNS = columns;
        return this;
    }

    public SQLBuilder select(String[] columns) {
        return select(_table, columns);
    }

    public SQLBuilder insert(String table_name) {
        sqlQuery.append("INSERT INTO ");
        sqlQuery.append(table_name);
        sqlQuery.append(" (");
        insert = true;
        return this;
    }

    public SQLBuilder insert() {
        return insert(_table);
    }

    public SQLBuilder insertOrIgnore(String table_name) {
        sqlQuery.append("INSERT OR IGNORE INTO ");
        sqlQuery.append(table_name);
        sqlQuery.append(" (");
        insert = true;
        return this;
    }

    public SQLBuilder insertOrIgnore() {
        return insertOrIgnore(_table);
    }

    public SQLBuilder addInsertString(String string) {
        sqlQuery.append(string);
        return this;
    }

    public SQLBuilder update(String table_name) {
        sqlQuery.append("UPDATE ");
        sqlQuery.append(table_name);
        sqlQuery.append(" SET ");
        return this;
    }

    public SQLBuilder update() {
        return update(_table);
    }

    public SQLBuilder set(String column, String value) {
        if (notFirstSet == 0) {
            notFirstSet = 1;

            if (if_set_if_insert(column, value)) {
                _set(column, value);
            }
        } else {
            insert_values.append(",");
            sqlQuery.append(", ");
            if (if_set_if_insert(column, value)) {
                _set(column, value);
            }
        }
        return this;
    }

    private void _set(String column, String value) {
        sqlQuery.append(column).append("='")
                .append(escapeString(value)).append("' ");
    }

    private boolean if_set_if_insert(String column, String value) {
        if (insert) {
            sqlQuery.append(column);
            boolean nul = (!value.equals("null") && !value.equals("=null"));
            if (nul) insert_values.append("'");
            insert_values.append(escapeString(value));
            if (nul) insert_values.append("'");
            return false;
        }
        return true;
    }

    public SQLBuilder setNull(String column) {
        if (notFirstSet == 0) {
            notFirstSet = 1;
            if (if_set_if_insert(column, "null"))
                sqlQuery.append(column + "=null ");
        } else {
            insert_values.append(",");
            sqlQuery.append(", ");
            if (if_set_if_insert(column, "null"))
                sqlQuery.append(column + "=null ");
        }
        return this;
    }

    public SQLBuilder setWithoutQuotes(String column, String value) {
        if (notFirstSet == 0) {
            notFirstSet = 1;
            if (insert) {
                sqlQuery.append(column);
                insert_values.append(value);
            } else {
                sqlQuery.append(column).append("=").append(value);
            }
        } else {
            insert_values.append(",");
            sqlQuery.append(", ");
            if (insert) {
                sqlQuery.append(column);
                insert_values.append(value);
            } else {
                sqlQuery.append(column).append("=").append(value);
            }
        }
        return this;
    }

    // помошник построения where
    private SQLBuilder buildWhere(String type, String column, String compare, String value) {
        if (compare.equals(LIKE)) {
            sqlQuery.append(type).append(column).append(" ").append(LIKE)
                    .append(" '%").append(escapeString(value)).append("%' ")
            ;
        } else {
            sqlQuery.append(type).append(column).append(compare)
                    .append("'").append(escapeString(value)).append("' ")
            ;
        }
        return this;
    }

    // where
    public SQLBuilder where(String column, String compare, String value) {
        buildWhere(WHERE, column, compare, value);
        return this;
    }

    public SQLBuilder where(String column, boolean value) {
        String v = value ? "1" : "0";
        sqlQuery.append(WHERE).append(column).append(EQUAL)
                .append("'").append(v).append("' ")
        ;
        return this;
    }

    public SQLBuilder where(String column, String value) {
        sqlQuery.append(WHERE).append(column).append(EQUAL)
                .append("'").append(escapeString(value)).append("' ")
        ;
        return this;
    }

    public SQLBuilder where(String where) {
        sqlQuery.append(WHERE)
                .append(where);
        return this;
    }

    // IN AND NOT IN
    public SQLBuilder whereIn(String column, String value) {
        sqlQuery.append(WHERE).append(column).append(IN)
                .append(value).append(") ")
        ;
        return this;
    }

    public SQLBuilder whereNotIn(String column, String value) {
        sqlQuery.append(WHERE).append(column).append(NOTIN)
                .append(value).append(") ")
        ;
        return this;
    }

    public SQLBuilder andWhereIn(String column, String value) {
        sqlQuery.append(AND).append(column)
                .append(IN).append(value).append(") ")
        ;
        return this;
    }

    public SQLBuilder orWhereNotIn(String column, String value) {
        sqlQuery.append(OR).append(column)
                .append(NOTIN).append(value).append(") ")
        ;
        return this;
    }

    public SQLBuilder orWhereIn(String column, String value) {
        sqlQuery.append(OR).append(column)
                .append(IN).append(value).append(") ")
        ;
        return this;
    }

    public SQLBuilder andWhereNotIn(String column, String value) {
        sqlQuery.append(AND).append(column)
                .append(NOTIN).append(value).append(") ")
        ;
        return this;
    }

    // left join
    public SQLBuilder leftJoin(String table, String from, String to) {
        StringBuilder _table_name_build = new StringBuilder();
        _table_name_build.append(TABLE_NAME).append(" LEFT OUTER JOIN ").append(table)
                .append(" ON(").append(from).append("=").append(to)
                .append(") ")
        ;
        TABLE_NAME = _table_name_build.toString();
        return this;
    }

    // left join
    public SQLBuilder leftJoin(String table, String from) {
        StringBuilder _table_name_build = new StringBuilder();
        _table_name_build.append(TABLE_NAME).append(" LEFT OUTER JOIN ").append(table)
                .append(" ON(").append(from).append("=").append(table)
                .append("._id) ")
        ;
        TABLE_NAME = _table_name_build.toString();
        return this;
    }

    // AND
    public SQLBuilder andWhere(String column, String compare, String value) {
        buildWhere(AND, column, compare, value);
        return this;
    }

    public SQLBuilder andWhere(String column, String value) {
        buildWhere(AND, column, EQUAL, value);
        return this;
    }

    public SQLBuilder andWhere(String column, boolean value) {
        String v = value ? "1" : "0";
        sqlQuery.append(AND).append(column).append(EQUAL)
                .append("'").append(v).append("' ")
        ;
        return this;
    }

    public SQLBuilder andWhere(String where) {
        sqlQuery.append(AND).append(where);
        return this;
    }

    // search
    public SQLBuilder setTypeSearch(String type) {
        type_search = type;
        return this;
    }

    public SQLBuilder search(String column, String text) {
        String query_search = prepareSearch(column, text);
        if (query_search != null) {
            where(query_search);
        }
        return this;
    }

    public SQLBuilder andSearch(String column, String text) {
        String query_search = prepareSearch(column, text);
        if (query_search != null) {
            andWhere(query_search);
        }
        return this;
    }

    public SQLBuilder orSearch(String column, String text) {
        String query_search = prepareSearch(column, text);
        if (query_search != null) {
            orWhere(query_search);
        }
        return this;
    }

    @SuppressLint("DefaultLocale")
    private String prepareSearch(String column, String text) {
        text = escapeString(text);
        text = text.trim();
        if (text.isEmpty())
            return null;

        String[] search_list = text.split("\\s+");
        ArrayList<String> query_search = new ArrayList<String>();
        //ArrayList<String> small_query_search = new ArrayList<String>();
        String first_later = "";
        //String small_row = "";
        for (int i = 0; i < search_list.length; i++) {
            //small_row   = search_list[i].toLowerCase();
            first_later = search_list[i].substring(0, 1).toUpperCase();
            if (search_list[i].length() > 1) {
                first_later += search_list[i].substring(1);
                query_search.add(first_later);
            }
            //small_query_search.add(small_row);
            //query_search.add(small_row);
        }
        if (query_search.size() > 0) {
            String _prepare = "";
            //String _prepare2 = "";
            for (String search_text : query_search) {
                //if(search_text.length()>1){
                _prepare += type_search + " (" + column + LIKE + "'%" + search_text + "%' " +
                        OR + column + LIKE + "'%" + search_text.toLowerCase() + "%') "
                ;
                //_prepare2 += "OR "+column + LIKE + "'%"+search_text+"%' "+
                //		OR + column + LIKE + "'%"+search_text.toLowerCase()+"%' "
                //;
                //}
            }
            //((name LIKE '%Ano%' OR name LIKE '%ano%') AND (name LIKE '%Natsu%' OR name LIKE '%natsu%') AND (name LIKE '%De%' OR name LIKE '%de%'))
            //if(_prepare.length()>1)
            return "(" + _prepare.substring(4) + ")";
            //+" "+_prepare2+"
        }
        return null;
    }

    // OR
    public SQLBuilder orWhere(String column, String compare, String value) {
        buildWhere(OR, column, compare, value);
        return this;
    }

    public SQLBuilder orWhere(String column, Boolean value) {
        String v = value ? "1" : "0";
        sqlQuery.append(OR).append(column).append(EQUAL)
                .append("'").append(v).append("' ")
        ;
        return this;
    }

    public SQLBuilder orWhere(String where) {
        sqlQuery.append(OR).append(where);
        return this;
    }

    // group
    public SQLBuilder groupBy(String groupBy) {
        if (TABLE_NAME != null)
            GROUP_BY = groupBy;
        else
            sqlQuery.append(" GROUP BY ").append(groupBy);
        return this;
    }

    // order
    public SQLBuilder orderBy(String orderBy) {
        if (TABLE_NAME != null)
            ORDER_BY = orderBy;
        else
            sqlQuery.append(" ORDER BY ").append(orderBy);
        return this;
    }

    // limit
    public SQLBuilder limit(String limit) {
        if (TABLE_NAME != null)
            LIMIT = limit;
        else
            sqlQuery.append(" LIMIT ").append(limit);
        return this;
    }

    public String getSqlQuery() {
        String res = "";
        if (insert)
            sqlQuery.append(") VALUES (").append(insert_values.toString()).append(")");
        else
            res += "SELECT " + COLUMNS.toString() + " FROM " + _table + " ";

        res += sqlQuery.toString();
        return res;
    }

    public void execute() {

        if (insert) {
            sqlQuery.append(") VALUES (").append(insert_values.toString()).append(")");
            DB.execSQL(sqlQuery.toString());
        } else {
            DB.execSQL(sqlQuery.toString());
        }
        clear();
    }

    public Cursor getCursor() {
        //h.log(sqlQuery.toString());
        //sqlQuery.delete(0, 6);
        String sql = sqlQuery.toString().trim();
        if (!TextUtils.isEmpty(sql)) {
            String[] arr = sql.split(" ", 2);
            if (arr.length > 1) {
                sql = arr[1];
            }
            sql = sql.trim();
            if (sql.startsWith("WHERE"))
                sql = sql.replaceFirst("WHERE", "");

        }


//		h.log("sqltest: "+sql);

        Cursor cur = DB.query(
                TABLE_NAME,
                COLUMNS,
                sql,
                null,
                GROUP_BY,
                null,
                ORDER_BY,
                LIMIT);
        cur.moveToFirst();
        clear();
        return cur;
    }

    String escapeString(String sqlString){
        return  sqlString;
    }

    public Cursor getItemById(String id) {
        Cursor _test = where("_id", EQUAL, id).getCursor();
        _test.moveToFirst();
        return _test;
    }

    private void clear() {
        TABLE_NAME = null;
        GROUP_BY = null;
        ORDER_BY = null;
        COLUMNS = null;
        LIMIT = null;
        insert = false;
        notFirstSet = 0;
        clearSQlQuery();
    }

}
