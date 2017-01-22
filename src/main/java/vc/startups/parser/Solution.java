package vc.startups.parser;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by nonu on 1/21/2017.
 */
public class Solution {
    private static final Logger log = LoggerFactory.getLogger(Solution.class);

    public static void main(String[] args) throws IOException{
        LinkedList<Startup> startups = new LinkedList<>();

        for (int i = 1; i <= 24; i++) {
            log.info("Reading page #" + i );
            Document docWithStartups = Jsoup.connect("https://vc.ru/startups/page"+i)
                    .ignoreContentType(true)
                    .timeout(5000).validateTLSCertificates(false).execute().parse();
            Elements links = docWithStartups.select("div.b-articles__b__title-w>a.b-articles__b__title");

            for (Element e : links) {
                log.info("\tAccessing to page with href: " + e.attr("href"));
                Document startUpPage = Jsoup.connect(e.attr("href")).timeout(5000).validateTLSCertificates(false).get();

                String sid = null;
                // TODO: fix NPE exceptions processing, catch all the startups
                try {
                    sid = startUpPage.select(".b-comment-add__message").first().getElementsByTag("input").first().attr("value");
                } catch (Exception e1) { // hi, ERR08-J
                    try {
                        sid = startUpPage.select(".z-comments>.b-articles-comments").first().attr("data-articleid");
                    } catch (Exception e2) { // and again, ERR08-J
                        log.error(e2.getMessage());
                        continue;
                    }
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
        }

        Collections.sort(startups);

        try (FileWriter writer = new FileWriter("startups.txt")) {
            startups.stream()
                    .forEach((s) -> {
                        try {
                            writer.write(s.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
