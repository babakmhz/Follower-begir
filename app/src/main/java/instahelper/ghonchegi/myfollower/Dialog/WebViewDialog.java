package instahelper.ghonchegi.myfollower.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import instahelper.ghonchegi.myfollower.Interface.WebViewLoadedInterface;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogWebViewBinding;

@SuppressLint("ValidFragment")
public class WebViewDialog extends DialogFragment {


    final String urlAddress;
    final int transactionId;
    private final WebViewLoadedInterface callbackWebView;
    private DialogWebViewBinding binding;

    public WebViewDialog(String urlAddress, int transactionId, WebViewLoadedInterface callBackWebView) {
        this.urlAddress = urlAddress;
        this.transactionId = transactionId;
        this.callbackWebView = callBackWebView;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_web_view, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //endregion
        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("https://www.instagram.com/p/" + urlAddress);
        callbackWebView.webViewOpened();
        binding.btnReturn.setOnClickListener(v -> dismiss());


        return dialog;
    }

}
