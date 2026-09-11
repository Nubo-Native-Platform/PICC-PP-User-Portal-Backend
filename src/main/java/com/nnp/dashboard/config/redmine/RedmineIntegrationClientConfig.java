package com.nnp.dashboard.config.redmine;

import com.nnp.dashboard.client.RedmineIntegrationClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class RedmineIntegrationClientConfig {

    private final RedmineConfigProps redmineConfigProps;

    @Autowired
    public RedmineIntegrationClientConfig(RedmineConfigProps redmineConfigProps) {
        this.redmineConfigProps = redmineConfigProps;
    }

    @Bean
    public RedmineIntegrationClient client(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
        return httpServiceProxyFactory.createClient(RedmineIntegrationClient.class);
    }

    @Bean
    public WebClient webClient() {
        String baseUrl = (redmineConfigProps != null && redmineConfigProps.getBaseUrl() != null && !redmineConfigProps.getBaseUrl().isBlank())
                ? redmineConfigProps.getBaseUrl() : "http://localhost:3000";
        return WebClient.builder()
                .baseUrl(baseUrl)
//                .filter(logReq())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(configWebClient()))
                .build();
    }

    private HttpClient configWebClient() {
        return HttpClient.create()
//                .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000)
                .responseTimeout(Duration.ofMillis(20000))
                .doOnConnected(
                        connection -> connection.addHandlerLast(
                                        new ReadTimeoutHandler(20000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(20000, TimeUnit.MILLISECONDS)
                                )
                );
    }

}
