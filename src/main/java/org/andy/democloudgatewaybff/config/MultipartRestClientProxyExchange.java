package org.andy.democloudgatewaybff.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayServerResponse;
import org.springframework.cloud.gateway.server.mvc.handler.RestClientProxyExchange;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class MultipartRestClientProxyExchange extends RestClientProxyExchange {

    private final RestClient restClient;

    public MultipartRestClientProxyExchange(RestClient restClient) {
        super(restClient);
        this.restClient = restClient;
    }



    @Override
    public ServerResponse exchange(Request request) {
        var servletRequest = request.getServerRequest().servletRequest();
        if (servletRequest instanceof MultipartHttpServletRequest) {
            return exchangeMultiPart(request, (CustomGatewayMvcMultipartResolver.CustomGatewayMultipartHttpServletRequest) servletRequest);
        }

        return restClient.method(request.getMethod()).uri(request.getUri())
                .headers(httpHeaders -> httpHeaders.putAll(request.getHeaders()))
                .body(outputStream -> copyBody(request, outputStream))
                .exchange((clientRequest, clientResponse) -> doExchange(request, clientResponse), false);
    }

    /** custom code :  process multipart request
     */
    private ServerResponse exchangeMultiPart(Request request, CustomGatewayMvcMultipartResolver.CustomGatewayMultipartHttpServletRequest servletRequest) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        var multipartFiles = servletRequest.getMultipartFiles();
        for (var part : multipartFiles.entrySet()) {
            for (var value : part.getValue()){
                try {
                    // Rebuild Content-Disposition
                    MultiValueMap<String, String> contentDispositionMap = new LinkedMultiValueMap<>();
                    ContentDisposition contentDisposition = ContentDisposition.builder("form-data").name(part.getKey()).filename(value.getOriginalFilename()).build();
                    contentDispositionMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

                    // Create new HttpEntity with body + content-disposition
                    HttpEntity<byte[]> fileEntity = new HttpEntity<>(value.getBytes(), contentDispositionMap);

                    parts.add(part.getKey(), fileEntity);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        var parameterMap = servletRequest.getParameterMap();
        for (var parameterMapKey : parameterMap.entrySet()) {
            String key = parameterMapKey.getKey();
            String[] values = parameterMapKey.getValue();

            if (!parts.containsKey(key)) { // Avoid duplicate entries.
                for (String value : values) {
                    parts.add(key, value);
                }
            }
        }

        log.info("Parts: {}", parts);

        return restClient.method(request.getMethod()).uri(request.getUri()).headers(httpHeaders -> httpHeaders.putAll(request.getHeaders())).body(parts)
                .exchange((clientRequest, clientResponse) -> doExchange(request, clientResponse), false);
    }
    private static int copyBody(Request request, OutputStream outputStream) throws IOException {
        // same code that the parent class
        return StreamUtils.copy(request.getServerRequest().servletRequest().getInputStream(), outputStream);
    }

    private static ServerResponse doExchange(Request request, ClientHttpResponse clientResponse) throws IOException {
        // same code that the parent class
        InputStream body = clientResponse.getBody();
        // put the body input stream in a request attribute so filters can read it.
        MvcUtils.putAttribute(request.getServerRequest(), MvcUtils.CLIENT_RESPONSE_INPUT_STREAM_ATTR, body);
        ServerResponse serverResponse = GatewayServerResponse.status(clientResponse.getStatusCode())
                .build((req, httpServletResponse) -> {
                    try (clientResponse) {
                        // get input stream from request attribute in case it was
                        // modified.
                        InputStream inputStream = MvcUtils.getAttribute(request.getServerRequest(),
                                MvcUtils.CLIENT_RESPONSE_INPUT_STREAM_ATTR);
                        // copy body from request to clientHttpRequest
                        StreamUtils.copy(inputStream, httpServletResponse.getOutputStream());
                    }
                    return null;
                });
        MyClientHttpResponseAdapter proxyExchangeResponse = new MyClientHttpResponseAdapter(clientResponse);
        request.getResponseConsumers()
                .forEach(responseConsumer -> responseConsumer.accept(proxyExchangeResponse, serverResponse));

        return serverResponse;
    }
}
