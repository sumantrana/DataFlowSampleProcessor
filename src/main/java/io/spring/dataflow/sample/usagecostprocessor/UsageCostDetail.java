package io.spring.dataflow.sample.usagecostprocessor;

public record UsageCostDetail(String userId, double callCost, double dataCost) {
}
