package com.deals.jeetodeals.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class Variations2 implements Serializable {
        public String attribute;

        @SerializedName("value")
        public Object value;

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        // Method to get value as string with better handling of different formats
        public String getValueAsString() {
            if (value == null) {
                return "";
            }

            if (value instanceof String) {
                return (String) value;
            } else if (value instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) value;
                if (map.isEmpty()) {
                    return "";
                } else {
                    // Try to get a meaningful value from the map
                    return map.toString();
                }
            } else {
                return value.toString();
            }
        }

}
