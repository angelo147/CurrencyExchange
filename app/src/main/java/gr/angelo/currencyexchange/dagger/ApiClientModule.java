package gr.angelo.currencyexchange.dagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gr.angelo.currencyexchange.retrofit.CurrencyResponseDeserializer;
import gr.angelo.currencyexchange.retrofit.models.Currency;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiClientModule {

    public final String BASE_URL;

    public ApiClientModule(String url) {
        this.BASE_URL = url;
    }

    @Provides
    @Singleton
    Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(createGsonConverter())
                .build();

    }
    private Converter.Factory createGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<Currency>() {
        }.getType(), new CurrencyResponseDeserializer());
        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);
    }
}
