package com.mailong245.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.*;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;

import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.springframework.integration.handler.LoggingHandler.Level.INFO;

@Configuration
@EnableIntegration
public class JdbcIntegrationFlowConfig {

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/spring");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("123456");
        return dataSourceBuilder.build();
    }

    @Bean
    public MessageSource<Object> jdbcMessageSource1() {
        return new JdbcPollingChannelAdapter(this.dataSource(), "SELECT * FROM user");
    }

    @Bean
    public IntegrationFlow pollingFlow1() {
        return IntegrationFlows.from(jdbcMessageSource1(),
                        c -> c.poller(Pollers.fixedRate(10000).maxMessagesPerPoll(1)))
                .transform(Transformers.toJson())
                .wireTap("testWiretap.input")
//                .enrich(e -> e.requestChannel("testEnrich1.input").requestPayload(Message::getPayload))
                .enrich(e -> e.requestChannel("testEnrichWithoutHeaderFunction.input")
                        .headerFunction("functionn", Message::getPayload))
                .<String>handle((p, h) -> {
                    p = Objects.requireNonNull(h.get("functionn")).toString();
                    System.out.println("functionn : " + h.get("functionn"));
                    System.out.println("=================> Handle" + p);
                    String firstSentence = "Hello";
                    System.out.println("=================> First sentence: " + firstSentence);
                    return MessageBuilder.withPayload(firstSentence).build();
                })
                .<String>handle((p, h) -> {
                    String firstSentence = p;
                    String secondSentence = firstSentence + " World !";
                    System.out.println("=================> Second sentence: " + secondSentence);
                    return MessageBuilder.withPayload(secondSentence).build();
                })
                .get();
    }

    @Bean
    public IntegrationFlow testEnrichWithoutHeaderFunction() {
        return f -> f.log(INFO, this.getClass().getName(), msg -> "testEnrichWithoutHeaderFunction: TRUEEEEEEEEEEEEEE")
                .handle((p, h) -> MessageBuilder.withPayload(p.toString().concat("concat testEnrichWithoutHeaderFunction")).build());
    }

    @Bean
    public IntegrationFlow testEnrich2() {
        return f -> f.handle((p, h) -> {
            System.out.println("==================> Enrich p.getClass() " + p.getClass());
            return MessageBuilder.withPayload(TRUE).build();
        });
    }

    @Bean
    public IntegrationFlow testEnrich1() {
        return f -> f.enrich(e -> e.requestChannel("testEnrich2.input").headerFunction("enrichedData",
                        s -> {
                            System.out.println("INSIDE THE HEADER FUNCTION");
                            System.out.println("s.getPayload(): " + s.getPayload());
                            return s.getPayload();
                        }))
                .route("headers['enrichedData']", // point to the result of enrichedData header function
                        m -> m
                                .subFlowMapping(TRUE, sf -> sf
                                        .log(INFO, this.getClass().getName(), msg -> "TRUEEEEEEEEEEEEEE")
                                        .handle((p, h) ->
                                                MessageBuilder.withPayload(p.toString().concat("TRUEEEEEEEEEEEEEE"))
                                        ))
                                .subFlowMapping(FALSE, sf -> sf.log(INFO, this.getClass().getName(), msg -> "FALSEEEEEEEEEEEEE")
                                        .handle((p, h) ->
                                                MessageBuilder.withPayload(p.toString().concat("FALSEEEEEEEEEEEEE"))
                                        ))
                );
    }

    @Bean
    public IntegrationFlow testWiretap() {
        return f -> f.handle((p) -> {
            System.out.println("==================> Wiretap payload: " + p.getPayload());
            System.out.println("==================> Wiretap header: " + p.getHeaders());
        });
    }

    @Bean
    @InboundChannelAdapter(value = "jdbcChannel", poller = @Poller(fixedRate = "10000", maxMessagesPerPoll = "1"))
    public JdbcPollingChannelAdapter jdbcMessageSource2() {
        return new JdbcPollingChannelAdapter(this.dataSource(), "SELECT * FROM user");
    }

    @Bean
    public MessageChannel jdbcChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow pollingFlow2() {
        return IntegrationFlows.from("jdbcChannel")
                .transform(Transformers.toJson())
                .wireTap(h -> h.handle(json -> System.out.println("==================> JSON: " + json.getPayload())))
                .channel("furtherProcessChannel")
                .get();
    }

}
