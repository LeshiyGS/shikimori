package org.shikimori.library.tool.parser;

import android.text.Html;
import android.text.Spanned;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.shikimori.library.tool.parser.htmlutil.HtmlToSpannedConverter;

/**
 * Created by Владимир on 09.04.2015.
 */
public class ParcerTool {

    public static Spanned fromHtml(String source){
        return fromHtml(source, null, null);
    }

    public static Spanned fromHtml(String source,  Html.ImageGetter imageGetter,
                                   Html.TagHandler tagHandler) {
        Parser parser = new Parser();
        try {
            parser.setProperty(Parser.schemaProperty, HtmlParser.schema);
        } catch (org.xml.sax.SAXNotRecognizedException e) {
            // Should not happen.
            throw new RuntimeException(e);
        } catch (org.xml.sax.SAXNotSupportedException e) {
            // Should not happen.
            throw new RuntimeException(e);
        }

        HtmlToSpannedConverter converter =
                new HtmlToSpannedConverter(source, imageGetter, tagHandler,
                        parser);
        return converter.convert();
    }

    private static class HtmlParser {
        private static final HTMLSchema schema = new HTMLSchema();
    }
}
