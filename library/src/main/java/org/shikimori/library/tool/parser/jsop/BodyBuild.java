package org.shikimori.library.tool.parser.jsop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.shikimori.library.R;
import org.shikimori.library.objects.one.ImageShiki;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.ParcerTool;
import org.shikimori.library.tool.parser.elements.ImageGetter;
import org.shikimori.library.tool.parser.elements.Quote;
import org.shikimori.library.tool.parser.elements.Spoiler;

import java.util.List;


/**
 * Created by Феофилактов on 12.04.2015.
 */
public class BodyBuild {

    private final Point screensize;
    private Context context;
    private TextView lastTv;

    public BodyBuild(Activity context) {
        this.context = context;
        screensize = h.getScreenSize(context);
    }

    public View parce(String text, ViewGroup viewBody) {
        if (text == null)
            return null;
        viewBody.removeAllViews();
//        long timeBefore = System.currentTimeMillis();
        Document doc = Jsoup.parse(clearText(text));
//        Log.d("timeload", "" + ((System.currentTimeMillis() - timeBefore) / 1000));
        List<Node> elemnts = doc.body().childNodes();
        looper(elemnts, viewBody);
        insertText();
        return null;
    }

    void looper(List<Node> elemnts, ViewGroup parent) {


        synchronized (elemnts) {

            for (int i = 0; i < elemnts.size(); i++) {

                if (i == elemnts.size())
                    break;

                Node elemnt = elemnts.get(i);

                if (elemnt instanceof TextNode) {
                    setSimpleText(elemnt, parent);
                    continue;
                }

                String html = elemnt.outerHtml().trim();
                if (html.length() == 0)
                    continue;

                if (checkSimpleHtml(html))
                    setSimpleText(elemnt, parent);

                else checkTag((Element) elemnt, parent);
            }
        }

    }

    boolean checkSimpleHtml(String html) {
        if (html.length() < 5)
            return true;
        if (html.contains("b-spoiler"))
            return false;
        if (html.contains("blockquote"))
            return false;
        if (html.contains("img") && !checkAvaOrSmiles(html))
            return false;
        return true;
    }

    boolean checkTag(Element elemnt, ViewGroup parent) {
        switch (elemnt.tagName()) {
            case "div":
                if (elemnt.hasClass("b-spoiler")) {
                    createSpoiler(elemnt, parent);
                    break;
                }
            case "ul":
                looper(elemnt.childNodes(), parent);
                break;
            case "img":
                addImage(elemnt, parent);
                break;
            case "blockquote":
                buildBlockquote(elemnt, parent);
                break;
            case "a":
                if (elemnt.child(0).tagName().equals("img")) {
                    addImage(elemnt.child(0), parent);
                } else
                    setSimpleText(elemnt, parent);
                break;
            default:
                if (elemnt.children().size() > 0)
                    looper(elemnt.childNodes(), parent);
                break;
        }

        return true;
    }

    /**
     * **********************************************************
     * Work simple Blockquote
     * **********************************************************
     */
    private void buildBlockquote(Element elemnt, ViewGroup parent) {
        Node firstChild = elemnt.childNode(0);
        Quote quote;
        if(firstChild instanceof TextNode){
            quote = new Quote(context, true);
            looper(elemnt.childNodes(), quote.getQuote());
        } else {
            Element title = (Element) firstChild;
            Element user = title.select("a").get(0);
            quote = new Quote(context, false);
            quote.setUserName(user.attr("title"));
            quote.setUserIdFromImage(user.html());
            quote.setUserImage(user.child(0).attr("src"));
            title.remove();
            looper(elemnt.childNodes(), quote.getContent());
        }
        elemnt.remove();
        parent.addView(quote.getQuote());
    }

