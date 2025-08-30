package com.david.localnews.backend.adapter.rss;


import com.david.localnews.backend.dao.entity.RawNews;
import com.david.localnews.backend.dao.entity.enums.RssFeedType;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class RomeRssClient implements RssClient {
    private static final int CONNECT_TIMEOUT_MS = 6000;
    private static final int READ_TIMEOUT_MS = 10000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124 Safari/537.36";
    private static final String ACCEPT_HEADER = "application/rss+xml, application/xml;q=0.9, text/xml;q=0.8, */*;q=0.7";

    @Override
    public List<RawNews> fetchOne(String feedUrl, RssFeedType type, int maxPerFeed) {
        List<RawNews> out = new ArrayList<>();
        try {
            ingestOne(feedUrl, type, Math.max(1, maxPerFeed), out);
        } catch (Exception ignored) {
        }
        return out;
    }

    private int ingestOne(String feedUrl, RssFeedType type, int maxPerFeed, List<RawNews> out) throws Exception {
        HttpURLConnection conn = null;
        int inserted = 0;
        try {
            URL url = new URL(feedUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", ACCEPT_HEADER);

            var input = new SyndFeedInput();
            try (XmlReader reader = new XmlReader(conn)) {
                var feed = input.build(reader);
                String source = feed.getTitle() != null && !feed.getTitle().isBlank() ? feed.getTitle().trim() : url.getHost();
                int taken = 0;
                for (SyndEntry e : feed.getEntries()) {
                    if (taken++ >= maxPerFeed) break;
                    String link = e.getLink() == null ? "" : e.getLink().trim();
                    if (link.isEmpty()) continue;
                    String title = safe(e.getTitle());
                    if (title.isEmpty()) continue;
                    String summary = extractSummary(e);
                    Instant published = e.getPublishedDate() != null
                            ? e.getPublishedDate().toInstant()
                            : (e.getUpdatedDate() != null ? e.getUpdatedDate().toInstant() : null);
                    out.add(new RawNews(null, title, summary, link, source, published, Instant.now(), type));
                    inserted++;
                }
            }
            return inserted;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private String extractSummary(SyndEntry e) {
        try {
            String html = null;
            if (e.getDescription() != null && e.getDescription().getValue() != null) html = e.getDescription().getValue();
            else if (!e.getContents().isEmpty() && e.getContents().get(0).getValue() != null) html = e.getContents().get(0).getValue();
            if (html == null) return "";
            String plain = Jsoup.parse(html).text();
            return plain.length() > 1000 ? plain.substring(0, 1000) : plain;
        } catch (Exception ex) {
            return "";
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

}