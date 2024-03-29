package ka.follow.app2.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import ka.follow.app2.App;
import ka.follow.app2.R;
import ka.follow.app2.databinding.DialogNetworkErrorBinding;

import static ka.follow.app2.App.isNetworkAvailable;

public class NetworkErrorDialog extends DialogFragment {

    private DialogNetworkErrorBinding binding;
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_network_error, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion
        binding.btnReconnect.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                dismiss();
            }

        });
        return dialog;
    }


}
