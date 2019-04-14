package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.squareup.picasso.Picasso;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import instahelper.ghonchegi.myfollower.Interface.AccountOptionChooserInterface;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogAccountActionBinding;
import instahelper.ghonchegi.myfollower.databinding.DialogAccountNeedsVerificationBinding;

@SuppressLint("ValidFragment")
public class AccountNeedsVerification extends DialogFragment {


    private DialogAccountNeedsVerificationBinding binding;


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
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
