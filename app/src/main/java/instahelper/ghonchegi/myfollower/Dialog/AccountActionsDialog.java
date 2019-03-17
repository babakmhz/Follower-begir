package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.squareup.picasso.Picasso;

import instahelper.ghonchegi.myfollower.Interface.AccountOptionChooserInterface;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogAccountActionBinding;

@SuppressLint("ValidFragment")
public class AccountActionsDialog extends DialogFragment {
    private final int isActive;
    private final AccountOptionChooserInterface callBakck;
    private final String password;
    private DialogAccountActionBinding binding;
    private String profilePic, userName;

    @SuppressLint("ValidFragment")
    public AccountActionsDialog(String profilePicture, String userName, int isActive, AccountOptionChooserInterface callBack,String password) {
        this.userName = userName;
        this.profilePic = profilePicture;
        this.isActive=isActive;
        this.callBakck= callBack;
        this.password = password;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_account_action, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion

        Picasso.get().load(profilePic).fit().into(binding.imgProfileImage);
        binding.tvUserName.setText(userName);
        if (isActive==1){binding.btnActivateAccount.setEnabled(false);}

        binding.btnActivateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBakck.changedInfo(userName,password);
                dismiss();
            }
        });
        binding.btnRemoveAccount.setOnClickListener( v->{
            callBakck.changedInfo(userName,password);
            dismiss();
        });

        return dialog;
    }


}