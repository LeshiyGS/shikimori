package ru.altarix.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import ru.altarix.ui.tool.h;

/**
 * Created by Владимир on 26.06.2014.
 */
public abstract class BaseCustomEditText<T extends EditText> extends CustomViewBase{
    private String mDigits;
    protected T etText;
    protected Crouton crouton;
    protected boolean isRegexpValueValid = true;
    protected String errorPopupText;
    private int inpType = 0x00000001;
    private AnimateLabel animateLabel;


    public abstract @LayoutRes int getLayoutId();

    public BaseCustomEditText(Context context) {
        this(context, null);
    }

    public BaseCustomEditText(Context context, @LayoutRes int layout) {
        super(context);
        guLayout = layout;
        init(null);
    }

    public BaseCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs, guLayout == 0 ? getLayoutId() : guLayout);

        int editTextId = 0,
                idFocusLeft     = 0,
                idFocusRight    = 0,
                idFocusUp       = 0,
                idFocusDown     = 0
        ;
        if(typedArray!=null){
            try {
                editTextId = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiEditTextId, 0);
                mDigits    = typedArray.getString(R.styleable.AltarixUiDesclareStyle_uiRegexp);

                idFocusLeft = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "nextFocusLeft",0);
                idFocusRight = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "nextFocusRight",0);
                idFocusUp = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "nextFocusUp",0);
                idFocusDown = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "nextFocusDown",0);

            } finally {
                typedArray.recycle();
            }
        }

        if(editTextId==0)
            editTextId = R.id.etText;
        etText = (T) this.findViewById(editTextId);
        if(attrs !=null)
            etText.setEnabled(attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "enabled", true));
        if(idFocusLeft!=0)
            etText.setNextFocusLeftId   ( idFocusLeft  );
        if(idFocusRight!=0)
            etText.setNextFocusRightId  ( idFocusRight );
        if(idFocusUp!=0)
            etText.setNextFocusUpId     ( idFocusUp    );
        if(idFocusDown!=0)
            etText.setNextFocusDownId   ( idFocusDown  );

        animateLabel = new AnimateLabel(mContext, tvLabel, mHideLabelIfEmpty, etText);
        if(mHideLabelIfEmpty)
            etText.addTextChangedListener(animateLabel);

        setHint(getHint());

        if(attrs !=null) {

            // set input type
            inpType = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "inputType", 0x00000001);
            etText.setInputType(inpType);
            if(inpType == InputType.TYPE_CLASS_PHONE)
                setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(18), new PhoneFormatter(etText)});


            int maxLenght = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "maxLength", 0);
            // set max length string
            if(maxLenght > 0){
                setInputFilter(new InputFilter.LengthFilter(maxLenght));
            }
        }

        setTextOfType();

        if(mDigits!=null){
            setRegexp(mDigits);
        }

        h.setFont(mContext, this);

        if(Build.VERSION.SDK_INT > 20){
            setColorCursor();
        }
    }

    protected void setColorCursor(){
    };


    public void setError(String errorText){
//        if(tiLabel!=null){
//            tiLabel.setErrorEnabled(errorText == null ? false : true);
//            if(errorText != null)
//                tiLabel.setError(errorText);
//        }
    }

    private void setTextOfType(){
        if(TextUtils.isEmpty(mText) && tvLabel!=null)
                tvLabel.setVisibility(INVISIBLE);

        if(inpType == InputType.TYPE_CLASS_PHONE){
            if(mText == null || mText.length() < 3 || cleanup(mText).length() < 3)
                mText = "+7 ";
        }
        etText.setText(mText);

        etText.setSelection(etText.getText().toString().length());
    }

    public void setInputFilters(InputFilter[] filters){
        List<InputFilter> l = new ArrayList<InputFilter>();
        InputFilter[] ifilters = etText.getFilters();

        for (InputFilter f : ifilters)
            l.add(f);
        for (InputFilter f : filters)
            l.add(f);

        if(l.size() > 0)
            etText.setFilters(l.toArray(new InputFilter[l.size()]));
    }

    public void setInputFilter(InputFilter filter){
        List<InputFilter> l = new ArrayList<InputFilter>();
        InputFilter[] ifilters = etText.getFilters();

        for (InputFilter f : ifilters)
            l.add(f);

        l.add(filter);

        if(l.size() > 0)
            etText.setFilters(l.toArray(new InputFilter[l.size()]));
    }

    public void showErrorMessage(String message){
        if(message == null)
            return;

        hideErrorMessage();
        crouton = Crouton.makeText((android.app.Activity) mContext, message, Style.ALERT)
                .setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_INFINITE).build());
        crouton.show();
    }

    public void showErrorMessage(){
        if(errorPopupText == null)
            return;

        hideErrorMessage();
        crouton = Crouton.makeText((android.app.Activity) mContext, errorPopupText, Style.INFO)
                .setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_INFINITE).build());
        crouton.show();
    }

    public void hideErrorMessage(){
        if(crouton != null){
            crouton.hide();
            crouton = null;
        }
    }

    public boolean isRegexpValueValid(){
        return isRegexpValueValid;
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
                if(!match(regexp, current_text)) {

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

    public static boolean match(String needle_regexp, CharSequence string) {
        Pattern p = Pattern.compile(needle_regexp);
        Matcher matcher = p.matcher(string);
        if (matcher.find())
            return true;
        return false;
    }

    public String getText(){
        mText = etText.getText().toString().trim();
        if(etText.getInputType() == InputType.TYPE_CLASS_PHONE)
            return cleanup(mText).toString();
        return mText;
    }
    public T getEditText(){
        return etText;
    }

    public void setText(int text){
        setText(mContext.getString(text));
    }

    public void setHint(String hint){
        super.setHint(hint);
//        if(etText!=null)
//            etText.setHint(hint);
//        if(tiLabel!=null)
//            tiLabel.setHint(getHint());
//        else
            etText.setHint(getHint());
    }

    public void setText(String text){
        mText = "null".equals(text) ? "" : text;
        setTextOfType();
//        etText.setText(mText);
        //invalidateLayout();
    }

    public void setHint(int hint){
        setHint(mContext.getString(hint));
    }

    public void addTextChangedListener(TextWatcher listener) {
        etText.addTextChangedListener(listener);
    }

    /**
     * Show keyboard
     */
    public void requestKeyboard() {
        if(etText!=null){
            etText.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public interface OnCustomFocusOutListener{
        public void onFocuusOut();
    }

    protected static CharSequence cleanup(CharSequence loginString) {
        StringBuilder format = new StringBuilder(loginString);
        for (int i = 0; i < format.length(); )
            if (!Character.isDigit(format.charAt(i)))
                format.deleteCharAt(i);
            else i++;
        return format;
    }

    /**
     * Форматирование номера телефона.
     *
     * @author cab404
     */
    public static class PhoneFormatter implements InputFilter {
        private EditText editText;

        public PhoneFormatter(EditText editText){
            this.editText = editText;
        }


        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            /** Защищаем префикс '+7 ' */
            if (dstart < 3 && end < 3){
                String text = dest.toString();
                int count = text.length();
//                dstart = count > 2 ? count : dstart;
                if(count > 2){

                    StringBuilder result = strPhoneFormat(text + source, 0);
                    editText.setText(result.toString());
                    editText.setSelection(result.length());
                }
                return source.length() == 0 ? dest.subSequence(dstart, dend) : "";
            }

            /*
             *   +7 (987) 123-45-67
             *     3     7   12 15
             */
            return strPhoneFormat(source, dstart);

        }
    }

    private static StringBuilder strPhoneFormat(CharSequence source, int dstart){
        StringBuilder format = new StringBuilder(cleanup(source));


        for (int i = 0; i < format.length(); i++) {
            switch (i + dstart) {
                case 0: format.insert(i, "+"); break;
                case 2: format.insert(i, " "); break;
                case 3: format.insert(i, "("); break;
                case 7: format.insert(i, ") "); break;
                case 12: format.insert(i, "-"); break;
                case 15: format.insert(i, "-"); break;
            }
        }

        if(dstart == 0 && format.length() == 2)
            format.append(" ");

        return format;
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
}
