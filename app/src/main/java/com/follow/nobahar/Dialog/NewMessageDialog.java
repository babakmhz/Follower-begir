package com.follow.nobahar.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.follow.nobahar.Interface.NewMessageSubmittedInterface;
import com.follow.nobahar.Manager.JsonManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.follow.nobahar.App;

import ir.novahar.followerbegir.R;
import ir.novahar.followerbegir.databinding.DialogNewMessageBinding;

@SuppressLint("ValidFragment")
public class NewMessageDialog extends DialogFragment {

    NewMessageSubmittedInterface callback;
    private DialogNewMessageBinding binding;
    private String complaintText;

    public NewMessageDialog(NewMessageSubmittedInterface callback) {
        this.callback = callback;
    }

    public NewMessageDialog(String text) {
        this.complaintText = text;

    }


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(App.currentActivity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_new_message, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion

        binding.brnDiscard.setOnClickListener(v -> {
            binding.edtText.setText("");
            binding.edtTitle.setText("");
        });

        if (complaintText != null) {
            binding.edtTitle.setText(complaintText);
            binding.edtTitle.setEnabled(false);
        }

        binding.btnSend.setOnClickListener(v -> {
            send();
        });

        binding.brnDiscard.setOnClickListener(v -> {
            dismiss();
        });


        return dialog;
    }

    private void send() {

        final String requestBody = JsonManager.sendTicket(binding.edtTitle.getText().toString(), binding.edtText.getText().toString());

        StringRequest request = new StringRequest(Request.Method.POST, App.Base_URL + "message/ticket/set", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optBoolean("status")) {
                        Toast.makeText(App.currentActivity, "پیام شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                        if (callback != null)
                            callback.sumbited(true);
                        dismiss();


                    } else
                        Toast.makeText(App.currentActivity, "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    Toast.makeText(App.currentActivity, "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        App.requestQueue.add(request);
    }


}
