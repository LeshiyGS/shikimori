package ru.altarix.ui.tool;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Владимир on 29.01.2015.
 */
public class TextStyling {

    private List<SimpleStyle> glovalStyles = new ArrayList<>();

    public enum TextStyle {
        ITALIC,
        BOLD,
        COLOR,
        BACKGROUND_COLOR
    }

    public TextStyling() {
    }

    public TextStyling addGlobalStyle(TextStyle textStyle){
        return addGlobalStyle(textStyle, null);
    }
    public TextStyling addGlobalStyle(TextStyle textStyle, String value){
        glovalStyles.add(new SimpleStyle(textStyle, value));
        return this;
    }

    public TextStyling(SimpleStyle... styles) {
        for(SimpleStyle st : styles)
            glovalStyles.add(st);
    }


    // <b color=000; background=fff;>text<b>
    // <i background=fff;>text<i>
    public SpannableStringBuilder formatString(String text) {
        ArrayList<CStyle> styles = doRegexp("<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)</\\1>", text);
        SpannableStringBuilder textSpan = new SpannableStringBuilder(text);

        for (CStyle s : styles) {
            List<CharacterStyle> rebuild = new ArrayList();
            if (s.tag.startsWith("<b")) {
                rebuild.add(new StyleSpan(Typeface.BOLD));
            } else if (s.tag.startsWith("<i")) {
                rebuild.add(new StyleSpan(Typeface.ITALIC));
            }

            if (s.tag.contains("color=")) {
                String color = getTagValue("color", s.tag);
                if (color != null) {
                    color = color.replace("#", "");
                    rebuild.add(new ForegroundColorSpan(Color.parseColor("#" + color)));
                }
            }

            if (s.tag.contains("background=")) {
                String color = getTagValue("background", s.tag);
                if (color != null) {
                    color = color.replace("#", "");
                    rebuild.add(new BackgroundColorSpan(Color.parseColor("#" + color)));
                }
            }

            int startOffsetOfClickedText = text.indexOf(s.all);
            int endOffsetOfClickedText = startOffsetOfClickedText + s.all.length();
            int afrterFotmatOffset = startOffsetOfClickedText + s.text.length();

            textSpan.replace(startOffsetOfClickedText, endOffsetOfClickedText, s.text);

            for (CharacterStyle cs : rebuild) {
                textSpan.setSpan(cs, startOffsetOfClickedText, afrterFotmatOffset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return textSpan;
    }

    /**
     * Форматирует весь найденный текст
     * @param searchText
     * @param text
     * @param styles
     * @return
     */
    public SpannableStringBuilder formatString(String searchText, String text, SimpleStyle... styles) {
        return formatString(searchText, text, false, styles);
    }

    /**
     * Форматирует весь найденный текст
     * @param searchText
     * @param text
     * @param onlyFirst только первое слово
     * @param styles
     * @return
     */
    public SpannableStringBuilder formatString(String searchText, String text, boolean onlyFirst, SimpleStyle... styles) {
        int startOffsetOfClickedText = text.indexOf(searchText);
        if(startOffsetOfClickedText < 0)
            return new SpannableStringBuilder(text);

        int endOffsetOfClickedText = startOffsetOfClickedText + searchText.length();

        SpannableStringBuilder textSpan = new SpannableStringBuilder(text);
        while (startOffsetOfClickedText >= 0){
            // Делаем клон, потому что стилизуеться только последние найденное слово
            ArrayList<CharacterStyle> rebuild = new ArrayList();
            for (SimpleStyle s : getListStyles(styles)) {
                if (s.type == TextStyle.BOLD) {
                    rebuild.add(new StyleSpan(Typeface.BOLD));
                } else if (s.type== TextStyle.ITALIC) {
                    rebuild.add(new StyleSpan(Typeface.ITALIC));
                } else if (s.type== TextStyle.COLOR) {
                    rebuild.add(new ForegroundColorSpan(Color.parseColor("#" + s.color.replace("#", ""))));
                } else if (s.type== TextStyle.BACKGROUND_COLOR) {
                    rebuild.add(new BackgroundColorSpan(Color.parseColor("#" + s.color.replace("#", ""))));
                }
            }
            for (CharacterStyle cs : rebuild) {
                textSpan.setSpan(cs, startOffsetOfClickedText, endOffsetOfClickedText, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if(onlyFirst)
                break;
            startOffsetOfClickedText = text.indexOf(searchText, endOffsetOfClickedText);
            endOffsetOfClickedText = startOffsetOfClickedText + searchText.length();
        }

        return textSpan;
    }

    /**
     * Стилизует prependText и вставляет его перед text
     * @param prependText
     * @param text
     * @param styles
     * @return
     */
    public SpannableStringBuilder prepend(String prependText, String text, SimpleStyle... styles) {
        SpannableStringBuilder textSpan = formatString(prependText, prependText, styles);
        textSpan.append(" ").append(text);
        return textSpan;
    }

    /**
     * Стилизует appendText и вставляет его после text
     * @param appendText
     * @param text
     * @param styles
     * @return
     */
    public SpannableStringBuilder append(String appendText, String text, SimpleStyle... styles) {
        SpannableStringBuilder textSpan = formatString(appendText, appendText, styles);
        textSpan.insert(0, text + " ");
        return textSpan;
    }

    List<SimpleStyle> getListStyles(SimpleStyle[] styles) {
        List<SimpleStyle> list = new ArrayList<>();
        if (glovalStyles != null) {
            for (SimpleStyle g : glovalStyles)
                list.add(g);
        }
        if (styles != null && styles.length > 0)
            list.addAll(Arrays.asList(styles));

        return list;
    }

    String getTagValue(String tag, String text) {
        Pattern p = Pattern.compile(tag + "=(.+?);");
        Matcher matcher = p.matcher(text);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    ArrayList<CStyle> doRegexp(String needle_regexp, String string) {
        if (string == null)
            return null;

        Pattern p = Pattern.compile(needle_regexp, Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = p.matcher(string);

        ArrayList<CStyle> list = new ArrayList<>();
        while (matcher.find()) {
            CStyle style = new CStyle();
            style.all = matcher.group(0);
            style.tag = matcher.group(1);
            style.text = matcher.group(2);
            list.add(style);
        }
        return list;
    }

    private static class CStyle {
        public String tag, text, all;
    }

    /**
     * color // 000, ffffff, dfdfdf
     */
    public static class SimpleStyle {
        private TextStyle type;
        private String color;

        public SimpleStyle(TextStyle type) {
            this.type = type;
        }

        public SimpleStyle(TextStyle type, String color) {
            this.type = type;
            this.color = color;
        }
    }

}
