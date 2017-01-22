package vc.startups.parser;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by nonu on 1/21/2017.
 */
public class Solution {
    public static void main(String[] args) throws IOException{
        Connection.Response startPage = Jsoup.connect("https://vc.ru/startups")
                .ignoreContentType(true)
                .timeout(5000).validateTLSCertificates(false).execute();

        String loadMore = startPage.parse().select("#btn-section-articles-more").first().attr("data-loadMore");
        String loadMoreHash = startPage.parse().select("#btn-section-articles-more").first().attr("data-loadMoreHash");

        Connection.Response json = Jsoup.connect(String.format("https://vc.ru/helper/articlesMore?count=300&loadMore=%s&loadMoreHash=%s", loadMore, loadMoreHash))
                .ignoreContentType(true)
                .timeout(5000).validateTLSCertificates(false).execute();

        String bodyWithStartups = StringEscapeUtils.unescapeHtml4(json.body());
        bodyWithStartups = StringEscapeUtils.unescapeJava(bodyWithStartups);
        bodyWithStartups = bodyWithStartups.substring(10);
        Document docWithStartups = Jsoup.parse(bodyWithStartups);
        Elements links = docWithStartups.select("div.b-articles__b__title-w>a.b-articles__b__title");

        // TODO: use paginator instead of ajax response to get all the startups


        LinkedList<Startup> startups = new LinkedList<>();
        for (Element e : links) {
            Document startUpPage = Jsoup.connect(e.attr("href")).timeout(5000).validateTLSCertificates(false).get();

            String sid = null;
            try {
                sid = startUpPage.select(".b-comment-add__message").first().getElementsByTag("input").first().attr("value");
            } catch (Exception e1) {
                sid = startUpPage.select(".z-comments>.b-articles-comments").first().attr("data-articleid");
            }

            // Get json with will and wont thumbs for the current startup.
            Connection.Response responseWithThumbs = Jsoup.connect("https://vc.ru/special/getWillFlyData?id=" + sid)
                    .ignoreContentType(true)
                    .timeout(5000).validateTLSCertificates(false).execute();
            JSONObject jsonWithThumbs = new JSONObject(responseWithThumbs.body());

            Startup startup = new Startup(sid, e.attr("href"));
            int votesUp = jsonWithThumbs.getInt("will");
            int votesDown = jsonWithThumbs.getInt("wont");

            if (votesUp + votesDown > 30) {
                startup.setUps(votesUp);
                startup.setDowns(votesDown);
                startups.add(startup);
            }
        }

        Collections.sort(startups);
        startups.stream()
                .forEach(System.out::println);

    }
}
