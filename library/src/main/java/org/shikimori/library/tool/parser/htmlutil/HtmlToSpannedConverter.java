package org.shikimori.library.tool.parser.htmlutil;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

import org.shikimori.library.tool.parser.htmlutil.Spans.QuoteSpanShiki;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

public class HtmlToSpannedConverter implements ContentHandler {

    private static final float[] HEADER_SIZES = {
        1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };

    private String mSource;
    private XMLReader mReader;
    private SpannableStringBuilder mSpannableStringBuilder;
    private Html.ImageGetter mImageGetter;
    private Html.TagHandler mTagHandler;

    public HtmlToSpannedConverter(
            String source, Html.ImageGetter imageGetter, Html.TagHandler tagHandler,
            XMLReader parser) {
        mSource = source;
        mSpannableStringBuilder = new SpannableStringBuilder();
        mImageGetter = imageGetter;
        mTagHandler = tagHandler;
        mReader = parser;
    }

    public Spanned convert() {

        mReader.setContentHandler(this);
        try {
            mReader.parse(new InputSource(new StringReader(mSource)));
        } catch (IOException e) {
            // We are reading from a string. There should not be IO problems.
            throw new RuntimeException(e);
        } catch (SAXException e) {
            // TagSoup doesn't throw parse exceptions.
            throw new RuntimeException(e);
        }

        // Fix flags and range for paragraph-type markup.
        Object[] obj = mSpannableStringBuilder.getSpans(0, mSpannableStringBuilder.length(), ParagraphStyle.class);
        for (int i = 0; i < obj.length; i++) {
            int start = mSpannableStringBuilder.getSpanStart(obj[i]);
            int end = mSpannableStringBuilder.getSpanEnd(obj[i]);

            // If the last line of the range is blank, back off by one.
            if (end - 2 >= 0) {
                if (mSpannableStringBuilder.charAt(end - 1) == '\n' &&
                    mSpannableStringBuilder.charAt(end - 2) == '\n') {
                    end--;
                }
            }

            if (end == start) {
                mSpannableStringBuilder.removeSpan(obj[i]);
            } else {
                mSpannableStringBuilder.setSpan(obj[i], start, end, Spannable.SPAN_PARAGRAPH);
            }
        }

        return mSpannableStringBuilder;
    }

