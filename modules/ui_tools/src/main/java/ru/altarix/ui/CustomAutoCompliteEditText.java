package ru.altarix.ui;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ListAdapter;

import java.util.ArrayList;

import ru.altarix.ui.tool.ProxyAdapter;
import ru.altarix.ui.tool.h;


/**
 * Created by Владимир on 11.08.2014.
 */
public class CustomAutoCompliteEditText extends BaseCustomEditText<AppCompatAutoCompleteTextView> {
    private View pbLoader;
    private CountDownTimer coldawn;
    private ProxyAdapter proxy;

    public CustomAutoCompliteEditText(Context context) {
        this(context, null);
    }

    public CustomAutoCompliteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.altarix_ui_custom_autocomplete_edit_text;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        pbLoader = this.findViewById(R.id.pbLoader);
    }

    public View getLoader(){
        return pbLoader;
    }

    public void showLoader(boolean show){
        if(show){
            if(pbLoader.getVisibility()!=VISIBLE)
                pbLoader.setVisibility(VISIBLE);
        } else {
            if(pbLoader.getVisibility()!=GONE)
                pbLoader.setVisibility(GONE);
        }
    }

    public interface ServerListener{
        /**
         * If no need change set return null
         * @return
         */
        public void canQueryToServer(String s);
    }

    public void setColdawnInterval(final ServerListener serverListener) {
        setColdawnInterval(serverListener, 1000);
    }
    public void setColdawnInterval(final ServerListener serverListener, final int time){

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                // если строка более 3 символов
                if(s.length() > 3){
                    // канселим предыдущий отсчет
                    if(coldawn!=null)
                        coldawn.cancel();
                    // ставим секунду
                    coldawn = new CountDownTimer(time, time) {

                        public void onTick(long millisUntilFinished) {
                            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            // разрешаем делать запрос на сервер
                            serverListener.canQueryToServer(s.toString());
                        }
                    }.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public <T extends ListAdapter & Filterable> void setAdapter(T adapter){
        setAdapter(adapter, false, null);
    }

    public <T extends ListAdapter & Filterable> void setAdapter(T adapter, ProxyAdapter.ProxyFilter filter){
        setAdapter(adapter, false, filter);
    }
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter, boolean notUseProxyAdapter, ProxyAdapter.ProxyFilter filter){
        if(notUseProxyAdapter){
            etText.setAdapter(adapter);
            return;
        }

        proxy = new ProxyAdapter(getContext(), adapter);
        proxy.setProxyFilter(filter);
        etText.setAdapter(proxy);
    }


    public void setProxyFilter(ProxyAdapter.ProxyFilter filter){
        proxy.setProxyFilter(filter);
    }

}
