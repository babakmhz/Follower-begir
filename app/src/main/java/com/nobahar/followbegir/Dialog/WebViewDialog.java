package com.nobahar.followbegir.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.nobahar.followbegir.App;
import com.nobahar.followbegir.Interface.WebViewLoadedInterface;
import com.nobahar.followbegir.R;
import com.nobahar.followbegir.databinding.DialogWebViewBinding;

import static com.crashlytics.android.beta.Beta.TAG;

@SuppressLint("ValidFragment")
public class WebViewDialog extends DialogFragment {


    final String urlAddress;
    final int transactionId;
    private final WebViewLoadedInterface callbackWebView;
    private DialogWebViewBinding binding;
    private CountDownTimer countDown=null;

    public WebViewDialog(String urlAddress, int transactionId, WebViewLoadedInterface callBackWebView) {
        this.urlAddress = urlAddress;
        this.transactionId = transactionId;
        this.callbackWebView = callBackWebView;
    }

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_web_view, null, false);
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
        binding.btnReturn.setEnabled(false);
        countDown=new CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "seconds remaining:  + "+millisUntilFinished / 1000);
                binding.btnReturn.setText(millisUntilFinished/1000 +"");

            }

            public void onFinish() {
                binding.btnReturn.setText("بازگشت");
                binding.btnReturn.setEnabled(true);
            }

        }.start();

        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        countDown.cancel();
        countDown=null;
    }
}
