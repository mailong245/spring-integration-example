# spring-integration-example

### 1. Flow:
- Messages will be produced come from **MessageSource** or **@InboundChannelAdapter**
- MessageSource or @InboundChannelAdapter will choose the **MessageChannel** to send data
- Channel will send data to **@ServiceActivator** or **MessageHandler**
- Bridge can be used to connect or forward channel


### 2. Description:
- BasicIntegrationConfig.class: Basic flow of Spring integration
- IntegrationFlowConfig.class: Basic flow but using IntegrationFlow instance
- JdbcIntegrationFlowConfig.class: Using IntegrationFlow and crawling data using JDBC