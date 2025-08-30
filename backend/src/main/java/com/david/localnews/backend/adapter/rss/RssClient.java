package com.david.localnews.backend.adapter.rss;

import com.david.localnews.backend.dao.entity.RawNews;
import com.david.localnews.backend.dao.entity.enums.RssFeedType;

import java.util.List;

public interface RssClient {
    List<RawNews> fetchOne(String feedUrl, RssFeedType type, int maxPerFeed);
}