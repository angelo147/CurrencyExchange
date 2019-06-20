package gr.angelo.currencyexchange.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module
public class RealmModule {
    public final static String CONVERTER = "converter";
    public final static String HISTORY = "history";
    private RealmConfiguration converterConf;
    private RealmConfiguration hostoricalConf;

    public RealmModule() {
        converterConf = new RealmConfiguration.Builder().name("currency.converter").build();
        hostoricalConf = new RealmConfiguration.Builder().name("currency.history").build();
    }

    @Provides
    @Singleton
    @Named(CONVERTER)
    RealmConfiguration getRealmConverter() {
        return converterConf;
    }

    @Provides
    @Singleton
    @Named(HISTORY)
    RealmConfiguration getRealmHistoryData() {
        return hostoricalConf;
    }
}
