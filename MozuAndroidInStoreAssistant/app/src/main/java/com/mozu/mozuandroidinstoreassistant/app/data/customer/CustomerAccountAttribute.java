package com.mozu.mozuandroidinstoreassistant.app.data.customer;

        import com.mozu.mozuandroidinstoreassistant.app.data.IData;

public class CustomerAccountAttribute implements IData {

    private String Property;
    private String Value;

    public String getProperty() {
        return Property;
    }

    public void setProperty(String property) {
        Property = property;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
