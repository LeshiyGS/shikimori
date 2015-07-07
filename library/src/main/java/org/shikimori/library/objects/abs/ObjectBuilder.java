package org.shikimori.library.objects.abs;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gars on 2/11/14.
 */
public class ObjectBuilder<T extends JsonParseable> {

    /**
     * list of items
     */
    public List<T> list = Collections.emptyList();
    /**
     * Advance check listener while build item
     */
    protected AdvanceCheck checklistener;
    /**
     * Class of item
     */
    private Class<T> objCls;

    /**
     * @param data   string or JSONArray
     * @param objCls
     */
    public ObjectBuilder(JSONArray data, Class<T> objCls) {
        getList(data, objCls);
    }

    public List<T> getList(JSONArray data, Class<T> objCls){
        addToList(new ArrayList<T>(data !=null ? data.length() : 0), data, objCls);
        return list;
    }

    public void addToList(@NonNull List<T> list, JSONArray data, Class<T> objCls){
        addToList(list, data, objCls, null);
    }

    public void addToList(@NonNull List<T> list, JSONArray data, Class<T> objCls, AdvanceCheck check){
        this.objCls = objCls;
        this.list = list;
        checklistener = check;
        buildData(data);
    }

    /**
     * @param array
     * @param objCls
     * @param check  дополнительная проверка для
     *               элемента при построении
     */
    public ObjectBuilder(JSONArray array, Class<T> objCls, AdvanceCheck check) {
        this.objCls = objCls;
        checklistener = check;
        list = new ArrayList<>(array.length());
        buildData(array);
    }

    /**
     * Generate objects
     *
     * @param array
     */
//    public void prepareData(JSONArray array) {
//        if (array != null) {
////            try {
////                Method method = objCls.getMethod("create", JSONObject.class);
////                buildData(method, array);
////            } catch (NoSuchMethodException e) {
//                buildData(array);
////            }
//        }
//    }

//    /**
//     * Use CREATOR for build item
//     *
//     * @param array
//     */
//    void buildData(JSONArray array) {
//        try {
//            T.Creator<T> creator = (T.Creator<T>) objCls.getField(CREATOR_FIELD).get(null);
//            for (int i = 0, count = array.length(); i < count; i++) {
//                T item = creator.createFromJson(array.getJSONObject(i));
//
//                if (checklistener != null && checklistener.check(item, i))
//                    continue;
//                list.add(item);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Use static method create for build item
     *
     * @param array
     */
    void buildData(JSONArray array) {
        for (int i = 0, count = array.length(); i < count; i++) {
            try {
                T item = (T) objCls.newInstance().createFromJson(array.optJSONObject(i));
                if (checklistener != null && checklistener.check(item, i))
                    continue;
                list.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Добавляем к уже имеющемся
     *
     * @param obj
     */
    public void addData(T obj) {
        list.add(obj);
    }

    /**
     * Add to list new list
     *
     * @param array
     */
    public void addData(JSONArray array) {
        buildData(array);
    }

    public List<T> getDataList() {
        return list;
    }

    public interface AdvanceCheck<T> {
        public boolean check(T item, int position);
    }

}
