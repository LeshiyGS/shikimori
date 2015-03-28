package org.shikimori.client.pull;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Феофилактов on 25.12.2014.
 */
public class WrapperUiTool {

    private View view;
    private int wrapperLayout;
    int positioView;
    private ViewGroup layout, parent;
    private SettingsBeforeSet settings;

    /**
     *
     * @param view
     * @param wrapperLayout это враппер, который обволакивает нужную вьюху
     */
    public WrapperUiTool(View view, @LayoutRes int wrapperLayout) {
        this.view = view;
        this.wrapperLayout = wrapperLayout;
        wrap();
    }

    /**
     *
     * @param view
     * @param layout это враппер, который обволакивает нужную вьюху
     */
    public WrapperUiTool(View view, ViewGroup layout) {
        this.view = view;
        this.layout = layout;
        wrap();
    }

    /**
     * Добавляем в контейнер с конкретным id
     *
     * @param inputId
     * @return
     */
    public WrapperUiTool insertAtId(@IdRes int inputId) {
        if (settings != null)
            settings.settings(view);
        ((ViewGroup) layout.findViewById(inputId)).addView(view);
        rebuildParent();
        return this;
    }

    /**
     * Добавляем в конкретную позицию во врапере
     *
     * @param position
     * @return
     */
    public WrapperUiTool insertAtPosition(int position) {
        if (settings != null)
            settings.settings(view);
        rebuildParent();
        layout.addView(view, position);
        return this;
    }

    public WrapperUiTool insert() {
        insertAtPosition(0);
        return this;
    }

    /**
     * Дополнительная настройка перед вставкой вьюхи
     *
     * @param settings
     * @return
     */
    public WrapperUiTool setOnSettingsBeforeSet(SettingsBeforeSet settings) {
        this.settings = settings;
        return this;
    }

    public View getView() {
        return layout;
    }

    /**
     * Удаляем оригинальное вью и вместо него ставим враппер
     */
    private void rebuildParent() {
        if (parent != null) {
            parent.removeViewAt(positioView);
            parent.addView(layout, positioView);
        }
    }

    /**
     * Подготовка
     */
    private void wrap() {
        parent = ((ViewGroup) view.getParent());
        if (parent != null) {
            positioView = parent.indexOfChild(view);
            initLayout(parent);
        } else {
            initLayout(null);
        }
    }

    private void initLayout(ViewGroup root) {
        if (layout == null && wrapperLayout != 0) {
            LayoutInflater inf = LayoutInflater.from(view.getContext());
            layout = (ViewGroup) inf.inflate(wrapperLayout, root, false);
        }
    }

    public interface SettingsBeforeSet {
        public void settings(View v);
    }
}
