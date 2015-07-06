package ru.altarix.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
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
public class CustomEditText extends BaseCustomEditText<EditText> implements View.OnClickListener {
    private View ivWarning;
    AnimateLabel animateLabel;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, @LayoutRes int layout) {
        super(context, layout);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.altarix_ui_custom_edit_text;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        ivWarning = this.findViewById(R.id.ivWarning);
        if(ivWarning!=null)
            ivWarning.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivWarning){
            if(crouton!=null)
                hideErrorMessage();
            else
                showErrorMessage();
        }
    }
}
