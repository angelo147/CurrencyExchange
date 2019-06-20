package gr.angelo.currencyexchange.dagger;

import javax.inject.Singleton;

import dagger.Component;
import gr.angelo.currencyexchange.AccountFragment;
import gr.angelo.currencyexchange.BottomNavigation;
import gr.angelo.currencyexchange.ChartFragment;
import gr.angelo.currencyexchange.ItemFragment;
import gr.angelo.currencyexchange.MyItemRecyclerViewAdapter;
import gr.angelo.currencyexchange.SelectCurrencyFragment;

@Singleton
@Component(modules = {ApiClientModule.class, AppModule.class, UtilsModule.class, RealmModule.class, FirebaseModule.class})
public interface ApiComponent {

    void inject(BottomNavigation bottomNavigation);
    void inject(ItemFragment fragment);
    void inject(ChartFragment fragment);
    void inject(MyItemRecyclerViewAdapter adapter);
    void inject(SelectCurrencyFragment fragment);
    void inject(AccountFragment accountFragment);
}
