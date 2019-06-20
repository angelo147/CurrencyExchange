package gr.angelo.currencyexchange;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.angelo.currencyexchange.dagger.FirebaseModule;
import gr.angelo.currencyexchange.dialogs.PasswordDialog;
import gr.angelo.currencyexchange.dialogs.ProfileDialog;
import gr.angelo.currencyexchange.utils.ConnectivityHelper;
import gr.angelo.currencyexchange.utils.FirestoreHelper;

public class AccountFragment extends Fragment implements ProfileDialog.NoticeDialogListener, PasswordDialog.NoticeDialogListener{
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.editProfile)
    Button edit;
    @BindView(R.id.editPassword)
    Button editPass;
    @BindView(R.id.logOut)
    Button logOut;
    @BindView(R.id.sync)
    Button sync;
    FirebaseUser user;
    @Inject
    @Named(FirebaseModule.FIRESTORE)
    FirestoreHelper mFirestore;

    private OnFragmentInteractionListener mListener;

    public AccountFragment() {
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateUI(FirebaseUser user) {
        this.user = user;
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        ((MyApplication) requireActivity().getApplication()).getComponent().inject(this);
        edit.setOnClickListener(v -> {
                    if(ConnectivityHelper.isConnectedToNetwork(view.getContext())) {
                        ProfileDialog d = ProfileDialog.newInstance(name.getText().toString(), email.getText().toString());
                        d.setTargetFragment(AccountFragment.this, 0);
                        d.show(getFragmentManager(), "profile");
                    } else {
                        Snackbar.make(view, R.string.connection_required, Snackbar.LENGTH_LONG)
                                .show();
                    }
        });
        editPass.setOnClickListener(v -> {
                    if(ConnectivityHelper.isConnectedToNetwork(view.getContext())) {
                        PasswordDialog d = new PasswordDialog();
                        d.setTargetFragment(AccountFragment.this, 0);
                        d.show(getFragmentManager(), "password");
                    } else {
                        Snackbar.make(view, R.string.connection_required, Snackbar.LENGTH_LONG)
                                .show();
                    }
        });
        logOut.setOnClickListener(v -> {
                    if(ConnectivityHelper.isConnectedToNetwork(view.getContext())) {
                        AuthUI.getInstance()
                                .signOut(view.getContext())
                                .addOnCompleteListener(task -> {
                                    Log.d("TT", "Logged out!");
                                    getActivity().finish();
                                    getActivity().startActivity(new Intent(view.getContext(), BottomNavigation.class));
                                });
                    } else {
                        Snackbar.make(view, R.string.connection_required, Snackbar.LENGTH_LONG)
                                .show();
                    }
        });
        sync.setOnClickListener(v -> mFirestore.savePreferences(user, mFirestore.localPrefToMap(requireActivity().getPreferences(Context.MODE_PRIVATE))));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onDialogPositiveClick(String displayName, String email) {
        if(!this.name.getText().toString().equals(displayName)) {
            this.name.setText(displayName);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TT", "User profile updated.");
                        }
                    });
        }
        if(!this.email.getText().toString().equals(email)) {
            this.email.setText(email);
            user.updateEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TT", "User email address updated.");
                        }
                    });
        }
    }

    @Override
    public void onDialogPositiveClick(String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TT", "User password updated.");
                    }
                });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
