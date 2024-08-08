package io.spring.dataflow.sample.usagecostprocessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class UsageCostProcessor {

    private final double ratePerSecond = 0.1;
    private final double ratePerMb = 0.05;

    @Bean
    public Function<UsageDetail, UsageCostDetail> processUsageCost(){

        return usageDetail -> {
            double callCost = usageDetail.duration() * this.ratePerSecond;
            double dataCost = usageDetail.data() * this.ratePerMb;

            return new UsageCostDetail(usageDetail.userId(), callCost, dataCost);
        };

    }

}
