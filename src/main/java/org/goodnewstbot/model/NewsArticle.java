package org.goodnewstbot.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewsArticle {

    String link;
    String title;
    String desc;
    LocalDateTime pubDate;
    String category;

    public NewsArticle(String link, String title, String desc, LocalDateTime pubDate, String category) {
        this.link = link;
        this.title = title;
        this.desc = desc;
        this.pubDate = pubDate;
        this.category = category;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public String getFormattedPubDate() {
        DateTimeFormatter df = pubDate.getYear() < LocalDateTime.now().getYear() ? DateTimeFormatter.ofPattern("dd MMM yyyy в HH:mm") : DateTimeFormatter.ofPattern("dd MMM в HH:mm");
        return df.format(pubDate);
    }

    public String getCategory() {
        return category;
    }
}