    /**
     * **********************************************************
     * Work simple text
     * **********************************************************
     */
    void setSimpleText(Node elemnt, ViewGroup parent) {
        View v = getLastView(parent);
        StringBuilder builder;
        if (v instanceof TextView) {
            lastTv = ((TextView) v);
            builder = (StringBuilder) lastTv.getTag();
        } else {
            lastTv = new TextView(context);
            lastTv.setLayoutParams(getDefaultParams());
            builder = new StringBuilder();
            lastTv.setTag(builder);
            parent.addView(lastTv);
        }
        String style = elemnt.attr("style");
        if (!TextUtils.isEmpty(style)) {
            String[] styles = style.split(";");
            builder.append("<font ");
            for (String s : styles) {
                String[] params = s.split(":");
                if (params.length < 2)
                    continue;
                builder.append(params[0])
                        .append("='")
                        .append(params[1].trim())
                        .append("' ");
            }

            String text = elemnt.childNode(0).outerHtml();
            builder.append(">")
                    .append(text)
                    .append("</font>");
        } else {
            String text = elemnt.outerHtml();
            builder.append(text);
        }
    }

    String clearText(String text){
        return text.replace("\n", "");
    }

    void insertText() {
        if (lastTv == null)
            return;
        StringBuilder builder = (StringBuilder) lastTv.getTag();
        lastTv.setText(ParcerTool.fromHtml(builder.toString(),
                ImageGetter.getImgGetter(context,
                        context.getResources().getDrawable(R.drawable.missing_preview), null), null));
        lastTv = null;
    }

    /**
     * **********************************************************
     * Work with spoiler
     * **********************************************************
     */
    void createSpoiler(Element elemnt, ViewGroup parent) {
        insertText();
        Spoiler spoiler = new Spoiler(context);
        parent.addView(spoiler.getSpoiler());
        // label
        Element label = elemnt.child(0);
        spoiler.getTitle().setText(" + " + label.text());
        label.remove();
        // text
        Element content = elemnt.getElementsByClass("inner").get(0);
        looper(content.childNodes(), spoiler.getContent());
        elemnt.remove();
    }

    /**
     * **********************************************************
     * Work whith images
     *
     * @param element
     * @param parent  **********************************************************
     */
    void addImage(Element element, ViewGroup parent) {
        insertText();
        View view = getLastView(parent);
        if (view == null || !(view instanceof GridLayout))
            view = createImageGallery(parent);

        final GridLayout grid = (GridLayout) view;

        ImageView img = new ImageView(context);
        img.setLayoutParams(getDefaultParams());
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageShiki obg = new ImageShiki();
        obg.thumb = element.attr("src");

        grid.addView(img);

        grid.post(new Runnable() {
            @Override
            public void run() {
                int viewCount = grid.getChildCount();
                int column = viewCount > 3 ? 3 : viewCount;
                for (int i = 0; i < viewCount; i++) {
                    View v = grid.getChildAt(i);
                    GridLayout.LayoutParams itemParams = (GridLayout.LayoutParams) v.getLayoutParams();
                    itemParams.width = (screensize.x / column) - itemParams.rightMargin - itemParams.leftMargin;
                    v.setLayoutParams(itemParams);
                }
            }
        });

//        parent.addView(img);
        Element parentNode = element.parent();
        if (parentNode.tagName().equals("a")) {
            obg.original = parentNode.attr("href");
            parentNode.remove();
        } else {
            element.remove();
        }

        img.setTag(obg);
        ImageLoader.getInstance().displayImage(obg.thumb, img);
    }

    private View getLastView(ViewGroup parent) {
        int count = parent.getChildCount();
        if (count == 0)
            return null;
        return parent.getChildAt(count - 1);
    }


    private GridLayout createImageGallery(ViewGroup parent) {
        GridLayout layout = new GridLayout(context);
        layout.setColumnCount(3);
        layout.setLayoutParams(getDefaultParams());
        parent.addView(layout);
        return layout;
    }


    boolean checkAvaOrSmiles(String url) {
        if (url.contains("/images/user/") || url.contains("/images/smileys/"))
            return true;
        return false;
    }

    ViewGroup.LayoutParams getDefaultParams() {
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
