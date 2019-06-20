package gr.angelo.currencyexchange;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.angelo.currencyexchange.dagger.RealmModule;
import gr.angelo.currencyexchange.dialogs.LoadingScreen;
import gr.angelo.currencyexchange.dummy.MyXAxisValueFormatter;
import gr.angelo.currencyexchange.retrofit.ExchangeApi;
import gr.angelo.currencyexchange.retrofit.models.ChartEntries;
import gr.angelo.currencyexchange.retrofit.models.Currency;
import gr.angelo.currencyexchange.utils.ConnectivityHelper;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

public class ChartFragment extends Fragment {
    @BindView(R.id.chart) LineChart chart;
    @BindView(R.id.chipGroup) ChipGroup chipGroup;
    @BindView(R.id.chip8) Chip chipBase;
    @BindView(R.id.chip7) Chip chipTo;
    @BindString(R.string._1m) String month;
    @BindString(R.string._1w) String week;
    @BindString(R.string._1y) String year;
    @BindString(R.string._3m) String threemonths;
    @BindString(R.string._5y) String fiveyears;
    @BindString(R.string._10y) String tenyears;
    @Inject
    Retrofit retrofit;
    @Inject
    @Named(RealmModule.HISTORY)
    RealmConfiguration hostoricalConf;
    private Realm mRealm;
    private ExchangeApi exchangeApi;
    private View v;
    private String chartBase;
    private String chartTo;
    private ChartEntries res;
    private DialogFragment load = LoadingScreen.getInstance();
    private enum CALL {
        ONLOAD, UPDATE
    }
    private RealmChangeListener realmListener = o -> {
        if(res == null)
            res = mRealm.where(ChartEntries.class).findFirst();
        if (res != null) {
            Map<Long, Float> map = new HashMap<>();
            res.getMyMap().forEach(el->map.put((long)el.getKey(),el.getValue()));
            loadChartDataa(map);
        }
    };

    public void clearCache() {
        res = null;
        mRealm.executeTransactionAsync(bgRealm -> bgRealm.delete(ChartEntries.class));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        v=view;
        calculateDate(week, CALL.ONLOAD);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(4, true);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());

        chipGroup.setOnCheckedChangeListener((chipGroup, i) -> {
            if(ConnectivityHelper.isConnectedToNetwork(v.getContext())) {
                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    calculateDate(chip.getText().toString(), CALL.UPDATE);
                }
            } else {
                Snackbar.make(v, getString(R.string.connection_required), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        chipBase.setChipIconResource(R.drawable.common_full_open_on_phone);
        chipBase.setText(chartBase);
        chipTo.setChipIconResource(R.drawable.common_full_open_on_phone);
        chipTo.setText(chartTo);
        chipBase.setOnClickListener(v -> {
            if(ConnectivityHelper.isConnectedToNetwork(v.getContext())) {
                swapFragment("base");
            } else {
                Snackbar.make(v, R.string.connection_required, Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        chipTo.setOnClickListener(v -> {
            if(ConnectivityHelper.isConnectedToNetwork(v.getContext())) {
                swapFragment("to");
            } else {
                Snackbar.make(v, R.string.connection_required, Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
    }

    private void swapFragment(String view){
        SelectCurrencyFragment newGamefragment = view.equals("base") ? SelectCurrencyFragment.newInstance(view, chartBase) : SelectCurrencyFragment.newInstance(view, chartTo);
        newGamefragment.setTargetFragment(ChartFragment.this, 23);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(ChartFragment.class.getName());
        fragmentTransaction.add(R.id.fragment_container, newGamefragment, "selectCurrencyFragment");
        fragmentTransaction.hide(this);//.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void calculateDate(String chipText, CALL call) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date now = null;
        Date dateBefore30Days = null;
        if(week.equalsIgnoreCase(chipText)) {
            now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DATE, -7);
            dateBefore30Days = cal.getTime();
        } else if(month.equalsIgnoreCase(chipText)) {
            now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.MONTH, -1);
            dateBefore30Days = cal.getTime();
        } else if(threemonths.equalsIgnoreCase(chipText)) {
            now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.MONTH, -3);
            dateBefore30Days = cal.getTime();
        } else if(year.equalsIgnoreCase(chipText)) {
            now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.YEAR, -1);
            dateBefore30Days = cal.getTime();
        } else if(fiveyears.equalsIgnoreCase(chipText)) {
            now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.YEAR, -5);
            dateBefore30Days = cal.getTime();
        } else if(tenyears.equalsIgnoreCase(chipText)) {
            now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.YEAR, -10);
            dateBefore30Days = cal.getTime();
        }
        getHistoricalData(formatter.format(dateBefore30Days), formatter.format(now), call);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        ((MyApplication) requireActivity().getApplication()).getComponent().inject(this);
        mRealm = Realm.getInstance(hostoricalConf);
        res = mRealm.where(ChartEntries.class).findFirst();
        mRealm.addChangeListener(realmListener);
        exchangeApi = retrofit.create(ExchangeApi.class);
        chartBase = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("chartBase", "EUR");
        chartTo = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("chartTo", "USD");
        return view;
    }

    public void getHistoricalData(String start, String end, CALL call) {
        load.show(getFragmentManager(),"loader");
        if(res == null || call == CALL.UPDATE) {
            exchangeApi.getCurrencyHistory(start, end, chartBase, chartTo).enqueue(new Callback<Currency>() {
                @Override
                public void onResponse(Call<Currency> call, Response<Currency> response) {
                    if (response.isSuccessful()) {
                        mRealm.executeTransactionAsync(bgRealm -> bgRealm.copyToRealmOrUpdate(new ChartEntries(response.body().getMyMap(), chartTo)));
                    }
                }

                @Override
                public void onFailure(Call<Currency> call, Throwable t) {
                    load.dismiss();
                    Log.e(ChartFragment.class.getCanonicalName(), t.getMessage());
                }
            });
        } else {
            Map<Long, Float> map = new HashMap<>();
            res.getMyMap().forEach(el->map.put((long)el.getKey(),el.getValue()));
            loadChartDataa(map);
        }
    }

    private void loadChartDataa(Map<Long, Float> map) {
        List<Long> collect1 = new ArrayList<>(map.keySet());
        collect1.sort((o1, o2) -> new Date(o1).compareTo(new Date(o2)));
        List<Entry> entries = new ArrayList<>();
        collect1.forEach(key -> entries.add(new Entry(key, map.get(key))));
        LineDataSet dataSet = new LineDataSet(entries, "Date"); // add entries to dataset
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setValueTextColor();
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.postInvalidate();
        load.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode==23){
                chartBase = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("chartBase", "EUR");
                chartTo = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("chartTo", "EUR");
                chipBase.setText(chartBase);
                chipTo.setText(chartTo);
                calculateDate(week, CALL.UPDATE);
            }
        }
    }
}
