package gr.angelo.currencyexchange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.angelo.currencyexchange.utils.Utils;

public class SelectCurrencyFragment extends Fragment {
    private static final String BASE_OR_TO = "base_or_to";
    private static final String SELECTED = "selected";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    @BindView(R.id.radiogroup)
    RadioGroup radioGroup;
    @Inject
    Utils utils;
    Context ctx;

    public SelectCurrencyFragment() {
    }

    public static SelectCurrencyFragment newInstance(String param1, String param2) {
        SelectCurrencyFragment fragment = new SelectCurrencyFragment();
        Bundle args = new Bundle();
        args.putString(BASE_OR_TO, param1);
        args.putString(SELECTED, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this.getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(BASE_OR_TO);
            mParam2 = getArguments().getString(SELECTED);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, () -> {
            Intent intent = new Intent(ctx, SelectCurrencyFragment.class);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getFragmentManager().popBackStack();
            return true;
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        ((MyApplication) requireActivity().getApplication()).getComponent().inject(this);
        utils.getAllSymbols().forEach(s -> {
            RadioButton r = new RadioButton(view.getContext());
            r.setText(s);
            r.setId(RadioButton.generateViewId());
            radioGroup.addView(r);
            if(s.equals(mParam2))
                radioGroup.check(r.getId());
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();
            if (isChecked)
            {
                mListener.onFragmentInteraction(mParam1, checkedRadioButton.getText().toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_currency, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String view, String selectedCurrencyCode);
    }

}
