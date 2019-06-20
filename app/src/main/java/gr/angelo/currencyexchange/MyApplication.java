package gr.angelo.currencyexchange;

import android.app.Application;

import gr.angelo.currencyexchange.dagger.ApiClientModule;
import gr.angelo.currencyexchange.dagger.ApiComponent;
import gr.angelo.currencyexchange.dagger.AppModule;
import gr.angelo.currencyexchange.dagger.DaggerApiComponent;
import gr.angelo.currencyexchange.dagger.FirebaseModule;
import gr.angelo.currencyexchange.dagger.RealmModule;
import gr.angelo.currencyexchange.dagger.UtilsModule;
import io.realm.Realm;

public class MyApplication extends Application {

    private ApiComponent mApiComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        mApiComponent = DaggerApiComponent.builder()
                .appModule(new AppModule(this))
                .apiClientModule(new ApiClientModule("https://api.exchangeratesapi.io/"))
                .utilsModule(new UtilsModule())
                .realmModule(new RealmModule())
                .firebaseModule(new FirebaseModule())
                .build();
    }

    public ApiComponent getComponent() {
        return mApiComponent;
    }
}
