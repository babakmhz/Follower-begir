package ka.follow.app.Dialog.PurchasePackages;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import ka.follow.app.Interface.DirectPurchaseDialogInterface;
import ka.follow.app.Manager.Config;
import ka.follow.app.R;
import ka.follow.app.databinding.DialogDirectCommentPurchaseBinding;
import ka.follow.app.databinding.DialogDirectFollowerPurchaseBinding;
import ka.follow.app.databinding.DialogDirectLikePurchaseBinding;
import ka.follow.app.databinding.DialogDirectPurchaseViewBinding;

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
            case 3:
                bindingView = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_direct_purchase_view, null, false);
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
                callBackDirectPurchase.directPurchase(Config.skuFirstVIew, Config.reqeustViewtFirst, selectedPicURl, itemId, count);
            }

        });
        bindingView.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuSecondView, Config.reqeustViewSecond, selectedPicURl, itemId, count);
            }

        });
        bindingView.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuThirdView, Config.reqeustViewThird, selectedPicURl, itemId, count);
            }

        });
    }
    private void initCommentSubmit() {
        bindingComment.llLikeOne.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuFirstComment, Config.reqeustCommentFirst, selectedPicURl, itemId, count);
            }

        });
        bindingComment.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuSecondComment, Config.reqeustCommentSecond, selectedPicURl, itemId, count);
            }

        });
        bindingComment.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuThirdComment, Config.reqeustCommentThird, selectedPicURl, itemId, count);
            }

        });
    }

    private void initSubmitFollower() {
        bindingFollow.llLikeOne.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuFirstFollow, Config.reqeustFollowFirst, selectedPicURl, itemId, count);
            }

        });
        bindingFollow.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuSecondFollow, Config.reqeustFollowSecond, selectedPicURl, itemId, count);
            }

        });
        bindingFollow.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuThirdFollow, Config.reqeustFollowThird, selectedPicURl, itemId, count);
            }

        });
        bindingFollow.llLikeFour.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuFourthFollow, Config.reqeustFollowFourth, selectedPicURl, itemId, count);
            }

        });
        bindingFollow.llLikeFive.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuFifthFollow, Config.reqeustFollowFifth, selectedPicURl, itemId, count);
            }

        });
    }

    private void initSubmitLike() {
        bindingLike.llLikeOne.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuFirstLike, Config.reqeustLikeFirst, selectedPicURl, itemId, count);
            }

        });
        bindingLike.llLikeTwo.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuSecondLike, Config.reqeustLikeSecond, selectedPicURl, itemId, count);
            }

        });
        bindingLike.llLikeThree.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuThirdLike, Config.reqeustLikeThird, selectedPicURl, itemId, count);
            }

        });
        bindingLike.llLikeFour.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuFourthLike, Config.reqeustLikeFourth, selectedPicURl, itemId, count);
            }

        });
        bindingLike.llLikeFive.setOnClickListener(v -> {
            if (validate()) {
                callBackDirectPurchase.directPurchase(Config.skuFifthLike, Config.reqeustLikeFifth, selectedPicURl, itemId, count);
            }

        });
    }

    private boolean validate() {
        if (selectedPicURl == null) {
            Toast.makeText(getContext(), "یک عکس انتخاب کنید", Toast.LENGTH_SHORT).show();
            return false;

        } else return true;
    }


}
