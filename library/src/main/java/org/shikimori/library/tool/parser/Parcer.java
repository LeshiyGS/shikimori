package org.shikimori.library.tool.parser;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shikimori.library.tool.parser.elements.HtmlText;
import org.shikimori.library.tool.parser.elements.PostImage;
import org.shikimori.library.tool.parser.elements.Spoiler;

import java.util.ArrayList;

/**
 * Created by Владимир on 09.04.2015.
 */
public class Parcer {

    public static void addComment(String body, ArrayList<String> comments_sname, LinearLayout layout, final Activity activity, Boolean is_link) {
        ArrayList<LinearLayout> spoiler_layout = new ArrayList<>();
        spoiler_layout.add(layout);
        body = body.replace("[Отправлено с Android]", "");

        if (body.contains("[SEP]")) {
            final String[] body_elements = body.split("\\[SEP\\]");
            for (int i = 0; i < body_elements.length; i++) {
                //Если кусок комментария начало спойлера
                if (body_elements[i].contains("SP:true")) {
                    //Получаем номер названия спойлера
                    int tag = Integer.parseInt(body_elements[i].substring(0, body_elements[i].indexOf("!")).split(":")[2]);
                    body_elements[i] = body_elements[i].replace("SP:true:" + tag + "!", ""); //Удаляем служебную информацию
                    //Создаем элементы комментария
                    Spoiler sp = new Spoiler(activity);
                    sp.setTitle(comments_sname.get(tag));
                    sp.setOpened(false);
                    //Загрузка смайлов и авок
                    HtmlText htmlText = new HtmlText(activity, is_link);
                    htmlText.setText(body_elements[i], sp.getContent());
                    //Добавляем элементы на слой спойлера
                    spoiler_layout.add(sp.getSpoiler());
                    spoiler_layout.get(spoiler_layout.size() - 2).addView(sp.getTitle());
                    spoiler_layout.get(spoiler_layout.size() - 2).addView(sp.getSpoiler());
                    spoiler_layout.get(spoiler_layout.size() - 1).addView(sp.getContent());
                    //Если кусок комментария конец спойлера
                } else if (body_elements[i].contains("SP:false")) {
                    //закрываем слой и добавляем текстовое поле в предидущем
                    int tag = Integer.parseInt(body_elements[i].substring(0, body_elements[i].indexOf("!")).split(":")[2]);
                    body_elements[i] = body_elements[i].replace("SP:false:" + tag + "!", ""); //Удаляем служебную информацию
                    //Обработчик нажатия на название спойлера
                    HtmlText htmlText = new HtmlText(activity, is_link);
                    htmlText.setText(body_elements[i]);
                    //Удаляем слой из массива
                    spoiler_layout.remove(spoiler_layout.size() - 1);
                    //Добавляем текст после спойлера
                    spoiler_layout.get(spoiler_layout.size() - 1).addView(htmlText.getText());
                } else if (body_elements[i].contains("CI:")) {
                    final String tag = body_elements[i].substring(0, body_elements[i].indexOf("!") + 1);
                    body_elements[i] = body_elements[i].replace(tag, ""); //Удаляем служебную информацию
                    //Вставляем картинку

                    PostImage img = new PostImage(activity, tag);
                    spoiler_layout.get(spoiler_layout.size() - 1).addView(img.getImage());

                    HtmlText htmlText = new HtmlText(activity, is_link);
                    htmlText.setText(body_elements[i]);
                    spoiler_layout.get(spoiler_layout.size() - 1).addView(htmlText.getText());

                } else if (body_elements[i].contains("VI:")) {
                    ////
                    final String tag = "http:" + body_elements[i].substring(3, body_elements[i].indexOf("!")).split("http:")[2];
                    final String tag2 = "http:" + body_elements[i].substring(3, body_elements[i].indexOf("!")).split("http:")[1];
                    //
                    body_elements[i] = body_elements[i].replace(body_elements[i].substring(0, body_elements[i].indexOf("!") + 1), ""); //Удаляем служебную информацию
                    //Вставляем картинку

                    PostImage img = new PostImage(activity, tag);
                    img.setBigImageUrl(tag2);
                    spoiler_layout.get(spoiler_layout.size() - 1).addView(img.getImage());

                    HtmlText htmlText = new HtmlText(activity, is_link);
                    spoiler_layout.get(spoiler_layout.size() - 1).addView(htmlText.getText());

                } else {
                    //Просто вставляем текстовое поле
                    HtmlText htmlText = new HtmlText(activity, is_link);
                    htmlText.setText(body_elements[i]);
                    layout.addView(htmlText.getText());
                }

            }
        } else {

            HtmlText htmlText = new HtmlText(activity, is_link);
            htmlText.setType(TextView.BufferType.SPANNABLE);
            htmlText.setText(body);
            layout.addView(htmlText.getText());

        }
    }
}
