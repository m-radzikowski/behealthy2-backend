package co.blasthack.mood.fun.integration;

import co.blasthack.mood.fun.model.FunMessage;
import co.blasthack.mood.fun.model.GiphyObject;
import co.blasthack.mood.fun.model.GiphyResponseMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class GipfyFunSource extends BaseFunSource {

    @Override
    public List<FunMessage> getFunMessages() {
        HttpHeaders headers = new HttpHeaders();

        String giphyHost = "http://api.giphy.com/v1/gifs/search";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(giphyHost)
                .queryParam("api_key", "hbWbSmW11Ev3R6BAsFb5RdH7LQ7orQrY")
                .queryParam("q", "Cat")
                .queryParam("limit", 2);

        UriComponents uriComponents = builder.build().encode();
        String url = uriComponents.toUri().toString();

        HttpEntity<GiphyResponseMessage> response = performRequest(url, headers);
        GiphyResponseMessage giphyResponse = response.getBody();

        assert giphyResponse != null;

        List<FunMessage> messages = new ArrayList<>();
        for (GiphyObject gif : giphyResponse.getData()) {
            messages.add(new FunMessage(gif.getUrl(), "Giphy Cat"));
        }
        return messages;
    }

    @Override
    protected HttpEntity<GiphyResponseMessage> performRequest(String url, HttpHeaders headers) {
        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GiphyResponseMessage.class);
    }
}
