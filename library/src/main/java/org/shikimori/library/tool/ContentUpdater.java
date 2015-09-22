package org.shikimori.library.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Феофилактов on 27.01.2015.
 */
public class ContentUpdater {

    private static HashMap<Class, UpdateContentListener> listListener = new HashMap<>();

    public interface UpdateContentListener{
        public void contentUpdated();
    }

    public static void registerListener(UpdateContentListener listener){
        if(!listListener.containsKey(listener.getClass()))
            listListener.put(listener.getClass(), listener);
    }

    public static void unregisterListener(UpdateContentListener listener){
        listListener.values().remove(listener);
    }

    public static void unregisterListener(Class id){
        listListener.remove(id);
    }
}
