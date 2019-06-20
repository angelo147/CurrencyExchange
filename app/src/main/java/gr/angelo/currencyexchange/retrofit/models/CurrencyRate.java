package gr.angelo.currencyexchange.retrofit.models;

import gr.angelo.currencyexchange.R;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CurrencyRate extends RealmObject {
    @PrimaryKey
    private String id;
    private float rate;
    private int image;
    private String displayName;
    private float exchange;
    private float convert;

    public CurrencyRate() {
    }

    public CurrencyRate(String id, float rate) {
        this.id = id;
        this.rate = rate;
        setImageSource();
        this.displayName = android.icu.util.Currency.getInstance(this.id).getDisplayName();
        this.exchange = (float)1.0/this.rate;
        this.convert = this.rate;
    }

    public float getConvert() {
        return convert;
    }

    public void setConvert(float convert) {
        this.convert = convert;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public float getExchange() {
        return exchange;
    }

    public void setExchange(float exchange) {
        this.exchange = exchange;
    }

    private void setImageSource() {
        if (id.equalsIgnoreCase("USD")) {
            image=R.drawable.ic_usa_flag;
        } else if (id.equalsIgnoreCase("GBP")) {
            image=R.drawable.ic_gb_flag;
        } else if (id.equalsIgnoreCase("EUR")) {
            image=R.drawable.ic_euro_flag;
        } else if (id.equalsIgnoreCase("CAD")) {
            image=R.drawable.ic_can_flag;
        } else if (id.equalsIgnoreCase("JPY")) {
            image=R.drawable.ic_japan_flag;
        } else if (id.equalsIgnoreCase("CNY")) {
            image=R.drawable.ic_china_flag;
        }
    }
}
