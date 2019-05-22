package ka.follow.v4.Dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ka.follow.v4.App;
import ka.follow.v4.Manager.BroadcastManager;
import ka.follow.v4.R;
import ka.follow.v4.Retrofit.ApiClient;
import ka.follow.v4.Retrofit.ApiInterface;
import ka.follow.v4.Retrofit.SimpleResult;
import ka.follow.v4.WheelPiclerView.LuckyItem;
import ka.follow.v4.WheelPiclerView.LuckyWheelView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ka.follow.v4.Retrofit.ApiClient.retrofit;

public class LuckyWheelPickerDialog extends DialogFragment {
    List<LuckyItem> data = new ArrayList<>();
    private Button btnStart;
    private ApiInterface apiInterface;



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

        ApiClient.getClient();

        apiInterface = retrofit.create(ApiInterface.class);
        checkStatus(apiInterface);
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
                Toast.makeText(App.currentActivity, "شانس با شما یار نبود !‌ ایشالا فردا ...", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(App.currentActivity, "شما برنده " + data.get(index).topText + " سکه " + type + " شدید ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.journaldev.broadcastreceiver.Update");
                getActivity().sendBroadcast(intent);
                BroadcastManager.sendBroadcast(App.currentActivity);

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


    private void checkStatus(ApiInterface apiInterface) {
        apiInterface.GetLuckyArrow(App.UUID, App.Api_Token).enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(Call<SimpleResult> call, Response<SimpleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getStatus()) {
                            Toast.makeText(App.currentActivity, "استفاده از این سرویس در هر روز فقط یک بار امکان پذیر می باشد", Toast.LENGTH_LONG).show();
                            dismiss();
                        } else btnStart.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {

            }
        });

    }


    private void setStatus(LuckyItem luckyItem) {
        apiInterface.SetLuckyArrow(App.UUID, App.Api_Token, luckyItem.type, luckyItem.topText).enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(Call<SimpleResult> call, Response<SimpleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            dismiss();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {

            }
        });

    }

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 1) + 0;
    }

}
