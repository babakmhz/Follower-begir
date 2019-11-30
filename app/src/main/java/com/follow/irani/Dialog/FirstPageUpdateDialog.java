package com.follow.irani.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.follow.irani.App;
import com.follow.irani.R;
import com.follow.irani.databinding.DialogFirstPageUpdateNoticeBinding;
import com.squareup.picasso.Picasso;

@SuppressLint("ValidFragment")
public class FirstPageUpdateDialog extends DialogFragment {
    private final String title;
    private final String url;
    private final String image;
    private DialogFirstPageUpdateNoticeBinding bindingLike;

    public FirstPageUpdateDialog(String title, String url, String image) {
        this.title = title;
        this.url = url;
        this.image = image;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        App.isNotificationDialgShown = true;
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bindingLike = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_first_page_update_notice, null, false);
        dialog.setContentView(bindingLike.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion

        Picasso.get().load(image).fit().centerCrop().into(bindingLike.imvLogo);
        bindingLike.tvTitle.setText(title);

        bindingLike.btnDismiss.setOnClickListener(v -> dismiss());

        bindingLike.btnOkay.setOnClickListener(v -> {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(myIntent);
            } catch (ActivityNotFoundException e) {

                e.printStackTrace();
            }
        });

        return dialog;
    }


}
