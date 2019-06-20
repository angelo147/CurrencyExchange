package gr.angelo.currencyexchange.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gr.angelo.currencyexchange.utils.Utils;

@Module
public class UtilsModule {
    public UtilsModule() {
    }

    @Provides
    @Singleton
    Utils getUtils() {
        return new Utils();
    }
}
