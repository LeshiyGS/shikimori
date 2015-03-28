package ru.altarix.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
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
public class CustomEditText extends CustomViewBase implements View.OnClickListener {
    private EditText etText;
    private String mDigits;
    private View ivWarning;
    private Crouton crouton;
    private boolean isRegexpValueValid = true;
    private String errorPopupText;
    AnimateLabel animateLabel;
    private int inpType = 0x00000001;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, @LayoutRes int layout) {
        super(context);
        guLayout = layout;
        init(null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs, R.layout.altarix_ui_custom_edit_text);

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
        etText = (EditText)this.findViewById(editTextId);
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

        ivWarning = this.findViewById(R.id.ivWarning);
        if(ivWarning!=null)
            ivWarning.setOnClickListener(this);

        animateLabel = new AnimateLabel(mContext, tvLabel, mHideLabelIfEmpty, etText);
        if(mHideLabelIfEmpty)
            etText.addTextChangedListener(animateLabel);

        etText.setHint(getHint());
        if(attrs !=null) {

            // set input type
            inpType = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "inputType", 0x00000001);
            etText.setInputType(inpType);
            if(inpType == InputType.TYPE_CLASS_PHONE)
                setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(18), new PhoneFormatter()});


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

    public void setRegexpIconListener(final String regexp, String errorPopupText){
        this.errorPopupText = errorPopupText;
        isRegexpValueValid = false;
        etText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current_text = etText.getText().toString();
                if(!match(regexp, current_text)) {
                    isRegexpValueValid = false;
                    if(ivWarning.getVisibility() != VISIBLE) {
                        h.setVisible(ivWarning, true);
                    }
                } else {
                    isRegexpValueValid = true;
                    hideErrorMessage();
                    h.setVisibleGone(ivWarning);
                }
            }
//            String xxx = "^\\p{L}{2}\\p{N}{3,6}$";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        etText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    //h.setVisibleGone(ivWarning);
                    hideErrorMessage();
                }
            }
        });
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
    public EditText getEditText(){
        return etText;
    }

    public void setText(int text){
        setText(mContext.getString(text));
    }

    public void setHint(String hint){
        super.setHint(hint); // DON'T DO THAT!
        if(etText!=null)
            etText.setHint(hint);
        //invalidateLayout();
    }

    public void setText(String text){
        mText = "null".equals(text) ? "" : text;
        setTextOfType();
//        etText.setText(mText);
        //invalidateLayout();
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

    public void setHint(int hint){
        setHint(mContext.getString(hint));
    }

    public void addTextChangedListener(TextWatcher listener) {
        etText.addTextChangedListener(listener);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivWarning){
            if(crouton!=null)
                hideErrorMessage();
            else
                showErrorMessage();
        }
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
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            /** Защищаем префикс '+7 ' */
            if (dstart < 3 && end < 3)
                return source.length() == 0 ? dest.subSequence(dstart, dend) : "";

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
}
