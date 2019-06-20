package gr.angelo.currencyexchange.retrofit.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChartEntries extends RealmObject {
    @PrimaryKey
    private int id;
    private RealmList<MyMapEntry> myMap;

    public ChartEntries() {
    }

    public ChartEntries(Map<String, Rate> map, String chartTo) {
        myMap = new RealmList<>();
        List<String> collect1 = new ArrayList<>(map.keySet());
        collect1.forEach(key -> myMap.add(new MyMapEntry(Float.parseFloat(key), map.get(key).get(chartTo))));
    }

    public RealmList<MyMapEntry> getMyMap() {
        return myMap;
    }

    public void setMyMap(RealmList<MyMapEntry> myMap) {
        this.myMap = myMap;
    }
}
