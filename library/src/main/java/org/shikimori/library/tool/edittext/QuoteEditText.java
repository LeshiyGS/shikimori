package org.shikimori.library.tool.edittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.widget.EditText;

import com.jmpergar.awesometext.AwesomeTextHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 10.06.2015.
 */
public class QuoteEditText {
    private final AwesomeTextHandler awesomeTextViewHandler;
    private EditText editText;
    private final List<ImageSpan> mEmoticonsToRemove = new ArrayList<ImageSpan>();

    public QuoteEditText(EditText editText){
        this.editText = editText;
        awesomeTextViewHandler = new AwesomeTextHandler();
        awesomeTextViewHandler
                .addViewSpanRenderer("(\\[message.+?message])", new PostSpanRenderer())
                .setView(editText);
        addChangeListener();
    }

    private void addChangeListener() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                // Check if some text will be removed.
                if (count > 0) {
                    int end = start + count;
                    Editable message = editText.getEditableText();
                    ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

                    for (ImageSpan span : list)
                    {
                        // Get only the emoticons that are inside of the changed
                        // region.

                        int spanStart = message.getSpanStart(span);
                        int spanEnd = message.getSpanEnd(span);
                        //txt = text.toString();
                        if ((spanStart < end) && (spanEnd > start)) {
                            // Add to remove list
                            mEmoticonsToRemove.add(span);
//                            mEmoticonsToRemove.add(span);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable text) {
                Editable message = editText.getEditableText();

                // Commit the emoticons to be removed.
                for (ImageSpan span : mEmoticonsToRemove) {
                    int start = message.getSpanStart(span);
                    int end = message.getSpanEnd(span);

                    // Remove the span
                    message.removeSpan(span);

                    // Remove the remaining emoticon text.
                    if (start != end) {
                        message.delete(start, end);
                    }
                }
                mEmoticonsToRemove.clear();


            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }
        });
    }

    public void setText(String text) {
        awesomeTextViewHandler.setText(text);
        editText.setSelection(editText.getSelectionEnd() + text.length());
    }
}
