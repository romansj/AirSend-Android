package com.cherrydev.airsend.app;

import com.cherrydev.airsend.app.settings.Preference;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

class PreferenceUtilsTest {

    public interface DynamicTypeValue {
        String valueDescription();
    }

    public class IntegerTypeValue implements DynamicTypeValue {
        private Integer value;

        public IntegerTypeValue(Integer value) {
            this.value = value;
        }

        @Override
        public String valueDescription() {
            if (value == null) {
                return "The value is null.";
            }
            return String.format("The value is a %s integer: %d", value > 0 ? "positive" : "negative", value);
        }
    }

    public class InstantTypeValue implements DynamicTypeValue {

        private Instant value;

        public InstantTypeValue(Instant value) {
            this.value = value;
        }

        @Override
        public String valueDescription() {
            // null handling omitted
            return String.format("The value is an instant: %s", value.toString());
        }
    }


    @Test
    void name2() {
        Map<String, DynamicTypeValue> theMap = new HashMap<>();
        theMap.put("E1 (Integer)", new IntegerTypeValue(2));
        theMap.put("E3 (Instant)", new InstantTypeValue(Instant.now()));


        theMap.forEach((k, v) -> System.out.println(k + " -> " + v.valueDescription()));

        DynamicTypeValue value = theMap.get("E1 (Integer)");
        String simpleName = value.getClass().getSimpleName();
        System.out.println(simpleName);

        System.out.println("instanceOf IntegerTypeValue " + (value instanceof IntegerTypeValue));
    }

    @Test
    void name() {
        var preference = new Preference<>("1", "hello", Preference.PreferenceType.STRING);
        var preference2 = new Preference<>("2", 1, Preference.PreferenceType.INT);

        Map<String, Preference<?>> map = new HashMap<>();
        map.put(preference.getId(), preference);
        map.put(preference2.getId(), preference2);

        Preference<?> preferenceRetrieved = map.get("1");
        Object defaultValue = preferenceRetrieved.getDefaultValue();

        switch (preferenceRetrieved.getType()) {
            case STRING: {
                var defaultValueCasted = (String) defaultValue;
                System.out.println("String" + defaultValueCasted);
                break;
            }

            case INT: {
                var defaultValueCasted = (int) defaultValue;
                System.out.println("int" + defaultValueCasted);
                break;
            }
        }
    }
}