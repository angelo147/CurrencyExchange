package gr.angelo.currencyexchange;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.angelo.currencyexchange.dagger.RealmModule;
import gr.angelo.currencyexchange.dialogs.LoadingScreen;
import gr.angelo.currencyexchange.dummy.DummyContent;
import gr.angelo.currencyexchange.retrofit.ExchangeApi;
import gr.angelo.currencyexchange.retrofit.models.Currency;
import gr.angelo.currencyexchange.retrofit.models.CurrencyRate;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {
    private ExchangeApi exchangeApi;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyItemRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private DialogFragment load = LoadingScreen.getInstance();
    @Inject
    Retrofit retrofit;
    @Inject
    @Named(RealmModule.CONVERTER)
    RealmConfiguration converterConf;
    private Realm realm;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ((MyApplication) requireActivity().getApplication()).getComponent().inject(this);
        realm = Realm.getInstance(converterConf);
        exchangeApi = retrofit.create(ExchangeApi.class);
        recyclerView = (RecyclerView) view;
        getExchange(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(CurrencyRate item);
    }

    private void setUpRecyclerView(Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyItemRecyclerViewAdapter((MyApplication) requireActivity().getApplication(), DummyContent.ITEMSS, mListener, getFragmentManager(), realm);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void getExchange(View view) {
        load.show(getFragmentManager(),"loader");
        Context context = view.getContext();

        RealmResults<CurrencyRate> res = realm.where(CurrencyRate.class).findAll();
        DummyContent.ITEMSS = realm.copyFromRealm(res);
        if (DummyContent.ITEMSS.isEmpty()) {
            exchangeApi.getCurrency(BottomNavigation.base, BottomNavigation.symbols).enqueue(
                    new Callback<Currency>() {
                        @Override
                        public void onResponse(Call<Currency> call, Response<Currency> response) {
                            load.dismiss();
                            if (response.isSuccessful()) {
                                Currency currency = response.body();
                                currency.getRates().tt();
                                DummyContent.ITEMSS.clear();
                                DummyContent.ITEMSS.add(new CurrencyRate(BottomNavigation.base, 1));
                                DummyContent.ITEMSS.addAll(currency.getRates().getCurrencyRates());
                                realm.executeTransactionAsync(bgRealm -> bgRealm.copyToRealmOrUpdate(DummyContent.ITEMSS));
                            }
                            setUpRecyclerView(context);
                        }

                        @Override
                        public void onFailure(Call<Currency> call, Throwable t) {
                            load.dismiss();
                            Log.e(ItemFragment.class.getCanonicalName(), t.getMessage());
                        }
                    });
        } else {
            load.dismiss();
            setUpRecyclerView(context);
        }
    }

    public void clearCache() {
        realm.executeTransactionAsync(bgRealm -> bgRealm.delete(CurrencyRate.class));
    }
}
