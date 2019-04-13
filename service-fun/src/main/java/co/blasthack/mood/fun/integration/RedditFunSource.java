package co.blasthack.mood.fun.integration;

import co.blasthack.mood.fun.model.FunMessage;
import co.blasthack.mood.fun.model.RedditPost;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RedditFunSource extends BaseFunSource {

    private List<RedditPost> getHotPosts(int numberOfPosts) {
        JsonNode root = getJsonFromURL("https://www.reddit.com/r/UpliftingNews/hot/.json?limit=" + numberOfPosts);
        if (root != null && root.has("data")) {
            return StreamSupport.stream(root.get("data").get("children").spliterator(), false)
                    .map(RedditPost::new)
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Getting hot posts went wrong");
    }

    private JsonNode getJsonFromURL(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return new ObjectMapper().readTree(reader.lines().collect(Collectors.joining()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FunMessage> getFunMessages() {
        List<RedditPost> trendingUpliftingPosts = getHotPosts(2);

        List<FunMessage> messages = new ArrayList<>();
        for (RedditPost post : trendingUpliftingPosts) {
            messages.add(new FunMessage(post.getRedditUrl(), post.getTitle()));
        }
        return messages;
    }

    @Override
    protected HttpEntity performRequest(String url, HttpHeaders headers) {
        return null;
    }
}
