package gr.angelo.currencyexchange.retrofit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import gr.angelo.currencyexchange.retrofit.models.Currency;
import gr.angelo.currencyexchange.retrofit.models.Rate;

public class CurrencyResponseDeserializer implements JsonDeserializer<Currency> {
    @Override
    public Currency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Currency response = new Gson().fromJson(json, Currency.class);

        if(response.getDate()==null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Map<String, Rate> map = new HashMap<>();
            //map = (Map<Date,Rate>) new Gson().fromJson(json, map.getClass());

            //response.setMyMap((Map<Date,Rate>)map.get("rates"));
            //return response;

            //Map<String, String> result = new HashMap<String, String>();

            //JsonArray array = json.getAsJsonArray();
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement paramsElement = jsonObject.get("rates");
            JsonObject parametersObject = paramsElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : parametersObject.entrySet()) {
                String key = null;
                try {
                    key = String.valueOf(formatter.parse(entry.getKey()).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //String key = String.valueOf(formatter.parse(entry.getKey()).getTime());
                Rate value = new Gson().fromJson(entry.getValue(), Rate.class);
                map.put(key, value);
            }
            //}

            response.setMyMap(map);
        }
        return response;
    }
}
