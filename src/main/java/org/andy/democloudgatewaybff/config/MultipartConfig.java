package org.andy.democloudgatewaybff.config;

import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.cloud.gateway.server.mvc.handler.RestClientProxyExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class MultipartConfig {

    @Bean
    public RestClientProxyExchange restClientProxyExchange(ClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
        // init your RestTemplate here
        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        // return our  custom instance of RestClientProxyExchange that is able to process multipart request
        return new MultipartRestClientProxyExchange(RestClient.create(restTemplate));
    }

    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public CustomGatewayMvcMultipartResolver gatewayMvcMultipartResolver(MultipartProperties properties) {
        CustomGatewayMvcMultipartResolver multipartResolver = new CustomGatewayMvcMultipartResolver();
        multipartResolver.setResolveLazily(properties.isResolveLazily());
        return multipartResolver;
    }
}
