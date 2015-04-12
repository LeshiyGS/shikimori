package org.shikimori.library.tool.parser.jsop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shikimori.library.R;
import org.shikimori.library.objects.one.ImageShiki;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.ParcerTool;
import org.shikimori.library.tool.parser.elements.ImageGetter;
import org.shikimori.library.tool.parser.elements.Spoiler;


/**
 * Created by Феофилактов on 12.04.2015.
 */
public class BodyBuild {

    private final Point screensize;
    private Context context;
    private TextView lastTv;
    public BodyBuild(Activity context){
        this.context = context;
        screensize = h.getScreenSize(context);
    }

    public View parce(String text, ViewGroup viewBody){
        if(text == null)
            return null;

        Document doc = Jsoup.parse(text);

        Elements elemnts = doc.body().children();
        looper(elemnts, viewBody);
        return null;
    }

    void looper(Elements elemnts, ViewGroup parent){
        for (Element elemnt : elemnts) {

            String html = elemnt.outerHtml().trim();
            if(html.length() == 0)
                continue;
            if(checkSimpleHtml(html))
                setSimpleText(elemnt, parent);
            else checkTag(elemnt, parent);
//            else if(checkTag(elemnt, parent)){
//                if(elemnt.children().size() > 0){
//                    looper(elemnt.children(), parent);
//                }
//            }

        }
    }

    boolean checkSimpleHtml(String html){
        if(html.length() < 5)
            return true;
        if(html.contains("b-spoiler"))
            return false;
        if(html.contains("img") && !checkAvaOrSmiles(html))
            return false;
        return true;
    }

    boolean checkTag(Element elemnt, ViewGroup parent){
        switch (elemnt.tagName()){
            case "div":
                if(elemnt.hasClass("b-spoiler")){
                    createSpoiler(elemnt, parent);
                    break;
                }
            case "ul":
                looper(elemnt.children(), parent);
                break;
            case "img":
                addImage(elemnt, parent);
            case "a":
                if(elemnt.child(0).tagName().equals("img")){
                    addImage(elemnt.child(0), parent);
                } else
                    setSimpleText(elemnt, parent);
                break;
            default:
                if(elemnt.children().size() > 0)
                    looper(elemnt.children(), parent);
                break;
        }

        return true;
    }

    /*************************************************************
     * Work simple text
     ************************************************************/
     void setSimpleText(Element elemnt, ViewGroup parent){
         View v = getLastView(parent);
         StringBuilder builder;
         if(v instanceof TextView){
             lastTv = ((TextView) v);
             builder = (StringBuilder) lastTv.getTag();
         } else{
             lastTv = new TextView(context);
             lastTv.setLayoutParams(getDefaultParams());
             builder = new StringBuilder();
             lastTv.setTag(builder);
             parent.addView(lastTv);
         }

         builder.append(elemnt.outerHtml());
     }

    void insertText(){
        if(lastTv == null)
            return;
        StringBuilder builder = (StringBuilder) lastTv.getTag();
        lastTv.setText(ParcerTool.fromHtml(builder.toString(),
                ImageGetter.getImgGetter(context,
                        context.getResources().getDrawable(R.drawable.missing_preview), null), null));
        lastTv = null;
    }

    /*************************************************************
     * Work with spoiler
     ************************************************************/
    void createSpoiler(Element elemnt, ViewGroup parent){
        insertText();
        Spoiler spoiler = new Spoiler(context);
        parent.addView(spoiler.getSpoiler());
        // label
        Element label = elemnt.child(0);
        spoiler.getTitle().setText(label.text());
        label.remove();
        // text
        Element content = elemnt.getElementsByClass("inner").get(0);
        looper(content.children(), spoiler.getContent());
        elemnt.remove();
    }

    /*************************************************************
     * Work whith images
     * @param element
     * @param parent
     ************************************************************/
    void addImage(Element element, ViewGroup parent){
        insertText();
        View view = getLastView(parent);
        if(view==null || !(view instanceof GridLayout))
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
                    itemParams.width = (screensize.x/column) - itemParams.rightMargin - itemParams.leftMargin;
                    v.setLayoutParams(itemParams);
                }
            }
        });

//        parent.addView(img);
        Element parentNode = element.parent();
        if(parentNode.tagName().equals("a")){
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
        if(count == 0)
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


    boolean checkAvaOrSmiles(String url){
        if (url.contains("/images/user/") || url.contains("/images/smileys/"))
            return true;
        return false;
    }

    ViewGroup.LayoutParams getDefaultParams(){
       return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
