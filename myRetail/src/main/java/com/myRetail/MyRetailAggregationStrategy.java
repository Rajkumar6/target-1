package com.myRetail;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.routing.AggregationContext;
import org.mule.module.json.JsonData;
import org.mule.routing.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyRetailAggregationStrategy implements AggregationStrategy {
	private Logger LOGGER = LoggerFactory
			.getLogger(MyRetailAggregationStrategy.class);

	@Override
	public MuleEvent aggregate(AggregationContext context) throws MuleException {
		LOGGER.info("Processing aggregation...");
		Object price = null;
		Object name = null;
		for (MuleEvent event : context.collectEventsWithoutExceptions()) {
			JsonData data = (JsonData) event.getMessage().getPayload();
			LOGGER.info("Looking at payload: {}", data);
			if (data.hasNode("current_price")) {
				price = data.get("current_price");
				LOGGER.info("Found price: {}", price);
			} else {
				JsonNode items = data.get("product_composite_response").get("items");
				name = items.path(0).get("general_description").getTextValue();
				LOGGER.info("Found name: {}", name);
			}
		}

		if (price == null ) {
			throw new RuntimeException("Price not found ");
		}
		if (name == null ) {
			throw new RuntimeException("Name not found");
		}

		Map<Object, Object> payload = new HashMap<>();
		payload.put("current_price", price);
		payload.put("id", context.getOriginalEvent().getFlowVariable("id"));
		payload.put("name", name);
		MuleEvent result = DefaultMuleEvent.copy(context.getOriginalEvent());
		result.getMessage().setPayload(payload);
		return result;
	}

}
