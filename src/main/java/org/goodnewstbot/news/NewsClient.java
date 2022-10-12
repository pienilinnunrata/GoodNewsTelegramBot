package org.goodnewstbot.news;

import org.goodnewstbot.exceptions.NoNewsException;
import org.goodnewstbot.exceptions.RSSException;
import org.goodnewstbot.model.NewsArticle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.goodnewstbot.news.KeyWords.getKeyWords;

public class NewsClient {

    public static String RSS = "https://lenta.ru/rss";
    public static String RU_NEWS = "https://lenta.ru/rss/news/russia";
    public static String WORLD_NEWS = "https://lenta.ru/rss/news/world";

    private final String newsUrl;
    private final List<String> keyWords;

    public NewsClient(String newsUrl) {
        this.newsUrl = newsUrl;
        this.keyWords = getKeyWords();
    }

    public NewsArticle getRandomArticle() throws RSSException {
        List<NewsArticle> news = getNews();
        news = filterNewsExceptKeywords(news);
        if (news != null) {
            int max = news.size() - 2;
            return news.get((int) ((Math.random() * (max))));
        } else throw new NoNewsException();
    }

    private List<NewsArticle> getNews() throws RSSException {
        try {
            List<NewsArticle> news = new ArrayList<>();
            Elements newsElements = Jsoup.connect(newsUrl).get().select("item");
            for (Element e : newsElements) {
                String articleDate = e.select("pubDate").text();
                news.add(
                        new NewsArticle(
                                e.select("link").text(),
                                e.select("title").text(),
                                e.select("description").text(),
                                LocalDateTime.parse(articleDate, DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss X", Locale.UK)),
                                e.select("category").text()
                        )
                );
            }
            return news;
        } catch (IOException e) {
            throw new RSSException(newsUrl);
        }
    }

    private List<NewsArticle> filterNewsExceptKeywords(List<NewsArticle> news) {
        for (String key : keyWords) {
            if (news != null) {
                news = news.stream()
                        .filter(a -> !a.getDesc().toUpperCase().contains(key.toUpperCase()) && !a.getTitle().toUpperCase().contains(key.toUpperCase()))
                        .collect(Collectors.toList());
            } else return null;
        }
        return news;
    }
}
