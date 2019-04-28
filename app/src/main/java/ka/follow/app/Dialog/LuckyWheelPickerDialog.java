package ka.follow.app.Dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ka.follow.app.Manager.BroadcastManager;
import ka.follow.app.Manager.JsonManager;
import ka.follow.app.R;
import ka.follow.app.WheelPiclerView.LuckyItem;
import ka.follow.app.WheelPiclerView.LuckyWheelView;

import static ka.follow.app.App.Base_URL;
import static ka.follow.app.App.requestQueue;

public class LuckyWheelPickerDialog extends DialogFragment {
    List<LuckyItem> data = new ArrayList<>();
    private Button btnStart;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wheel_picker);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final LuckyWheelView luckyWheelView = (LuckyWheelView) dialog.findViewById(R.id.luckyWheel);
        btnStart = dialog.findViewById(R.id.start);
        btnStart.setEnabled(false);

        checkStatus();
        setData(luckyWheelView);

        btnStart.setOnClickListener(view -> {
            int index = getRandomIndex();
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
        });


        luckyWheelView.setLuckyRoundItemSelectedListener(index -> {
            String type = "";
            switch (data.get(index).type) {
                case 0:
                    type = "لایک";
                    break;
                case 1:
                    type = "فالو";
                    break;
            }
            if (data.get(index).topText.equals("0")) {
                Toast.makeText(getContext(), "شانس با شما یار نبود !‌ ایشالا فردا ...", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "شما برنده " + data.get(index).topText + " سکه " + type + " شدید ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.journaldev.broadcastreceiver.Update");
                getActivity().sendBroadcast(intent);
                BroadcastManager.sendBroadcast(getContext());

            }
            setStatus(data.get(index));
        });
        return dialog;


    }

    private void setData(LuckyWheelView luckyWheelView) {
        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "4";
        luckyItem1.icon = R.drawable.like_coin;
        luckyItem1.color = 0xffFFF3E0;
        luckyItem1.type = 0;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "9";
        luckyItem2.icon = R.drawable.like_coin;
        luckyItem2.color = 0xffFFE0B2;
        luckyItem1.type = 0;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "5";
        luckyItem3.icon = R.drawable.follow_coin;
        luckyItem3.color = 0xffFFCC80;
        luckyItem1.type = 1;
        data.add(luckyItem3);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "15";
        luckyItem4.icon = R.drawable.like_coin;
        luckyItem1.type = 0;
        luckyItem4.color = 0xffFFF3E0;
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "0";
        luckyItem5.icon = R.drawable.follow_coin;
        luckyItem1.type = 1;
        luckyItem5.color = 0xffFFE0B2;
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "6";
        luckyItem6.icon = R.drawable.like_coin;
        luckyItem1.type = 0;
        luckyItem6.color = 0xffFFCC80;
        data.add(luckyItem6);
        //////////////////

        //////////////////////
        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "11";
        luckyItem1.type = 1;
        luckyItem7.icon = R.drawable.follow_coin;
        luckyItem7.color = 0xffFFF3E0;
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.topText = "5";
        luckyItem1.type = 0;
        luckyItem8.icon = R.drawable.like_coin;
        luckyItem8.color = 0xffFFE0B2;
        data.add(luckyItem8);


        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.topText = "8";
        luckyItem1.type = 1;
        luckyItem9.icon = R.drawable.follow_coin;
        luckyItem9.color = 0xffFFCC80;
        data.add(luckyItem9);
        ////////////////////////

        LuckyItem luckyItem10 = new LuckyItem();
        luckyItem10.topText = "0";
        luckyItem1.type = 0;
        luckyItem10.icon = R.drawable.like_coin;
        luckyItem10.color = 0xffFFE0B2;
        data.add(luckyItem10);

        LuckyItem luckyItem11 = new LuckyItem();
        luckyItem11.topText = "13";
        luckyItem1.type = 0;
        luckyItem11.icon = R.drawable.like_coin;
        luckyItem11.color = 0xffFFE0B2;
        data.add(luckyItem11);

        LuckyItem luckyItem12 = new LuckyItem();
        luckyItem12.topText = "4";
        luckyItem1.type = 1;
        luckyItem12.icon = R.drawable.follow_coin;
        luckyItem12.color = 0xffFFE0B2;
        data.add(luckyItem12);
        luckyWheelView.setData(data);
        luckyWheelView.setRound(5);

    }


    private void checkStatus() {
        final String requestBody = JsonManager.simpleJson();

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "lucky_arrow/get", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (!jsonRootObject.optBoolean("status")) {
                        Toast.makeText(getContext(), "استفاده از این سرویس در هر روز فقط یک بار امکان پذیر می باشد", Toast.LENGTH_LONG).show();
                        dismiss();
                    } else btnStart.setEnabled(true);

                } catch (JSONException e) {
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


    private void setStatus(LuckyItem luckyItem) {
        final String requestBody = JsonManager.setLuckyWheel(luckyItem);

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "lucky_arrow/set", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (!jsonRootObject.optBoolean("status")) {
                        Toast.makeText(getContext(), "خطا در ثبت اطلاعات", Toast.LENGTH_LONG).show();

                    } else
                        dismiss();

                } catch (JSONException e) {
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

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 1) + 0;
    }

}
