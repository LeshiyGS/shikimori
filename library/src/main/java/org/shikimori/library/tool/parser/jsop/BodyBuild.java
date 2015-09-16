package org.shikimori.library.tool.parser.jsop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.GridLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.shikimori.library.R;
import org.shikimori.library.adapters.AniPostGaleryAdapter;
import org.shikimori.library.custom.CustomGridlayout;
import org.shikimori.library.custom.ExpandableHeightGridView;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.objects.one.ItemImage;
import org.shikimori.library.objects.one.ItemImageShiki;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.ImageController;
import org.shikimori.library.tool.parser.ParcerTool;
import org.shikimori.library.tool.parser.UILImageGetter;
import org.shikimori.library.tool.parser.elements.PostAnime;
import org.shikimori.library.tool.parser.elements.PostImage;
import org.shikimori.library.tool.parser.elements.Quote;
import org.shikimori.library.tool.parser.elements.Spoiler;
import org.shikimori.library.tool.parser.elements.VideoImage;
import org.shikimori.library.tool.parser.htmlutil.TextHtmlUtils;
import org.shikimori.library.tool.popup.BasePopup;
import org.shikimori.library.tool.popup.ListPopup;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.altarix.basekit.library.activity.BaseKitActivity;


/**
 * Created by Феофилактов on 12.04.2015.
 */
public class BodyBuild {

    private UrlTextListener urlTextListener;
    private OnClickLinkInPopup popupClick;
    private ImageClickListener imageClickListener;

    public enum CLICKABLETYPE{
        NOT, INTEXT, POPUP
    }

    public enum IMAGETYPE{
        SYMPLE, POSTER, BIGIMAGE
    }

    private final Point screensize;
    private BaseKitActivity<ShikiAC> context;
    private TextView lastTv;
    List<ImageController> images = new ArrayList<>();
    CopyOnWriteArrayList<View> gallerys = new CopyOnWriteArrayList<>();
    CLICKABLETYPE clicktype = CLICKABLETYPE.NOT;
    // check reach maxLenth

    public BodyBuild(BaseKitActivity<ShikiAC> context) {
        this.context = context;
        screensize = hs.getScreenSize(context);
    }

    public void setOnImageClickListener(ImageClickListener imageClickListener){
        this.imageClickListener = imageClickListener;
    }

    /**
     * Если type = INTEXT то при клике на ссылку в тексте возвращаеться урл
     * и в setUrlTextListener возвращаеться ссылка
     * Если type = POPUP показываеться диалог со списком всех урлов
     * и setClickLinkInPopup возвращаются данные
     * @param type
     */
    public void setClickType(CLICKABLETYPE type){
        clicktype = type;
    }

    /**
     * urlTextListener = INTEXT
     * @param urlTextListener
     */
    public void setUrlTextListener(UrlTextListener urlTextListener){
        this.urlTextListener = urlTextListener;
    }

    /**
     * urlTextListener = POPUP
     * @param popupClick
     */
    public void setClickLinkInPopup(OnClickLinkInPopup popupClick){
        this.popupClick = popupClick;
    }

    public View parce(String text, ViewGroup viewBody) {
        return parce(text, viewBody, 0);
    }

    public View parce(String text, ViewGroup viewBody, int maxLenght) {
        if (text == null)
            return null;
//        text = text.replace("<br><br>", "\n");
        if(maxLenght > 0 && text.length() > maxLenght)
            text = text.substring(0, maxLenght) + "...";
        viewBody.removeAllViews();
        Document doc = Jsoup.parse(text);
        return parce(doc, viewBody);
    }

    public View parce(Document doc, ViewGroup viewBody) {
        if (doc == null)
            return null;
        viewBody.removeAllViews();
        List<Node> elemnts = doc.body().childNodes();
        looper(elemnts, viewBody);
        insertText();
        return null;
    }

    /**
     * При асинхронном парсинге
     */
    public interface ParceDoneListener {
        public void done(ViewGroup view);
    }

    /**
     * Click в тексте
     */
    public interface UrlTextListener{
        public void textLink(String url, URLSpan span, View view);
    }

    /**
     * Попап лист
     */
    public interface OnClickLinkInPopup{
        public void popup(BasePopup popup);
        public void clickLink(String link);
    }

    public interface ImageClickListener{
        public void imageClick(PostImage image);
    }