    private void handleStartTag(String tag, Attributes attributes) {
        if (tag.equalsIgnoreCase("br")) {
            // We don't need to handle this. TagSoup will ensure that there's a </br> for each <br>
            // so we can safely emite the linebreaks when we handle the close tag.
        } else if (tag.equalsIgnoreCase("p")) {
            handleP(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("div")) {
            handleP(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("em")) {
            start(mSpannableStringBuilder, new Bold());
        } else if (tag.equalsIgnoreCase("b")) {
            start(mSpannableStringBuilder, new Bold());
        } else if (tag.equalsIgnoreCase("strong")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("cite")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("dfn")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("i")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("big")) {
            start(mSpannableStringBuilder, new Big());
        } else if (tag.equalsIgnoreCase("small")) {
            start(mSpannableStringBuilder, new Small());
        } else if (tag.equalsIgnoreCase("font")) {
            startFont(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("blockquote")) {
            handleP(mSpannableStringBuilder);
            start(mSpannableStringBuilder, new Blockquote());
        } else if (tag.equalsIgnoreCase("tt")) {
            start(mSpannableStringBuilder, new Monospace());
        } else if (tag.equalsIgnoreCase("a")) {
            startA(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("u")) {
            start(mSpannableStringBuilder, new Underline());
        } else if (tag.equalsIgnoreCase("sup")) {
            start(mSpannableStringBuilder, new Super());
        } else if (tag.equalsIgnoreCase("sub")) {
            start(mSpannableStringBuilder, new Sub());
        } else if (tag.length() == 2 &&
                   Character.toLowerCase(tag.charAt(0)) == 'h' &&
                   tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            handleP(mSpannableStringBuilder);
            start(mSpannableStringBuilder, new Header(tag.charAt(1) - '1'));
        } else if (tag.equalsIgnoreCase("img")) {
            startImg(mSpannableStringBuilder, attributes, mImageGetter);
        } else if (mTagHandler != null) {
            mTagHandler.handleTag(true, tag, mSpannableStringBuilder, mReader);
        }
    }

    private void handleEndTag(String tag) {
        if (tag.equalsIgnoreCase("br")) {
            handleBr(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("p")) {
            handleP(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("div")) {
            handleP(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("em")) {
            end(mSpannableStringBuilder, Bold.class, new StyleSpan(Typeface.BOLD));
        } else if (tag.equalsIgnoreCase("b")) {
            end(mSpannableStringBuilder, Bold.class, new StyleSpan(Typeface.BOLD));
        } else if (tag.equalsIgnoreCase("strong")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("cite")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("dfn")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("i")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("big")) {
            end(mSpannableStringBuilder, Big.class, new RelativeSizeSpan(1.25f));
        } else if (tag.equalsIgnoreCase("small")) {
            end(mSpannableStringBuilder, Small.class, new RelativeSizeSpan(0.8f));
        } else if (tag.equalsIgnoreCase("font")) {
            endFont(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("blockquote")) {
            handleP(mSpannableStringBuilder);
            end(mSpannableStringBuilder, Blockquote.class, new QuoteSpanShiki(Color.GRAY));
        } else if (tag.equalsIgnoreCase("tt")) {
            end(mSpannableStringBuilder, Monospace.class,
                    new TypefaceSpan("monospace"));
        } else if (tag.equalsIgnoreCase("a")) {
            endA(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("u")) {
            end(mSpannableStringBuilder, Underline.class, new UnderlineSpan());
        } else if (tag.equalsIgnoreCase("sup")) {
            end(mSpannableStringBuilder, Super.class, new SuperscriptSpan());
        } else if (tag.equalsIgnoreCase("sub")) {
            end(mSpannableStringBuilder, Sub.class, new SubscriptSpan());
        } else if (tag.length() == 2 &&
                Character.toLowerCase(tag.charAt(0)) == 'h' &&
                tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            handleP(mSpannableStringBuilder);
            endHeader(mSpannableStringBuilder);
        } else if (mTagHandler != null) {
            mTagHandler.handleTag(false, tag, mSpannableStringBuilder, mReader);
        }
    }

    private static void handleP(SpannableStringBuilder text) {
        int len = text.length();

        if (len >= 1 && text.charAt(len - 1) == '\n') {
            if (len >= 2 && text.charAt(len - 2) == '\n') {
                return;
            }

            text.append("\n");
            return;
        }

        if (len != 0) {
            text.append("\n\n");
        }
    }

    private static void handleBr(SpannableStringBuilder text) {
        text.append("\n");
    }

    private static Object getLast(Spanned text, Class kind) {
        /*
         * This knows that the last returned object from getSpans()
         * will be the most recently added.
         */
        Object[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            return objs[objs.length - 1];
        }
    }

    private static void start(SpannableStringBuilder text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);
    }

    private static void end(SpannableStringBuilder text, Class kind,
                            Object repl) {
        int len = text.length();
        Object obj = getLast(text, kind);
        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        if (where != len) {
            text.setSpan(repl, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return;
    }

    private static void startImg(SpannableStringBuilder text,
                                 Attributes attributes, Html.ImageGetter img) {
        String src = attributes.getValue("", "src");
        Drawable d = null;

        if (img != null) {
            d = img.getDrawable(src);
        }

        if (d == null) {
            d = Resources.getSystem().getDrawable(android.R.drawable.btn_star);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }

        int len = text.length();
        text.append("\uFFFC");

        text.setSpan(new ImageSpan(d, src), len, text.length(),
                     Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void startFont(SpannableStringBuilder text,
                                  Attributes attributes) {
        String color = attributes.getValue("", "color");
        String face = attributes.getValue("", "face");
        String size = attributes.getValue("", "size");

        int len = text.length();
        text.setSpan(new Font(color, face, size), len, len, Spannable.SPAN_MARK_MARK);
    }

    private static void endFont(SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLast(text, Font.class);
        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        if (where != len) {
            Font f = (Font) obj;

            if (f.mColor != null) {
                int c = -1;

                if (f.mColor.equals("aqua")) {
                    c = 0x00FFFF;
                } else if (f.mColor.equals("black")) {
                    c = 0x000000;
                } else if (f.mColor.equals("blue")) {
                    c = 0x0000FF;
                } else if (f.mColor.equals("fuchsia")) {
                    c = 0xFF00FF;
                } else if (f.mColor.equals("green")) {
                    c = 0x008000;
                } else if (f.mColor.equals("grey")) {
                    c = 0x808080;
                } else if (f.mColor.equals("lime")) {
                    c = 0x00FF00;
                } else if (f.mColor.equals("maroon")) {
                    c = 0x800000;
                } else if (f.mColor.equals("navy")) {
                    c = 0x000080;
                } else if (f.mColor.equals("olive")) {
                    c = 0x808000;
                } else if (f.mColor.equals("purple")) {
                    c = 0x800080;
                } else if (f.mColor.equals("red")) {
                    c = 0xFF0000;
                } else if (f.mColor.equals("silver")) {
                    c = 0xC0C0C0;
                } else if (f.mColor.equals("teal")) {
                    c = 0x008080;
                } else if (f.mColor.equals("white")) {
                    c = 0xFFFFFF;
                } else if (f.mColor.equals("yellow")) {
                    c = 0xFFFF00;
                } else {
                    try {
                        c = convertValueToInt(f.mColor, -1);
                    } catch (NumberFormatException nfe) {
                        // Can't understand the color, so just drop it.
                    }
                }

                if (c != -1) {
                    text.setSpan(new ForegroundColorSpan(c | 0xFF000000),
                                 where, len,
                                 Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            if (f.mFace != null) {
                text.setSpan(new TypefaceSpan(f.mFace), where, len,
                             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            
            if (f.mSize != null) {
            	int size = Integer.parseInt(f.mSize);
            	text.setSpan(new AbsoluteSizeSpan(size), where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static void startA(SpannableStringBuilder text, Attributes attributes) {
        String href = attributes.getValue("", "href");

        int len = text.length();
        text.setSpan(new Href(href), len, len, Spannable.SPAN_MARK_MARK);
    }

    private static void endA(SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLast(text, Href.class);
        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        if (where != len) {
            Href h = (Href) obj;

            if (h.mHref != null) {
                text.setSpan(new URLSpan(h.mHref), where, len,
                             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static void endHeader(SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLast(text, Header.class);

        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        // Back off not to change only the text, not the blank line.
        while (len > where && text.charAt(len - 1) == '\n') {
            len--;
        }

        if (where != len) {
            Header h = (Header) obj;

            text.setSpan(new RelativeSizeSpan(HEADER_SIZES[h.mLevel]),
                         where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new StyleSpan(Typeface.BOLD),
                         where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        handleStartTag(localName, attributes);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        handleEndTag(localName);
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        mSpannableStringBuilder.append(CharBuffer.wrap(ch, start, length));
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    private static class Bold { }
    private static class Italic { }
    private static class Underline { }
    private static class Big { }
    private static class Small { }
    private static class Monospace { }
    private static class Blockquote { }
    private static class Super { }
    private static class Sub { }

    private static class Font {
        public String mColor;
        public String mFace;
        public String mSize;

        public Font(String color, String face, String size) {
            mColor = color == null ? null : color.toLowerCase();
            mFace = face;
            mSize = size;
        }
    }

    private static class Href {
        public String mHref;

        public Href(String href) {
            mHref = href;
        }
    }

    private static class Header {
        private int mLevel;

        public Header(int level) {
            mLevel = level;
        }
    }
    
    public static final int convertValueToInt(CharSequence charSeq, int defaultValue)
    {
        if (null == charSeq)
            return defaultValue;

        String nm = charSeq.toString();

        // XXX This code is copied from Integer.decode() so we don't
        // have to instantiate an Integer!
        
        int value;
        int sign = 1;
        int index = 0;
        int len = nm.length();
        int base = 10;

        if ('-' == nm.charAt(0)) {
            sign = -1;
            index++;
        }

        if ('0' == nm.charAt(index)) {
            //  Quick check for a zero by itself
            if (index == (len - 1))
                return 0;

            char    c = nm.charAt(index + 1);

            if ('x' == c || 'X' == c) {
                index += 2;
                base = 16;
            } else {
                index++;
                base = 8;
            }
        }
        else if ('#' == nm.charAt(index))
        {
            index++;
            base = 16;
        }

        return Integer.parseInt(nm.substring(index), base) * sign;
    }
}