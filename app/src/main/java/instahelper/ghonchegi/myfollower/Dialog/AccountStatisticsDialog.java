package instahelper.ghonchegi.myfollower.Dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Models.PictureModel;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramMedia;
import instahelper.ghonchegi.myfollower.databinding.DialogAccountStatisticsBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;
import instahelper.ghonchegi.myfollower.parser.MediasParser;

import static instahelper.ghonchegi.myfollower.App.TAG;

public class AccountStatisticsDialog extends DialogFragment {

    boolean is_more_available = false;
    private DialogAccountStatisticsBinding binding;
    private int maxLike = 0;
    private int maxComment = 0;
    private String maxLikePostId;
    private String maxCommentPostId;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_account_statistics, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //endregion


        return dialog;
    }



}
