package gr.angelo.currencyexchange;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.angelo.currencyexchange.ItemFragment.OnListFragmentInteractionListener;
import gr.angelo.currencyexchange.dialogs.LoadingScreen;
import gr.angelo.currencyexchange.utils.ConnectivityHelper;
import gr.angelo.currencyexchange.utils.Utils;
import gr.angelo.currencyexchange.dummy.DummyContent.DummyItem;
import gr.angelo.currencyexchange.retrofit.ExchangeApi;
import gr.angelo.currencyexchange.retrofit.models.Currency;
import gr.angelo.currencyexchange.retrofit.models.CurrencyRate;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<CurrencyRate> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final FragmentManager fm;
    private DialogFragment load = LoadingScreen.getInstance();
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    private ExchangeApi exchangeApi;
    @Inject
    Retrofit retrofit;
    @Inject
    Utils utils;
    private Realm mRealm;

    public MyItemRecyclerViewAdapter(MyApplication application, List<CurrencyRate> items, OnListFragmentInteractionListener listener, FragmentManager fm, Realm mRealm) {
        mValues = items;
        mListener = listener;
        this.fm = fm;
        this.mRealm = mRealm;
        application.getComponent().inject(this);
        exchangeApi = retrofit.create(ExchangeApi.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    private void calculate(String value, List<CurrencyRate> cr) {
        cr.forEach(rate -> rate.setConvert(Float.parseFloat(value) * rate.getRate()));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mImage.setImageResource(mValues.get(position).getImage());
        holder.mIdView.setText(mValues.get(position).getId());
        if (position == 0) {
            holder.mEdit.setVisibility(View.VISIBLE);
            holder.mEdit.setText(String.valueOf(mValues.get(position).getConvert()));
            holder.mEdit.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    calculate(holder.mEdit.getText().toString(), mValues);
                    notifyDataSetChanged();
                }
                return false;
            });

            holder.mContentView.setVisibility(View.INVISIBLE);
            holder.mexchange.setVisibility(View.INVISIBLE);
            holder.mGrid.setVisibility(View.GONE);
            holder.mView.setBackgroundColor(Color.GRAY);
            holder.mIdView.setTextColor(Color.WHITE);
            holder.mContentView.setTextColor(Color.WHITE);
            holder.mDisplayName.setTextColor(Color.WHITE);
        }
        holder.mContentView.setText(String.valueOf(mValues.get(position).getConvert()));
        holder.mDisplayName.setText(mValues.get(position).getDisplayName());
        holder.mexchange.setText(String.format("1 %s = %s %s", mValues.get(position).getId(), String.valueOf(mValues.get(position).getExchange()), BottomNavigation.base));
        holder.mView.setOnClickListener(v -> {
            if(ConnectivityHelper.isConnectedToNetwork(v.getContext())) {
                if (null != mListener) {
                    getExchange(v, holder.mIdView.getText().toString());
                    Log.i("SELECTED", holder.mItem.getId());
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            } else {
                Snackbar.make(v, R.string.connection_required, Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        setAnimation(holder.mView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        @BindView(R.id.item_number)
        TextView mIdView;
        @BindView(R.id.content)
        TextView mContentView;
        @BindView(R.id.edit_)
        EditText mEdit;
        @BindView(R.id.imageView)
        ImageView mImage;
        @BindView(R.id.displayName)
        TextView mDisplayName;
        @BindView(R.id.exchange)
        TextView mexchange;
        @BindView(R.id.hidegrid)
        GridLayout mGrid;
        CurrencyRate mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void getExchange(View view, String base) {
        load.show(fm,"loader");
        utils.prepareSymbols(base);
        exchangeApi.getCurrency(base, BottomNavigation.symbols).enqueue(
                new Callback<Currency>() {
                    @Override
                    public void onResponse(Call<Currency> call, Response<Currency> response) {
                        load.dismiss();
                        if (response.isSuccessful()) {
                            Currency currency = response.body();
                            currency.getRates().tt();
                            mValues.clear();
                            mValues.add(new CurrencyRate(base, 1));
                            mValues.addAll(currency.getRates().getCurrencyRates());
                            notifyDataSetChanged();
                            mRealm.executeTransactionAsync(bgRealm -> {
                                bgRealm.delete(CurrencyRate.class);
                                bgRealm.copyToRealmOrUpdate(mValues);
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Currency> call, Throwable t) {
                        load.dismiss();
                        Log.e(MyItemRecyclerViewAdapter.class.getCanonicalName(), t.getMessage());
                    }
                });
    }
}
