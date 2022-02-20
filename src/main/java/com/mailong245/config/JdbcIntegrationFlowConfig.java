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
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;

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
                .wireTap(h -> h.handle(json -> System.out.println("==================> JSON: " + json.getPayload())))
                .channel("furtherProcessChannel")
                .get();
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
