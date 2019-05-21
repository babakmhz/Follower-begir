package ka.follow.app2.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import ka.follow.app2.App;
import ka.follow.app2.R;
import ka.follow.app2.databinding.DialogAboutUsBinding;

@SuppressLint("ValidFragment")
public class AboutUsDialog extends DialogFragment {
    private DialogAboutUsBinding bindingLike;


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        App.isNotificationDialgShown = true;
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bindingLike = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_about_us, null, false);
        dialog.setContentView(bindingLike.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion

        return dialog;
    }


}
