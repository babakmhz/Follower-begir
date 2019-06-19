package ir.novahar.followerbegir.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import ir.novahar.followerbegir.App;
import ir.novahar.followerbegir.R;
import ir.novahar.followerbegir.databinding.DialogAccountNeedsVerificationBinding;

@SuppressLint("ValidFragment")
public class AccountNeedsVerification extends DialogFragment {


    private DialogAccountNeedsVerificationBinding binding;


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_account_needs_verification, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion


        binding.btnActivateAccount.setOnClickListener(v -> dismiss());
        return dialog;
    }


}
