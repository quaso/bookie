package org.bookie.web.utils;

/**
 * Created by kvasnicka on 1/13/17.
 */

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class EmberModel extends HashMap<String, Object> {

    private EmberModel() {
        //Must use the builder
    }

    public static class EmberBuilder<T> implements Builder<EmberModel> {
        private Map<String, Object> sideLoadedItems = new HashMap<>();
        private Map<String, Object> metaData = new HashMap<>();

        public EmberBuilder(String rootName, Object entity) {
            sideLoad(rootName, entity, false);
        }

        public EmberBuilder(Class<T> clazz, Collection<T> entities) {
            sideLoad(clazz, entities);
        }

        public EmberBuilder(Collection entities) {
            sideLoad(entities);
        }

        public EmberBuilder<T> addMeta(String key, Object value) {
            metaData.put(key, value);
            return this;
        }

        public EmberBuilder<T> sideLoad(String rootName, Object entity) {
            return sideLoad(rootName, entity, true);
        }

        private EmberBuilder<T> sideLoad(String rootName, Object entity, boolean asArray) {
            if (entity != null) {
                Object value = asArray ? Arrays.asList(entity) : entity;
                sideLoadedItems.put(rootName, value);
            }
            return this;
        }

        private EmberBuilder<T> sideLoad(Collection entity) {
            if (entity != null) {
                sideLoadedItems.put(entity.getClass().getSimpleName().toLowerCase(), entity);
            }
            return this;
        }

        public <K> EmberBuilder<T> sideLoad(Class<K> clazz, Collection<K> entities) {
            return this.sideLoad(clazz.getSimpleName().toLowerCase(), entities);
        }


        public <K> EmberBuilder<T> sideLoad(String rootName, Collection<K> entities) {
            if (entities != null) {
                sideLoadedItems.put(rootName, entities);
            }
            return this;
        }

        @Override
        public EmberModel build() {
            if (metaData.size() > 0) {
                sideLoadedItems.put("meta", metaData);
            }
            EmberModel sideLoader = new EmberModel();
            sideLoader.putAll(sideLoadedItems);
            return sideLoader;
        }
    }
}