    public void parceAsync(final String text, final ParceDoneListener listener) {
        parceAsync(text, 0, listener);
    }

    public void parceAsync(final String text, int maxLenght, final ParceDoneListener listener) {
        ViewsLoader viewsLoader = new ViewsLoader(context, text, listener);
        viewsLoader.setmaxLenght(maxLenght);
        prepareAsyncBuilder(viewsLoader.getBuilder());
        viewsLoader.forceLoad();
    }

    public void parceAsync(Document doc, final ParceDoneListener listener) {
        if (doc == null)
            return;
        ViewsLoader viewsLoader = new ViewsLoader(context, doc, listener);
        prepareAsyncBuilder(viewsLoader.getBuilder());
        viewsLoader.forceLoad();
    }

    void prepareAsyncBuilder(BodyBuild builder){
        builder.setOnImageClickListener(imageClickListener);
        builder.setClickLinkInPopup(popupClick);
        builder.setClickType(clicktype);
        builder.setUrlTextListener(urlTextListener);
    }

    /**
     * Проверяет все ноды для строительства вьюх
     * @param elemnts
     * @param parent
     */
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

//                if (checkSimpleHtml(html))
//                    setSimpleText(elemnt, parent);

                else checkTag((Element) elemnt, parent);
            }
        }

    }

    boolean checkSimpleHtml(String html) {
        if (html.length() < 5)
            return true;
        if (html.contains("b-spoiler"))
            return false;
        if (html.contains("b-quote"))
            return false;
        if (html.contains("b-replies"))
            return false;
        if (html.contains("blockquote"))
            return false;
        if (html.contains("c-anime"))
            return false;
        if (html.contains("img") && !checkAvaOrSmiles(html))
            return false;
        if (html.contains("comments"))
            return false;
        return true;
    }

    boolean checkTag(Element elemnt, ViewGroup parent) {
        switch (elemnt.tagName()) {
            case "div":
                if(elemnt.hasClass("c-video")){
                    addVideo(elemnt, parent);
                    break;
                }else if (elemnt.hasClass("b-spoiler")) {
                    createSpoiler(elemnt, parent);
                    break;
                } else if (elemnt.hasClass("b-quote")) {
                    buildBlockquote(elemnt, parent);
                    break;
                } else if (elemnt.hasClass("b-replies")) {
                    buildReplies(elemnt, parent);
                    break;
                }
            case "ul":
                looper(elemnt.childNodes(), parent);
                break;
            case "article":
                buildViewAni(elemnt, parent);
                break;
            case "img":
                if(!checkAvaOrSmiles(elemnt.outerHtml().trim()))
                    addImage(elemnt, parent, getImageType(elemnt));
                else
                    setSimpleText(elemnt, parent);
                break;
            case "blockquote":
                buildBlockquote(elemnt, parent);
                break;
            case "a":
                if(elemnt.hasClass("c-video")){
                    addVideo(elemnt, parent);
                } else if (elemnt.children().size() > 0 && elemnt.child(0).tagName().equals("img")) {
                    if(!checkAvaOrSmiles(elemnt.outerHtml().trim()))
                        addImage(elemnt.child(0), parent, getImageType(elemnt.child(0)));
                    else
                        setSimpleText(elemnt, parent);
                } else {
                    String comment = elemnt.attr("data-href");
                    if(comment.contains("comments") || comment.contains("message")){
                        elemnt.attr("href", comment);
                    }

                    setSimpleText(elemnt, parent);
                }
                break;
            default:
                if (elemnt.children().size() > 0)
                    looper(elemnt.childNodes(), parent);
                else
                    setSimpleText(elemnt, parent);
                break;
        }

        return true;
    }

    private void buildReplies(Element elemnt, ViewGroup parent) {
//        elemnt.prepend("<b>Ответы:</b> ");

        if(elemnt.childNodeSize() < 2)
            return;

        TextView text = new TextView(context);
        text.setLayoutParams(getDefaultParams());
        text.setTypeface(null, Typeface.ITALIC);
        text.setTextColor(context.getResources().getColor(R.color.altarixUiLabelColor));
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        text.setText("Ответы");
        text.setPadding(0,16,0,0);
        parent.addView(text);
        parent.addView(new View(context));
//        LinearLayout wr = new LinearLayout(context);
//        wr.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        params.gravity = Gravity.RIGHT;
//        wr.setLayoutParams(params);
//
//        for (int i = 0; i < elemnt.childNodeSize(); i++) {
//            Element chaild = elemnt.child(i);
//            TextView text = new TextView(context);
//
//        }
        setSimpleText(elemnt, parent);
    }

    IMAGETYPE getImageType(Element elemnt){
        if(elemnt.hasClass("b-poster"))
            return IMAGETYPE.POSTER;
        else if (elemnt.hasAttr("width") || elemnt.hasAttr("width"))
            return IMAGETYPE.SYMPLE;
        return IMAGETYPE.BIGIMAGE;
    }

    /**
     * **********************************************************
     * Work simple Blockquote
     * **********************************************************
     */
    private void buildBlockquote(Element elemnt, ViewGroup parent) {
        Node firstChild = elemnt.childNode(0);
        Quote quote;
        if (firstChild instanceof TextNode) {
            quote = new Quote(context, true);
            looper(elemnt.childNodes(), quote.getQuote());
        } else {
            Element title = (Element) firstChild;
            Elements a = title.select("a");
            quote = new Quote(context, false);
            if(a.size() > 0){
                Element user = title.select("a").get(0);
                quote.setUserName(user.attr("title"));
                quote.setUserIdFromImage(user.html());
                if(user.childNodeSize() > 0)
                    quote.setUserImage(user.child(0).attr("src"));
                images.add(quote);
                title.remove();
            }
            looper(elemnt.childNodes(), quote.getContent());
        }
//        elemnt.remove();
        parent.addView(quote.getQuote());
    }
    /**
     * **********************************************************
     * Work simple Anime and Manga preview
     * **********************************************************
     */
    private void buildViewAni(Element elemnt, final ViewGroup parent) {

        Element a = elemnt.select("a").first();

        Element imageSrc = a.select("img").first();
        if(imageSrc == null)
            return;

        AMShiki item = new AMShiki().create(null);
        item.image = new ItemImage(null);
        item.image.preview = imageSrc.attr("src");
        item.image.original = imageSrc.attr("srcset");
        item.name = imageSrc.attr("title");
        item.url = a.attr("href");
        item.id = elemnt.id();

        if(Build.VERSION.SDK_INT > 10){
            ExpandableHeightGridView exGrid;
            View view = getLastView(parent);
            if (view == null || !(view instanceof ExpandableHeightGridView)) {
                insertText();
                exGrid = createPosterGallery(parent);
                exGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AMShiki obj = (AMShiki) parent.getAdapter().getItem(position);
                        LinkHelper.goToUrl(context, obj.url, BodyBuild.this);
                    }
                });
            } else {
                exGrid = (ExpandableHeightGridView) view;
            }

            if(exGrid.getAdapter() == null)
                exGrid.setAdapter(new AniPostGaleryAdapter(context, new ArrayList<AMShiki>()));

            ((AniPostGaleryAdapter)exGrid.getAdapter()).add(item);
        } else {
            ItemImageShiki imageData = new ItemImageShiki(item.image.preview, item.image.original);
            imageData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = (String) v.getTag();
                    LinkHelper.goToUrl(context, url, BodyBuild.this);
                }
            });

            PostAnime postAnime = new PostAnime(context, imageData, item.url);
            postAnime.setTitle(item.name);
            images.add(postAnime);
            parent.addView(postAnime.getView());
        }

    }

    /**
     * **********************************************************
     * Work simple text
     * **********************************************************
     */
    void setSimpleText(Node elemnt, ViewGroup parent) {
        View v = getLastView(parent);

        String text;
        String style = elemnt.attr("style");
        if (!TextUtils.isEmpty(style)) {
            text = elemnt.childNode(0).outerHtml();
        } else {
            text = elemnt.outerHtml();
        }

        if(text.length() < 2)
            return;

//        if(text.equals("<br><br>"))
        text = text.replace("<br><br>", "\n");

        StringBuilder builder;
        if (v instanceof TextView) {
            if (lastTv != null && !lastTv.equals(v))
                insertText();
            lastTv = ((TextView) v);
            builder = (StringBuilder) lastTv.getTag();
            if (builder == null) {
                builder = new StringBuilder();
                lastTv.setTag(builder);
            }
        } else {
            if(text.equals("<br>"))
                return;
            insertText();
            lastTv = new TextView(context);
            lastTv.setLayoutParams(getDefaultParams());
            if (parent.getId() == R.id.llQuoteBody) {
                lastTv.setTypeface(null, Typeface.ITALIC);
                lastTv.setTextColor(context.getResources().getColor(R.color.altarixUiLabelColor));
            }
            lastTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            builder = new StringBuilder();
            lastTv.setTag(builder);
            if(lastTv.getParent()!=null)
                ((ViewGroup)lastTv.getParent()).removeView(lastTv);
            parent.addView(lastTv);
        }

        TextHtmlUtils.getStyledText(style, builder, text);

    }

    void insertText() {
        if (lastTv == null)
            return;
        StringBuilder builder = (StringBuilder) lastTv.getTag();
        if(builder == null){
            lastTv = null;
            return;
        }
        Spanned _text = ParcerTool.fromHtml(builder.toString(),
                new UILImageGetter(lastTv, context), null);
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(_text);
        if(clicktype == CLICKABLETYPE.INTEXT){
            URLSpan[] urls = _text.getSpans(0, _text.length(), URLSpan.class);
            for (URLSpan span : urls) {
                makeLinkClickable(spanBuilder, span);
            }
            if(lastTv!=null && urls.length > 0)
                lastTv.setMovementMethod(LinkMovementMethod.getInstance());
        } else if(clicktype == CLICKABLETYPE.POPUP){
            List<String> listUrl = new ArrayList<>();
            URLSpan[] urls = _text.getSpans(0, _text.length(), URLSpan.class);
            for (URLSpan span : urls) {
                listUrl.add(span.getURL());
            }
            if(listUrl.size() > 0){
                lastTv.setTag(R.id.spanned_urls, listUrl);
                lastTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> listUrl = (List<String>) v.getTag(R.id.spanned_urls);
                        ListPopup popup = new ListPopup((Activity) context);
                        popup.setList(listUrl);
                        if(popupClick!= null)
                            popupClick.popup(popup);
                        popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if(popupClick!= null){
                                    String item = (String) parent.getAdapter().getItem(position);
                                    popupClick.clickLink(item);
                                }
                            }
                        });
                        popup.show();
                    }
                });
            }
        }
        if(lastTv!=null)
            lastTv.setText(spanBuilder);
        lastTv = null;
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                if(urlTextListener!=null)
                    urlTextListener.textLink(span.getURL(), span, view);
                else
                    LinkHelper.goToUrl(context, span.getURL(), BodyBuild.this);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
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
//        label.remove();
        // text
        Element content = elemnt.getElementsByClass("inner").get(0);
        looper(content.childNodes(), spoiler.getContent());
        //elemnt.remove();
    }

    /**
     * **********************************************************
     * Work whith video
     * @param element
     * @param parent
     ***********************************************************/
    private void addVideo(Element element, ViewGroup parent) {

        Elements links = element.select("a");
        String href = "", marker = "", image = "";
        for (Element a : links) {
            if(a.hasClass("video-link")){
                href = a.attr("href");
                Element img = a.select("img").get(0);
                image = img.attr("src");
            }
            else if (a.hasClass("marker"))
                marker = a.html();
        }

        ItemImageShiki item = new ItemImageShiki();
        item.setThumb(image);
        item.setOriginal(image);

        VideoImage video = new VideoImage(context, item, href);
        video.setMarker(marker);
        images.add(video);

        View view = getLastView(parent);
        addToGallery(parent, view, video.getView());
    }


    /**
     * **********************************************************
     * Work whith images
     * @param element
     * @param parent  **********************************************************
     * @param typeImage
     */
    void addImage(Element element, ViewGroup parent, IMAGETYPE typeImage) {

        ItemImageShiki item = new ItemImageShiki();
        item.setThumb(element.attr("src"));

        Element parentNode = element.parent();
        if (parentNode.tagName().equals("a")) {
            item.setOriginal(parentNode.attr("href"));
        }

        final PostImage postImg = new PostImage(context, item);
        if(imageClickListener!=null){
            postImg.getImage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageClickListener.imageClick(postImg);
                }
            });
            postImg.getImage().setOnTouchListener(hs.getImageHighlight);
        }
        // добавляем в пост обработку для отображения картинки
        images.add(postImg);

        // получаем последнюю добавленную вьюху
        View view = getLastView(parent);

        if (typeImage == IMAGETYPE.BIGIMAGE) {
            postImg.setIsGallery();
            addToGallery(parent, view, postImg.getImage());
        } else {
            insertText();
            postImg.initMargin();
            parent.addView(postImg.getImage());
        }
    }

    private void addToGallery(ViewGroup parent, View lastView, View v){
        if(!(lastView instanceof CustomGridlayout)){
            insertText();
            lastView = createImageGallery(parent);
            gallerys.add(lastView);
        }
        CustomGridlayout grid = (CustomGridlayout) lastView;
        grid.addView(v);
    }

    private View getLastView(ViewGroup parent) {
        int count = parent.getChildCount();
        if (count == 0)
            return null;
        return parent.getChildAt(count - 1);
    }

    private GridLayout createImageGallery(ViewGroup parent) {
        CustomGridlayout layout = new CustomGridlayout(context);
        layout.setColumnCount(3);
        layout.setLayoutParams(getDefaultParams());
        layout.setPadding(0, 0, 0, 30);
        layout.setUseDefaultMargins(true);
        parent.addView(layout);
        return layout;
    }

    private ExpandableHeightGridView createPosterGallery(ViewGroup parent) {
        ExpandableHeightGridView layout = new ExpandableHeightGridView(context);
        layout.setNumColumns(ExpandableHeightGridView.AUTO_FIT);
        layout.setStretchMode(ExpandableHeightGridView.STRETCH_COLUMN_WIDTH);
        layout.setLayoutParams(getDefaultParams());
        layout.setPadding(0, 0, 0, 30);
        parent.addView(layout);
        return layout;
    }


    boolean checkAvaOrSmiles(String url) {
        if (url.contains("/images/user/") || url.contains("/images/smileys/"))
            return true;
        return false;
    }

    public void  loadPreparedImages(){

//        if(images.size() == 0)
//            return;

//        for (View v : gallerys){
//            final GridLayout grid = (GridLayout) v;
//            grid.post(new Runnable() {
//                @Override
//                public void run() {
//                    int viewCount = grid.getChildCount();
//                    int column = viewCount > 3 ? 3 : viewCount;
//                    for (int i = 0; i < viewCount; i++) {
//                        View v = grid.getChildAt(i);
//                        GridLayout.LayoutParams itemParams = (GridLayout.LayoutParams) v.getLayoutParams();
//
//                        int gridSize = grid.getWidth() == 0 ? screensize.x : grid.getWidth();
//                        gridSize -= grid.getPaddingLeft() - grid.getPaddingRight();
//                        gridSize -= itemParams.rightMargin - itemParams.leftMargin;
//                        itemParams.width = (gridSize / column);
//                        v.setLayoutParams(itemParams);
//                    }
//                }
//            });
//        }

        for (int i = 0; i < images.size(); i++) {
            images.get(i).loadImage();
        }
        images.clear();
    }

    /**
     * **********************************************************
     * settings
     *
     * @return ***********************************************************
     */
    ViewGroup.LayoutParams getDefaultParams() {
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    static class ViewsLoader extends AsyncTaskLoader<ViewGroup> {
        private BodyBuild builder;
        private String text;
        private Document doc;
        private ParceDoneListener listener;
        private int maxLenght;

        public ViewsLoader(BaseKitActivity context, String text, ParceDoneListener listener) {
            super(context);
            this.text = text;
            builder = new BodyBuild(context);
            this.listener = listener;
        }

        public ViewsLoader(Context context, Document doc, ParceDoneListener listener) {
            super(context);
            this.doc = doc;
            this.listener = listener;
        }

        public BodyBuild getBuilder() {
            return builder;
        }

        @Override
        public ViewGroup loadInBackground() {
            LinearLayout view = new LinearLayout(getContext());
            view.setLayoutParams(hs.getDefaultParams());
            view.setOrientation(LinearLayout.VERTICAL);
            builder.parce(text, view, maxLenght);
            return view;
        }

        @Override
        public void deliverResult(ViewGroup data) {
            super.deliverResult(data);
            listener.done(data);
            builder.loadPreparedImages();
        }

        public void setmaxLenght(int maxLenght) {
            this.maxLenght = maxLenght;
        }
    }
}
