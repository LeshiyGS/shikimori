package ru.altarix.ui;

import android.content.Context;
import android.os.CountDownTimer;
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
public class CustomAutoCompliteEditText extends CustomViewBase {
    private AutoCompleteTextView etText;
    private String mHint;
    private int mType;
    private String mDigits;
    private View pbLoader;
    private CountDownTimer coldawn;
    private ProxyAdapter proxy;
    private AnimateLabel animateLabel;

    public CustomAutoCompliteEditText(Context context) {
        this(context, null);
    }

    public CustomAutoCompliteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomAutoCompliteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs, R.layout.altarix_ui_custom_autocomplete_edit_text);
        if(typedArray!=null){
            try {
                mHint      = getContext().getString(attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "hint",0));
                mType      = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "inputType", 0x00000001);
                mDigits    = typedArray.getString(R.styleable.AltarixUiDesclareStyle_uiRegexp);
            } finally {
                typedArray.recycle();
            }
        }
        if(mText == null)
            mText = "";

        if(mHint == null)
            mHint = "";

        etText   = (AutoCompleteTextView)this.findViewById(R.id.acText);
        animateLabel = new AnimateLabel(mContext, tvLabel, mHideLabelIfEmpty, etText);
        if(mHideLabelIfEmpty)
            etText.addTextChangedListener(animateLabel);
        pbLoader = this.findViewById(R.id.pbLoader);

        etText.setText(mText);
        etText.setHint(mHint);

        if(mType!=0)
            etText.setInputType(mType);

        if(mDigits!=null){
            setRegexp(mDigits);
        }

        h.setFont(mContext, this);
    }

    /**
     *
     * @param regexp
     */
    public void setRegexp(final String regexp){
        /**
         * Regexp valid input letters
         */
        etText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0 || before > start)
                    return;

                //char currentChar = s.charAt(start);
                String current_text = etText.getText().toString();
//                    Log.d("current_text: ", current_text);
//                    Log.d("current_reg: ", ""+mDigits);
                if(!h.match(regexp, current_text)) {

                    StringBuilder dialled_nos_builder = new StringBuilder(current_text);
                    dialled_nos_builder.delete(start, start+count);
                    etText.setText(dialled_nos_builder.toString());
                    etText.setSelection(start);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    public AutoCompleteTextView getAutoCompleteTv(){
        return etText;
    }

    public String getText(){
        return etText.getText().toString();
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

    public void setText(int text){
        setText(mContext.getString(text));
    }

    public void setText(String text){
        mText = text.replace("null", "");
        etText.setText(mText);
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

    public void setmHideLabelIfEmpty(boolean hide){
        mHideLabelIfEmpty = hide;
        etText.removeTextChangedListener(animateLabel);
        animateLabel.setmHideLabelIfEmpty(mHideLabelIfEmpty);
        if(hide)
            etText.addTextChangedListener(animateLabel);
        else
            h.setVisible(tvLabel, true);
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
