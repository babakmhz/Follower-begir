package instahelper.ghonchegi.myfollower.Dialog.PurchasePackages;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogDirectCommentPurchaseBinding;
import instahelper.ghonchegi.myfollower.databinding.DialogDirectFollowerPurchaseBinding;
import instahelper.ghonchegi.myfollower.databinding.DialogDirectLikePurchaseBinding;

@SuppressLint("ValidFragment")
public class PurchaseLike extends DialogFragment {
    private final int type;
    private DialogDirectLikePurchaseBinding bindingLike;
    private DialogDirectCommentPurchaseBinding bindingComment;
    private DialogDirectFollowerPurchaseBinding bindingFollow;

    public PurchaseLike(int type) {
        this.type = type;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch (type) {
            case 0:
                bindingLike = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_direct_like_purchase, null, false);
                dialog.setContentView(bindingLike.getRoot());

                break;
            case 1:
                bindingFollow = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_direct_follower_purchase, null, false);
                dialog.setContentView(bindingFollow.getRoot());
                break;
            case 2:
                bindingComment = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_direct_comment_purchase, null, false);
                dialog.setContentView(bindingComment.getRoot());
                break;
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion


        switch (type) {
            case 0:
                bindingLike.tvTitle.setText("با خرید لایک مستقیم به محض انجام عملیات خرید سفارش شما آغاز می گردد.");
                break;
            case 1:
                bindingFollow.tvTitle.setText("با خرید فالویر مستقیم به محض انجام عملیات خرید سفارش شما آغاز می گردد.");
                break;
            case 2:
                bindingComment.tvTitle.setText("با خرید کامنت مستقیم به محض انجام عملیات خرید سفارش شما آغاز می گردد.");
                break;
        }
        return dialog;
    }


}
