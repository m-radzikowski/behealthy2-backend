package co.blasthack.mood.fun.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedditPost {

    private final String title;
    private final String redditUrl;

    public RedditPost(JsonNode node) {
        JsonNode dataNode = node.get("data");
        title = dataNode.get("title").asText("No Title");
        redditUrl = "https://www.reddit.com" + dataNode.get("permalink").asText("/r/failed");
    }
}
