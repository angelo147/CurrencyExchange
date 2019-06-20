package gr.angelo.currencyexchange;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.gauravk.bubblenavigation.IBubbleNavigation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.angelo.currencyexchange.dagger.FirebaseModule;
import gr.angelo.currencyexchange.utils.ConnectivityHelper;
import gr.angelo.currencyexchange.utils.FirestoreHelper;
import gr.angelo.currencyexchange.utils.Utils;
import gr.angelo.currencyexchange.retrofit.models.CurrencyRate;

public class BottomNavigation extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener,
        SelectCurrencyFragment.OnFragmentInteractionListener, AccountFragment.OnFragmentInteractionListener{
    private ActionBar toolbar;
    private final static int RC_SIGN_IN = 123;
    @BindView(R.id.top_navigation_constraint)
    IBubbleNavigation nav;
    private ItemFragment exchangeListFragment;
    private ChartFragment chartFragment;
    private AccountFragment accountFragment;
    final private FragmentManager fm = getSupportFragmentManager();
    private Fragment active;
    public static String base;
    private static String prevBase;
    public static String symbols;
    public final static String allSymbols = "EUR,GBP,CAD,USD,JPY,CNY";
    private SharedPreferences sharedPref;
    @Inject
    Utils utils;
    FirebaseUser user;
    @Inject @Named(FirebaseModule.AUTH)
    FirebaseAuth mAuth;
    @Inject @Named(FirebaseModule.FIRESTORE)
    FirestoreHelper mFirestore;

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null) {
            fm.beginTransaction()
                    .hide(active)
                    .show(fragment)
                    //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            active = fragment;
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        base = sharedPref.getString("base", "EUR");
        symbols = sharedPref.getString("symbols", allSymbols);
        prevBase = base;

        toolbar = getSupportActionBar();
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).getComponent().inject(this);
        utils.prepareSymbols(base);
        user = mAuth.getCurrentUser();

        exchangeListFragment = new ItemFragment();
        chartFragment = new ChartFragment();
        accountFragment = new AccountFragment();
        active = exchangeListFragment;
        fm.beginTransaction().add(R.id.fragment_container, exchangeListFragment,"exchangeListFragment").commit();
        fm.beginTransaction().add(R.id.fragment_container, chartFragment,"chartFragment").hide(chartFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, accountFragment,"accountFragment").hide(accountFragment).commit();
        nav.setCurrentActiveItem(0);
        nav.setNavigationChangeListener((view, position) -> {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    toolbar.setTitle(R.string.converter_title);
                    fragment = exchangeListFragment;
                    break;
                case 1:
                    toolbar.setTitle(R.string.chart_title);
                    fragment = chartFragment;
                    break;
                case 2:
                    if(user == null) {
                        utils.showSpotlight(this, findViewById(R.id.navigation_dashboard), nav);
                    } else {
                        fragment = accountFragment;
                        toolbar.setTitle(R.string.account_title);
                        accountFragment.updateUI(user);
                    }
                    break;
            }
            loadFragment(fragment);
        });
    }

    @Override
    public void onListFragmentInteraction(CurrencyRate item) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("base", item.getId());
        editor.apply();
        base = item.getId();
    }

    public void createSignInIntent(MenuItem item) {
        if(ConnectivityHelper.isConnectedToNetwork(this.getApplicationContext())) {
            if(user == null) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build());
                /*new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());*/

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.AppTheme)
                                .setLogo(R.mipmap.ic_launcher_currency)
                                .build(),
                        RC_SIGN_IN);
            } else
                Toast.makeText(BottomNavigation.this, R.string.already_logged_in,
                        Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(getWindow().getDecorView(), R.string.connection_required, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                user = mAuth.getCurrentUser();
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            exchangeListFragment.clearCache();
                            chartFragment.clearCache();
                            mFirestore.getPreferences(BottomNavigation.this, user, sharedPref);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.sync).setMessage(R.string.sync_diag_message).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            } else {
            }
        }
    }

    @Override
    public void onFragmentInteraction(String view, String selectedCurrencyCode) {
        SharedPreferences.Editor editor = sharedPref.edit();
        if(view.equals("base"))
            editor.putString("chartBase", selectedCurrencyCode);
        else if(view.equals("to"))
            editor.putString("chartTo", selectedCurrencyCode);
        editor.apply();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
