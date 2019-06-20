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

public class PasswordDialog extends DialogFragment {
    @BindView(R.id.newPassword)
    TextInputEditText newPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_password, null);
        ButterKnife.bind(this, v);
        builder.setTitle(R.string.update_password);
        builder.setView(v)
                .setPositiveButton(R.string.save, (dialog, id) -> {
                    listener.onDialogPositiveClick(newPassword.getText().toString());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    PasswordDialog.this.getDialog().cancel();
                });
        return builder.create();
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(String newPassword);
        //void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement MyDialogFragmentListener");
        }
    }
}
