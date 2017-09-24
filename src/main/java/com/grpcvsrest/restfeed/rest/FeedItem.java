package com.grpcvsrest.restfeed.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedItem {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("type")
    private final String type;
    @JsonProperty("content")
    private final String content;
    @JsonProperty("next_uri")
    private final String nextUri;


    @JsonCreator
    public FeedItem(
            @JsonProperty("id") Integer id,
            @JsonProperty("type") String type,
            @JsonProperty("content") String content,
            @JsonProperty("next_uri") String nextUri) {
        this.id = id;
        this.content = content;
        this.type= type;
        this.nextUri = nextUri;
    }

    public Integer getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getNextUri() {
        return nextUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedItem feedItem = (FeedItem) o;

        if (!id.equals(feedItem.id)) return false;
        if (!type.equals(feedItem.type)) return false;
        if (!content.equals(feedItem.content)) return false;
        return nextUri.equals(feedItem.nextUri);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + nextUri.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", nextUri='" + nextUri + '\'' +
                '}';
    }
}
