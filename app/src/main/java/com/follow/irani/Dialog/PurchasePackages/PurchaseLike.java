package com.follow.irani.Dialog.PurchasePackages;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.follow.irani.Activities.MainActivity;
import com.follow.irani.App;
import com.follow.irani.Interface.DirectPurchaseDialogInterface;
import com.follow.irani.Manager.Config;
import com.follow.irani.R;
import com.follow.irani.databinding.DialogDirectCommentPurchaseBinding;
import com.follow.irani.databinding.DialogDirectFollowerPurchaseBinding;
import com.follow.irani.databinding.DialogDirectLikePurchaseBinding;
import com.follow.irani.databinding.DialogDirectPurchaseViewBinding;

@SuppressLint("ValidFragment")
public class PurchaseLike extends DialogFragment {
    private final int type;
    private final int count;
    private final String selectedPicURl;
    private final DirectPurchaseDialogInterface callBackDirectPurchase;
    private String itemId = null;
    private DialogDirectLikePurchaseBinding bindingLike;
    private DialogDirectCommentPurchaseBinding bindingComment;
    private DialogDirectFollowerPurchaseBinding bindingFollow;
    private DialogDirectPurchaseViewBinding bindingView;


    public PurchaseLike(int type, String selectedPicURL, int progress, String itemId, DirectPurchaseDialogInterface callBackDirectPurchase) {
        this.type = type;
        this.selectedPicURl = selectedPicURL;
        this.count = progress;
        this.callBackDirectPurchase = callBackDirectPurchase;
        this.itemId = itemId;
    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch (type) {
            case 0:
                bindingLike = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_direct_like_purchase, null, false);
                dialog.setContentView(bindingLike.getRoot());

                break;
            case 1:
                bindingFollow = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_direct_follower_purchase, null, false);
                dialog.setContentView(bindingFollow.getRoot());
                break;
            case 2:
                bindingComment = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_direct_comment_purchase, null, false);
                dialog.setContentView(bindingComment.getRoot());
                break;
            case 3:
                bindingView = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_direct_purchase_view, null, false);
                dialog.setContentView(bindingView.getRoot());
                break;
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion


        switch (type) {
            case 0:
                bindingLike.tvTitle.setText("با خرید لایک مستقیم به محض انجام عملیات خرید سفارش شما آغاز می گردد.");
                initSubmitLike();
                break;
            case 1:
                bindingFollow.tvTitle.setText("با خرید فالوئر مستقیم به محض انجام عملیات خرید سفارش شما آغاز می گردد.");
                initSubmitFollower();
                break;
            case 2:
                bindingComment.tvTitle.setText("با خرید کامنت مستقیم به محض انجام عملیات خرید سفارش شما آغاز می گردد.");
                initCommentSubmit();
                break;
            case 3 :
                bindingView.tvTitle.setText("با خرید ویو مستقیم به محض انجام عملیات خرید سفارش شما آغاز می گردد.");
                initViewSumbite();
                break;
        }
        return dialog;
    }
    private void initViewSumbite() {
        bindingView.llLikeOne.setOnClickListener(v -> {
            if (validate()) {

                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFirstVIew);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingView.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuSecondView);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingView.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuThirdView);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
    }
    private void initCommentSubmit() {
        bindingComment.llLikeOne.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFirstComment);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingComment.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuSecondComment);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingComment.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuThirdComment);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
    }

    private void initSubmitFollower() {
        bindingFollow.llLikeOne.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFirstFollow);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingFollow.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuSecondFollow);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingFollow.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuThirdFollow);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingFollow.llLikeFour.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFourthFollow);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingFollow.llLikeFive.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFifthFollow);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
    }

    private void initSubmitLike() {
        bindingLike.llLikeOne.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFirstLike);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingLike.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuSecondLike);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingLike.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuThirdLike);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingLike.llLikeFour.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFourthLike);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
        bindingLike.llLikeFive.setOnClickListener(v -> {
            if (validate()) {
                MainActivity.mNivadBilling.purchase(App.currentActivity, Config.skuFifthLike);
                MainActivity.directOrderItemId = itemId;
                MainActivity.directOrderItemURL = selectedPicURl;
            }

        });
    }


    private boolean validate() {
        if (selectedPicURl == null) {
            Toast.makeText(App.currentActivity, "یک عکس انتخاب کنید", Toast.LENGTH_SHORT).show();
            return false;

        } else return true;
    }


}