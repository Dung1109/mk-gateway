package org.andy.democloudgatewaybff.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class MyClientHttpResponseAdapter implements org.springframework.cloud.gateway.server.mvc.handler.ProxyExchange.Response {
    private final ClientHttpResponse response;

    MyClientHttpResponseAdapter(ClientHttpResponse response) {
        this.response = response;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        try {
            return response.getStatusCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

}