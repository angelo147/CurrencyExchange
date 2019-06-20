package gr.angelo.currencyexchange.retrofit.models;

import io.realm.RealmObject;

public class MyMapEntry extends RealmObject {
    private float key;
    private float value;

    public MyMapEntry() {
    }

    public MyMapEntry(float key, float value) {
        this.key = key;
        this.value = value;
    }

    public float getKey() {
        return key;
    }

    public void setKey(float key) {
        this.key = key;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
