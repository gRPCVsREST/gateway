package com.grpcvsrest.restfeed.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AggregatedContentResponse {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("type")
    private final String type;
    @JsonProperty("content")
    private final String content;


    @JsonCreator
    public AggregatedContentResponse(
            @JsonProperty("id") Integer id,
            @JsonProperty("type") String type,
            @JsonProperty("content") String content) {
        this.id = id;
        this.content = content;
        this.type= type;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AggregatedContentResponse that = (AggregatedContentResponse) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AggregatedContentResponse{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
