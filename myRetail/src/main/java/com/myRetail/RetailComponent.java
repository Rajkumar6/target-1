package com.myRetail;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.mule.module.json.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;

public class RetailComponent implements Callable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		Object payload = eventContext.getMessage().getPayload();
		if (payload instanceof String) {
			String jsonString = (String) payload;
			if (!jsonString.startsWith("{")) {
				jsonString = "{" + jsonString + "}";
			}
			LOGGER.info("Parsing JSON to product: {}", jsonString);
			try {
				Product retVal = new GsonBuilder().setLenient().create()
						.fromJson(jsonString, Product.class);
				LOGGER.info("Return Product: {}", retVal);
				return retVal;
			} catch (Exception e) {
				LOGGER.error("Failed to deserialize GSON", e);
				throw new IllegalStateException(e);
			}
		} else  {
			LOGGER.error("Unknown payload type", payload.getClass());
			throw new IllegalStateException("Unknown paylaod type");
		}
	}

}
