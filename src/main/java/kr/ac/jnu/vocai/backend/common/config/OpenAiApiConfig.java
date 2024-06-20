package kr.ac.jnu.vocai.backend.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.openai.api.ApiUtils;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * OpenAiApi 프로퍼티 설정.
 * @author daecheol song
 * @since 1.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenAiApiConfig {

    private final OpenAiChatProperties openAiChatProperties;

    @Bean
    @Primary
    public OpenAiApi openAiApi() {
        return new OpenAiApi(ApiUtils.DEFAULT_BASE_URL, openAiChatProperties.getApiKey(), restClientBuilder(), webClientBuilder());
    }

    @Bean
    public ClientHttpRequestInterceptor customInterceptor() {
        return (request, body, execution) -> {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
            logRequest(request, body);
            ClientHttpResponse response = execution.execute(request, body);
            logResponse(response);
            return response;
        };
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                });
    }


    @Bean
    public RestClient.Builder restClientBuilder() {
        RestClientBuilderConfigurer configurer = new RestClientBuilderConfigurer();
        RestClient.Builder builder = RestClient.builder()
                .requestFactory(customRequestFactory())
                .requestInterceptor(customInterceptor());
        return configurer.configure(builder);
    }

    @Bean
    public ClientHttpRequestFactory customRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(5000));
        requestFactory.setReadTimeout(Duration.ofMillis(150000));
        return new BufferingClientHttpRequestFactory(requestFactory);
    }


    private void logRequest(HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            log.debug("===========================request begin================================================");
            log.debug("URI         : {}", request.getURI());
            log.debug("Method      : {}", request.getMethod());
            log.debug("Headers     : {}", request.getHeaders());
            log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
            log.debug("==========================request end================================================");
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("============================response begin==========================================");
            log.debug("Status code  : {}", response.getStatusCode());
            log.debug("Status text  : {}", response.getStatusText());
            log.debug("Headers      : {}", response.getHeaders());
            log.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
            log.debug("=======================response end=================================================");
        }
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("===========================request begin================================================");
            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.debug("{}={}", name, value)));
            log.debug("==========================request end================================================");
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("============================response begin==========================================");
            log.debug("Response status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.debug("{}={}", name, value)));
            log.debug("=======================response end=================================================");
            return Mono.just(clientResponse);
        });
    }

}