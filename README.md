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

### 3. Instruction:

- Step 1: Go to main method at SpringIntegrationApplication.java
- Step 2: Change value of int CHOICE according to the static block to choose which example to be run
- Step 3: Run the project (press q to terminate)