package com.follow.irani.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.follow.irani.Adapters.AccountTransferChooserAdapter;
import com.follow.irani.App;
import com.follow.irani.Interface.AccountTransferInfoInterface;
import com.follow.irani.Interface.ExternalAccountTransferChooserInsterface;
import com.follow.irani.Manager.DataBaseHelper;
import com.follow.irani.R;
import com.follow.irani.databinding.DialogManageAccountsBinding;
import com.squareup.picasso.Picasso;

@SuppressLint("ValidFragment")
public class AccountTransferChooserDialog extends DialogFragment implements AccountTransferInfoInterface {

    DialogManageAccountsBinding binding;
    private ExternalAccountTransferChooserInsterface externalCallBack;
    private AccountTransferInfoInterface internalCallback;

    public AccountTransferChooserDialog(ExternalAccountTransferChooserInsterface callBack) {
        this.externalCallBack = callBack;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_manage_accounts, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion
        internalCallback = this;
        binding.imvAdd.setVisibility(View.GONE);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(App.currentActivity);
        AccountTransferChooserAdapter adapter = new AccountTransferChooserAdapter(dataBaseHelper.getAllUsers(), internalCallback,dialog);

        DividerItemDecoration decoration = new DividerItemDecoration(App.currentActivity, DividerItemDecoration.VERTICAL);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(App.currentActivity, LinearLayoutManager.VERTICAL, false);
        binding.imvBack.setOnClickListener(v -> dialog.dismiss());
        binding.tvUserName.setText(App.user.getUserName());

        binding.rcvAccounts.setLayoutManager(mLayoutManager);
        binding.rcvAccounts.setItemAnimator(new DefaultItemAnimator());
        binding.rcvAccounts.setAdapter(adapter);
        binding.rcvAccounts.addItemDecoration(decoration);
        binding.rcvAccounts.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rcvAccounts.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < binding.rcvAccounts.getChildCount(); i++) {
                    View v = binding.rcvAccounts.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });


        binding.imvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper dataBaseHelper1 = new DataBaseHelper(App.currentActivity);
                dataBaseHelper.setAllValueNotActive();
                authenticate();
            }
        });
        Picasso.get().load(App.profilePicURl).into(binding.profileImage);
        binding.tvUserName.setText(App.user.getUserName());
        binding.tvLikeCoinCount.setText(App.likeCoin + "");
        binding.tvFollowCoinCount.setText(App.followCoin + "");
        return dialog;
    }

    private void authenticate() {
        // startActivity(new Intent(this,ActivityLoginWebview.class));
        AuthenticationDialog dialog = new AuthenticationDialog(false, null, null);
        dialog.setCancelable(true);
        dialog.show(getFragmentManager(), ":");


    }




    @Override
    public void sendUUID(String uuid, String profilePic) {
        externalCallBack.setUUID(uuid,profilePic);


    }
}
