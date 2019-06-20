package gr.angelo.currencyexchange.retrofit.models;

import java.util.ArrayList;
import java.util.List;

public class Rate {
    private float EUR;
    private float CAD;
    private float USD;
    private float BGN;
    private float NZD;
    private float ILS;
    private float RUB;
    private float PHP;
    private float CHF;
    private float ZAR;
    private float AUD;
    private float JPY;
    private float TRY;
    private float HKD;
    private float MYR;
    private float THB;
    private float HRK;
    private float NOK;
    private float IDR;
    private float DKK;
    private float CZK;
    private float HUF;
    private float GBP;
    private float MXN;
    private float KRW;
    private float ISK;
    private float SGD;
    private float BRL;
    private float PLN;
    private float INR;
    private float RON;
    private float CNY;
    private float SEK;

    private List<CurrencyRate> currencyRates = new ArrayList<>();

    public Rate() {
    }

    public void tt() {
        if(EUR!=0)
            currencyRates.add(new CurrencyRate("EUR", EUR));
        if(CAD!=0)
            currencyRates.add(new CurrencyRate("CAD", CAD));
        if(USD!=0)
            currencyRates.add(new CurrencyRate("USD", USD));
        if(BGN!=0)
            currencyRates.add(new CurrencyRate("BGN", BGN));
        if(ILS!=0)
            currencyRates.add(new CurrencyRate("ILS", ILS));
        if(RUB!=0)
        currencyRates.add(new CurrencyRate("RUB", RUB));
        if(PHP!=0)
        currencyRates.add(new CurrencyRate("PHP", PHP));
        if(CHF!=0)
        currencyRates.add(new CurrencyRate("CHF", CHF));
        if(ZAR!=0)
        currencyRates.add(new CurrencyRate("ZAR", ZAR));
        if(AUD!=0)
        currencyRates.add(new CurrencyRate("AUD", AUD));
        if(JPY!=0)
        currencyRates.add(new CurrencyRate("JPY", JPY));
        if(TRY!=0)
        currencyRates.add(new CurrencyRate("TRY", TRY));
        if(HKD!=0)
        currencyRates.add(new CurrencyRate("HKD", HKD));
        if(MYR!=0)
        currencyRates.add(new CurrencyRate("MYR", MYR));
        if(THB!=0)
        currencyRates.add(new CurrencyRate("THB", THB));
        if(HRK!=0)
        currencyRates.add(new CurrencyRate("HRK", HRK));
        if(NOK!=0)
        currencyRates.add(new CurrencyRate("NOK", NOK));
        if(IDR!=0)
        currencyRates.add(new CurrencyRate("IDR", IDR));
        if(DKK!=0)
        currencyRates.add(new CurrencyRate("DKK", DKK));
        if(CZK!=0)
        currencyRates.add(new CurrencyRate("CZK", CZK));
        if(HUF!=0)
        currencyRates.add(new CurrencyRate("HUF", HUF));
        if(GBP!=0)
        currencyRates.add(new CurrencyRate("GBP", GBP));
        if(MXN!=0)
        currencyRates.add(new CurrencyRate("MXN", MXN));
        if(KRW!=0)
        currencyRates.add(new CurrencyRate("KRW", KRW));
        if(ISK!=0)
        currencyRates.add(new CurrencyRate("ISK", ISK));
        if(SGD!=0)
        currencyRates.add(new CurrencyRate("SGD", SGD));
        if(BRL!=0)
        currencyRates.add(new CurrencyRate("BRL", BRL));
        if(PLN!=0)
        currencyRates.add(new CurrencyRate("PLN", PLN));
        if(INR!=0)
        currencyRates.add(new CurrencyRate("INR", INR));
        if(RON!=0)
        currencyRates.add(new CurrencyRate("RON", RON));
        if(CNY!=0)
        currencyRates.add(new CurrencyRate("CNY", CNY));
        if(SEK!=0)
        currencyRates.add(new CurrencyRate("SEK", SEK));
    }

    public List<CurrencyRate> getCurrencyRates() {
        return currencyRates;
    }

    public float getCAD() {
        return CAD;
    }

    public float getUSD() {
        return USD;
    }

    public float getBGN() {
        return BGN;
    }

    public float getNZD() {
        return NZD;
    }

    public float getILS() {
        return ILS;
    }

    public float getRUB() {
        return RUB;
    }

    public float getPHP() {
        return PHP;
    }

    public float getCHF() {
        return CHF;
    }

    public float getZAR() {
        return ZAR;
    }

    public float getAUD() {
        return AUD;
    }

    public float getJPY() {
        return JPY;
    }

    public float getTRY() {
        return TRY;
    }

    public float getHKD() {
        return HKD;
    }

    public float getMYR() {
        return MYR;
    }

    public float getTHB() {
        return THB;
    }

    public float getHRK() {
        return HRK;
    }

    public float getNOK() {
        return NOK;
    }

    public float getIDR() {
        return IDR;
    }

    public float getDKK() {
        return DKK;
    }

    public float getCZK() {
        return CZK;
    }

    public float getHUF() {
        return HUF;
    }

    public float getGBP() {
        return GBP;
    }

    public float getMXN() {
        return MXN;
    }

    public float getKRW() {
        return KRW;
    }

    public float getISK() {
        return ISK;
    }

    public float getSGD() {
        return SGD;
    }

    public float getBRL() {
        return BRL;
    }

    public float getPLN() {
        return PLN;
    }

    public float getINR() {
        return INR;
    }

    public float getRON() {
        return RON;
    }

    public float getCNY() {
        return CNY;
    }

    public float getSEK() {
        return SEK;
    }

    public float getEUR() {
        return EUR;
    }

    public float get(String code) {
        if(code.equals("EUR"))
            return getEUR();
        if(code.equals("USD"))
            return getUSD();
        if(code.equals("CAD"))
            return getCAD();
        if(code.equals("JPY"))
            return getJPY();
        if(code.equals("GBP"))
            return getGBP();
        if(code.equals("CNY"))
            return getCNY();
        return 0;
    }
}
