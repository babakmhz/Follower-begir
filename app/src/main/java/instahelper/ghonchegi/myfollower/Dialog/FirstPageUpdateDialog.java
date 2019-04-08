package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import com.squareup.picasso.Picasso;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogFirstPageUpdateNoticeBinding;

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
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bindingLike = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_first_page_update_notice, null, false);
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
