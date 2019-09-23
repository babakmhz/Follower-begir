package com.follow.nobahar.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import com.follow.nobahar.App;
import com.follow.nobahar.R;
import com.follow.nobahar.databinding.DialogFirstPageOffersBinding;

@SuppressLint("ValidFragment")
public class FirstPageNotificationDialog extends DialogFragment {
    private DialogFirstPageOffersBinding bindingLike;
    private String text;
    public FirstPageNotificationDialog(String text) {
        this.text=text;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        App.isNotificationDialgShown=true;
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bindingLike = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_first_page_offers, null, false);
        dialog.setContentView(bindingLike.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion
        bindingLike.tvNotice.setText(text);

        return dialog;
    }


}
