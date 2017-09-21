package com.grpcvsrest.restfeed.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Objects;

public class AggregatedContent {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("type")
    private final String type;
    @JsonProperty("content")
    private final String content;
    @JsonProperty("original_id")
    private final Integer originalId;

    @JsonCreator
    public AggregatedContent(
            @JsonProperty("id") Integer id,
            @JsonProperty("type") String type,
            @JsonProperty("content") String content,
            @JsonProperty("original_id") Integer originalId) {
        this.id = id;
        this.content = content;
        this.type= type;
        this.originalId = originalId;
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

    public Integer getOriginalId() {
        return originalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregatedContent)) return false;
        AggregatedContent that = (AggregatedContent) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(type, that.type) &&
                Objects.equals(content, that.content) &&
                Objects.equals(originalId, that.originalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, content, originalId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("content", content)
                .add("originalId", originalId)
                .toString();
    }
}
