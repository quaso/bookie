package org.bookie.web.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by kvasnicka on 1/13/17.
 */
public class EmberConfig {
	private final Map<String, Object> parsedJson;
	private final ObjectMapper mapper;

	private EmberConfig(final ObjectMapper mapper, final Map<String, Object> parsedJson) {
		this.mapper = mapper;
		this.parsedJson = parsedJson;
	}

	public static EmberConfig parseConfig(final String configJson) throws IOException {
		final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};

		final Map<String, Object> json = mapper.readValue(configJson, typeRef);
		return new EmberConfig(mapper, json);
	}

	public EmberConfig addConfig(final String key, final Object value) {
		final String[] keyParts = key.split("\\.");
		final Map<String, Object> foundValue = this.findByKey(keyParts, 0, this.parsedJson, true);
		final String lastKey = keyParts[keyParts.length - 1];
		foundValue.put(lastKey, value);
		return this;
	}

	public EmberConfig changeConfig(final String key, final Object newValue) {
		final String[] keyParts = key.split("\\.");
		final Map<String, Object> foundValue = this.findByKey(keyParts, 0, this.parsedJson, false);
		if (foundValue == null) {
			throw new IllegalStateException("cannot change JSON - key [" + key + "] not found");
		}
		final String lastKey = keyParts[keyParts.length - 1];
		foundValue.put(lastKey, newValue);
		return this;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> findByKey(final String[] keyParts, final int startIndex,
			final Map<String, Object> json, final boolean createWhenNotFound) {
		Map<String, Object> result = null;

		if (keyParts.length == 1) {
			result = json;
		} else {
			final String key = keyParts[startIndex];
			Object obj = json.get(key);
			if (obj == null && createWhenNotFound) {
				obj = new HashMap<String, Object>();
				json.put(key, obj);
			}

			if (obj != null) {
				if (keyParts.length == startIndex + 2) {
					//penultimate key part
					result = (Map<String, Object>) obj;
				} else {
					result = this.findByKey(keyParts, startIndex + 1, (Map<String, Object>) obj, createWhenNotFound);
				}
			}
		}
		return result;
	}

	public String toJsonString() throws JsonProcessingException {
		return this.mapper.writeValueAsString(this.parsedJson);
	}
}
