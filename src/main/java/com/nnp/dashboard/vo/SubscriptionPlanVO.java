package com.nnp.dashboard.vo;

import java.util.List;
import java.util.Map;

public record SubscriptionPlanVO(
        Long subscribedPlanId,
        String subscribedPlan,
        String subscribedPlanPrice,
        String subscribedPlanCurrency,
        String subscribedPlanBaseDct,
        String subscribedPlanBasePrice,
        Map<String,List<PlanComponent>> planComponents
) {
    public record PlanComponent(
            String componentId,
            String componentName,
            PriceVO pricePerDay,
            List<PlanFeature> features
    ) {}
    public record PlanFeature(
            String featureName,
            String featureId
    ) {}
}


