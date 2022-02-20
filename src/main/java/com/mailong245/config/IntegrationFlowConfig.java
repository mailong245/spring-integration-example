package com.mailong245.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@Configuration
@EnableIntegration
public class IntegrationFlowConfig {

    @Bean
    public IntegrationFlow myFlow1() {
        return IntegrationFlows.from("input")
                .<String, String>transform(String::toUpperCase)
                .handle(e -> System.out.println(e.getPayload()))
                .get();
    }

    @Bean
    public IntegrationFlow myFlow2() {
        return IntegrationFlows.from("input")
                .filter("World"::equals)
                .transform("Hello "::concat)
                .handle(System.out::println)
                .get();
    }

    @Bean
    public MessageChannel input() {
        return MessageChannels.direct().get();
    }

    @InboundChannelAdapter(value = "input", poller = @Poller(fixedDelay = "1000"))
    public Message<String> adapter1() {
        return new GenericMessage<>("Xin chao cac ban");
    }

    @InboundChannelAdapter(value = "input", poller = @Poller(fixedDelay = "1000"))
    public Message<String> adapter2() {
        return new GenericMessage<>("World");
    }

}
