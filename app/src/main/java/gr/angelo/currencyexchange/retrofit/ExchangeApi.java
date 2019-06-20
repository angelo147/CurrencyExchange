package gr.angelo.currencyexchange.retrofit;

import java.util.Map;

import gr.angelo.currencyexchange.retrofit.models.Currency;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by amenychtas on 19/03/2018.
 */

public interface ExchangeApi {
    @GET("latest")
    Call<Currency> getCurrency(@Query("base") String base, @Query("symbols") String symbols);
    @GET("latest")
    Call<Currency> getlatestCurrency(@QueryMap Map<String, String> options);
    @GET("history")
    Call<Currency> getCurrencyHistory(@Query("start_at") String start_at, @Query("end_at") String end_at,
                                      @Query("base") String base, @Query("symbols") String symbols);
    @GET("{date}")
    Call<Currency> getCurrencyForDate(@Path("date") String date);
}
