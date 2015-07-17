package com.mozu.mozuandroidinstoreassistant.app.product;

import java.util.HashMap;

public class ProductOptionsContainer {

    HashMap<String, String> optionsMap = new HashMap<String, String>();

    public void add(String attributeFQN, String value) {
        optionsMap.put(attributeFQN, value);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductOptionsContainer that = (ProductOptionsContainer) o;
        if (optionsMap != null) {
            for (String e : that.optionsMap.keySet()) {
                if (!optionsMap.containsKey(e))
                    return false;
                if (!optionsMap.get(e).equals(that.optionsMap.get(e)))
                    return false;
            }
            return true;
        } else {
            return that.optionsMap != null;
        }
    }

}
