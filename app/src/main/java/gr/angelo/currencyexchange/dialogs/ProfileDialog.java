package gr.angelo.currencyexchange.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import gr.angelo.currencyexchange.R;

public class ProfileDialog extends DialogFragment {
    @BindView(R.id.displayName)
    TextInputEditText name;
    @BindView(R.id.dialogEmail)
    TextInputEditText email;
    private static final String DISPLAY_NAME = "name";
    private static final String EMAIL = "email";
    private String nameText;
    private String emailText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_profile, null);
        ButterKnife.bind(this, v);
        name.setText(nameText);
        email.setText(emailText);
        builder.setTitle(R.string.update_profile);
        builder.setView(v)
                .setPositiveButton(getString(R.string.save), (dialog, id) -> {
                    listener.onDialogPositiveClick(name.getText().toString(), email.getText().toString());
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
                    ProfileDialog.this.getDialog().cancel();
                });
        return builder.create();
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(String displayName, String email);
        //void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nameText = getArguments().getString(DISPLAY_NAME);
            emailText = getArguments().getString(EMAIL);
        }
        try {
            listener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement MyDialogFragmentListener");
        }
    }

    public static ProfileDialog newInstance(String name, String email) {
        ProfileDialog fragment = new ProfileDialog();
        Bundle args = new Bundle();
        args.putString(DISPLAY_NAME, name);
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }
}
