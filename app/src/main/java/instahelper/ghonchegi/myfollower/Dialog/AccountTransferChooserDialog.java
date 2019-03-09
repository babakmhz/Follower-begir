package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.squareup.picasso.Picasso;

import instahelper.ghonchegi.myfollower.Adapters.AccountTransferChooserAdapter;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Interface.AccountChangerInterface;
import instahelper.ghonchegi.myfollower.Interface.AccountOptionChooserInterface;
import instahelper.ghonchegi.myfollower.Interface.AccountTransferInfoInterface;
import instahelper.ghonchegi.myfollower.Interface.ExternalAccountTransferChooserInsterface;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogManageAccountsBinding;

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
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_manage_accounts, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion
        internalCallback = this;
        binding.imvAdd.setVisibility(View.GONE);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        AccountTransferChooserAdapter adapter = new AccountTransferChooserAdapter(dataBaseHelper.getAllUsers(), internalCallback,dialog);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

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
                DataBaseHelper dataBaseHelper1 = new DataBaseHelper(getActivity());
                dataBaseHelper.setAllValueNotActive();
                authenticate();
            }
        });
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);
        return dialog;
    }

    private void authenticate() {
        // startActivity(new Intent(this,ActivityLoginWebview.class));
        InstagramAutenticationDialog dialog = new InstagramAutenticationDialog(false, null, null);
        dialog.setCancelable(true);
        dialog.show(getFragmentManager(), ":");


    }




    @Override
    public void sendUUID(String uuid, String profilePic) {
        externalCallBack.setUUID(uuid,profilePic);


    }
}
