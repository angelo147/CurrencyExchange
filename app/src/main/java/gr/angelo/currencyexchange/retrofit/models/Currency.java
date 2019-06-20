package gr.angelo.currencyexchange.retrofit.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Currency {
    private String base;
    private Date date;
    private Rate rates;
    private Map<String,Rate> myMap;
    private Date end_at;
    private Date start_at;

    public Currency() {
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Rate getRates() {
        return rates;
    }

    public void setRates(Rate rates) {
        this.rates = rates;
    }

    public Map<String, Rate> getMyMap() {
        return myMap;
    }

    public void setMyMap(Map<String, Rate> myMap) {
        this.myMap = myMap;
    }

    public Date getEnd_at() {
        return end_at;
    }

    public void setEnd_at(Date end_at) {
        this.end_at = end_at;
    }

    public Date getStart_at() {
        return start_at;
    }

    public void setStart_at(Date start_at) {
        this.start_at = start_at;
    }
}
