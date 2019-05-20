package ka.follow.app2.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Objects;

import ka.follow.app2.App;
import ka.follow.app2.Interface.AccountOptionChooserInterface;
import ka.follow.app2.Manager.DataBaseHelper;
import ka.follow.app2.R;
import ka.follow.app2.databinding.DialogAccountActionBinding;
import ka.follow.app2.instaAPI.InstaApiException;
import ka.follow.app2.instaAPI.InstagramApi;

import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class AccountActionsDialog extends DialogFragment {
    private final int isActive;
    private final AccountOptionChooserInterface callBakck;
    private final String password;
    private final String userId;
    private final String UUid;
    private DialogAccountActionBinding binding;
    private String profilePic, userName;

    @SuppressLint("ValidFragment")
    public AccountActionsDialog(String profilePicture, String userName, int isActive, AccountOptionChooserInterface callBack, String password, String userId, String uuid) {
        this.userName = userName;
        this.profilePic = profilePicture;
        this.isActive=isActive;
        this.callBakck= callBack;
        this.password = password;
        this.userId = userId;
        this.UUid = uuid;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_account_action, null, false);
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
            DataBaseHelper dbHeplper = new DataBaseHelper(App.currentActivity);
            try {
                if (dbHeplper.getAllUsers().size() == 1) {
                    dbHeplper.deleteUserById(App.userId);
                    logOut(dbHeplper);
                    AuthenticationDialog dialogauth = new AuthenticationDialog(false, null, null);
                    dialogauth.setCancelable(false);
                    dialogauth.show(getChildFragmentManager(), "");

                } else if (dbHeplper.getAllUsers().size() > 1) {
                    if (isActive == 1) {
                        if (!dbHeplper.getAllUsers().get(0).getUserId().equals(userId)) {
                            dbHeplper.deleteUserById(userId);
                            logOut(dbHeplper);
                            AuthenticationDialog authenticationDialogialog = new AuthenticationDialog(true, dbHeplper.getAllUsers().get(0).getUserName(), dbHeplper.getAllUsers().get(0).getPassword());
                            authenticationDialogialog.show(getChildFragmentManager(), "");
                        } else {
                            AuthenticationDialog authenticationDialogialog = new AuthenticationDialog(true, dbHeplper.getAllUsers().get(1).getUserName(), dbHeplper.getAllUsers().get(1).getPassword());
                            dbHeplper.deleteUserById(userId);
                            logOut(dbHeplper);
                            authenticationDialogialog.show(getChildFragmentManager(), "");
                        }
                    } else if (isActive == 0) {
                        boolean state = dbHeplper.deleteUserById(userId);
                        if (state) {
                            callBakck.onDelete(true);
                            dialog.dismiss();
                        } else {
                            boolean isdeleted = dbHeplper.deleteUuid(UUid);
                            if (isdeleted) {

                                callBakck.onDelete(true);
                                dialog.dismiss();
                            }
                        }
                    }

                }

            } catch (Exception e) {

            }
        });

        return dialog;
    }

    private void logOut(DataBaseHelper dbHeplper) {
        try {

            android.content.SharedPreferences shared;
            android.content.SharedPreferences.Editor editor;
            shared = Objects.requireNonNull(getActivity()).getSharedPreferences("UserPrefs", MODE_PRIVATE);
            editor = shared.edit();

            InstagramApi api = InstagramApi.getInstance();
            SQLiteDatabase db = dbHeplper.getWritableDatabase();
            api.Logout(new InstagramApi.ResponseHandler() {
                @Override
                public void OnSuccess(JSONObject response) {
                    db.execSQL("DELETE FROM posts");
                    db.execSQL("DELETE FROM followers");
                    db.execSQL("DELETE FROM followings");
                    db.execSQL("DELETE FROM first_followers");
                    db.execSQL("DELETE FROM first_followings");
                    editor.putString("username", "");
                    editor.putString("profile_pic_url", "");
                    editor.putString("full_name", "");
                    editor.putBoolean("is_first_reload", true);
                    editor.putBoolean("is_get_posts", false);
                    editor.putLong("first_reload_time", 0);
                    editor.putLong("last_reload_time", 0);
                    editor.putBoolean("auto_unfollow_is_active", false);
                    editor.apply();

                    App.followCoin = 0;
                    App.Api_Token = null;
                    App.UUID = null;
                    App.likeCoin = 0;
                    App.userId = null;
                    App.profilePicURl = null;


                }

                @Override
                public void OnFailure(int statusCode, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } catch (InstaApiException e) {
            e.printStackTrace();
        }
    }

}
