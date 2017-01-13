package org.bookie.web.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kvasnicka on 1/13/17.
 */
public class EmberErrorResponse {
    private List<EmberError> errors = new LinkedList<>();

    public EmberErrorResponse() {
    }



   /* public void addError(String[] fields, String fieldTranslationCode, String message, String identifier) {
        errors.add(new EmberError(fields, fieldTranslationCode, message, identifier));
    }*/


    public static class EmberError {
        private String[] fields;
        private String[] result;
        private ErrorParams params;

        public EmberError(String[] fields, String fieldTranslationCode, String message, String identifier) {
            this.fields = fields;
            this.params = new ErrorParams(fieldTranslationCode, identifier);
            this.result = new String[]{message};
        }

        public String[] getFields() {
            return fields;
        }

        public void setFields(String[] fields) {
            this.fields = fields;
        }

        public String[] getResult() {
            return result;
        }

        public void setResult(String[] result) {
            this.result = result;
        }

        public ErrorParams getParams() {
            return params;
        }

        public void setParams(ErrorParams params) {
            this.params = params;
        }


        private class ErrorParams {
            private final String fieldName;
            private final String identifier;

            public ErrorParams(String fieldTranslationCode, String identifier) {
                this.fieldName = fieldTranslationCode;
                this.identifier = identifier;
            }

            public String getFieldName() {
                return fieldName;
            }

            public String getIdentifier() {
                return identifier;
            }
        }
    }

}
