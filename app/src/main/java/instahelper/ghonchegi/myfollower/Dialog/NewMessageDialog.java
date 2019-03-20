package instahelper.ghonchegi.myfollower.Dialog;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogNewMessageBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

@SuppressLint("ValidFragment")
public class NewMessageDialog extends DialogFragment {

    private DialogNewMessageBinding binding;


    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_new_message, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //endregion

        binding.brnDiscard.setOnClickListener(v -> {
            binding.edtText.setText("");
            binding.edtTitle.setText("");
        });

        binding.btnSend.setOnClickListener(v -> {
            send();
        });


        return dialog;
    }

    private void send() {

        final String requestBody = JsonManager.sendTicket(binding.edtTitle.getText().toString(), binding.edtText.getText().toString());

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "message/ticket/set", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (jsonRootObject.optBoolean("status")) {
                        Toast.makeText(getContext(), "پیام شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                        dismiss();


                    } else
                        Toast.makeText(getContext(), "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    Toast.makeText(getContext(), "خطا در ارسال پیام", Toast.LENGTH_SHORT).show();
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
        requestQueue.add(request);
    }


}