package org.bookie.web.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by kvasnicka on 1/13/17.
 */
public class EmberConfigManipulationUtil {
    public static EmberConfig parseConfig(String configJson) throws IOException {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };

        HashMap<String, Object> json = mapper.readValue(configJson, typeRef);
        return new EmberConfig(mapper, json);
    }

    public static class EmberConfig {
        private HashMap<String, Object> parsedJson;
        private ObjectMapper mapper;

        private EmberConfig(ObjectMapper mapper, HashMap<String, Object> parsedJson) {
            this.mapper = mapper;
            this.parsedJson = parsedJson;
        }

        public EmberConfig addConfig(String key, Object value) {
            String[] keyParts = key.split("\\.");
            HashMap<String, Object> foundValue = findByKey(keyParts, 0, parsedJson, true);
            if (foundValue == null) {
                throw new IllegalStateException("cannot add to JSON - key [" + key + "] not found");
            }
            String lastKey = keyParts[keyParts.length - 1];
            if (foundValue.containsKey(lastKey)){
                throw new IllegalStateException("cannot add to JSON - key [" + key + "] already exists");
            }
            foundValue.put(lastKey, value);
            return this;
        }

        public EmberConfig changeConfig(String key, Object newValue) {
            String[] keyParts = key.split("\\.");
            HashMap<String, Object> foundValue = findByKey(keyParts, 0, parsedJson, false);
            if (foundValue == null) {
                throw new IllegalStateException("cannot change JSON - key [" + key + "] not found");
            }
            String lastKey = keyParts[keyParts.length - 1];
            foundValue.put(lastKey, newValue);
            return this;
        }

        private HashMap<String, Object> findByKey(String[] keyParts, int startIndex, HashMap<String, Object> json, boolean createWhenNotFound) {

            if (keyParts.length == 1) {
                return json;
            }

            String key = keyParts[startIndex];
            Object obj = json.get(key);
            if (obj == null) {
                if (createWhenNotFound){
                    obj = new HashMap<String, Object>();
                    json.put(key, obj);
                }else {
                    return null;
                }
            }

            if (keyParts.length == startIndex + 2) {
                //penultimate key part
                return (HashMap<String, Object>) obj;
            }

            return findByKey(keyParts, startIndex + 1, (HashMap<String, Object>) obj, createWhenNotFound);
        }

        public String toJsonString() throws JsonProcessingException {
            return mapper.writeValueAsString(parsedJson);
        }
    }
}
