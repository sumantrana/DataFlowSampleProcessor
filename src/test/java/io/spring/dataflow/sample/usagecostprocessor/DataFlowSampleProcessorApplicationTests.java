package io.spring.dataflow.sample.usagecostprocessor;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataFlowSampleProcessorApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void UsageCostProcessor_SubscribesUsageDetails_PublishesProcessedCostMessage(){

        try(ConfigurableApplicationContext context  = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(DataFlowSampleProcessorApplication.class))
                .web(WebApplicationType.NONE)
                .run())
        {
            InputDestination source = context.getBean(InputDestination.class);

            CompositeMessageConverter messageConverter = context.getBean(CompositeMessageConverter.class);

            UsageDetail usageDetail = new UsageDetail("user1", 100L, 100L);

            Map<String, Object> headers = new HashMap<>();
            headers.put("contentType", "application/json");
            MessageHeaders messageHeaders = new MessageHeaders(headers);

            Message<?> message = messageConverter.toMessage(usageDetail, messageHeaders);

            source.send(message);

            OutputDestination sink = context.getBean(OutputDestination.class);

            Message<byte[]> incomingMessage = sink.receive(1000, "usage-cost");
            UsageCostDetail usageCostDetail = (UsageCostDetail) messageConverter.fromMessage(incomingMessage, UsageCostDetail.class);

            assertThat(usageCostDetail.userId()).isEqualTo("user1");
            assertThat(usageCostDetail.callCost()).isEqualTo(10);
            assertThat(usageCostDetail.dataCost()).isEqualTo(5);

        }
    }
}
