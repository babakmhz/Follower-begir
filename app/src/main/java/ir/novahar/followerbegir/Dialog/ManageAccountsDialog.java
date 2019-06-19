package ir.novahar.followerbegir.Dialog;

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

import com.squareup.picasso.Picasso;

import ir.novahar.followerbegir.Adapters.AccountsListAdapter;
import ir.novahar.followerbegir.App;
import ir.novahar.followerbegir.Interface.AccountChangerInterface;
import ir.novahar.followerbegir.Interface.AccountOptionChooserInterface;
import ir.novahar.followerbegir.Manager.DataBaseHelper;
import ir.novahar.followerbegir.R;
import ir.novahar.followerbegir.databinding.DialogManageAccountsBinding;

@SuppressLint("ValidFragment")
public class ManageAccountsDialog extends DialogFragment implements AccountOptionChooserInterface {

    DialogManageAccountsBinding binding;
    private AccountChangerInterface externalCallBack;
    private AccountOptionChooserInterface internalCallback;

    public ManageAccountsDialog(AccountChangerInterface callBack) {
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
        binding.tvReturn.setOnClickListener(v -> dialog.dismiss());
        binding.tvUserName.setText(App.user.getUserName());
        //endregion
        internalCallback = this;
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);

        showUsers();
        binding.imvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });

        return dialog;
    }

    private void showUsers() {

        DataBaseHelper dataBaseHelper = new DataBaseHelper(App.currentActivity);
        AccountsListAdapter adapter = new AccountsListAdapter(dataBaseHelper.getAllUsers(), getChildFragmentManager(), internalCallback);


        DividerItemDecoration decoration = new DividerItemDecoration(App.currentActivity, DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(App.currentActivity, LinearLayoutManager.VERTICAL, false);

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

    }

    private void authenticate() {
        // startActivity(new Intent(this,ActivityLoginWebview.class));
        AuthenticationDialog dialog = new AuthenticationDialog(false, null, null);
        dialog.setCancelable(true);
        dialog.show(getChildFragmentManager(), ":");


    }


    @Override
    public void changedInfo(String username, String password) {
        externalCallBack.selectToChange(username, password);
        dismiss();
    }

    @Override
    public void onDelete(boolean isDeleted) {
        if (isDeleted) {
            showUsers();

        }
    }
}
