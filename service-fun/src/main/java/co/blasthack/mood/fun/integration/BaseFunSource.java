package co.blasthack.mood.fun.integration;

import co.blasthack.mood.fun.model.FunMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;

public abstract class BaseFunSource<T> {

    public abstract List<FunMessage> getFunMessages();

    protected abstract HttpEntity<T> performRequest(String url, HttpHeaders headers);
}
