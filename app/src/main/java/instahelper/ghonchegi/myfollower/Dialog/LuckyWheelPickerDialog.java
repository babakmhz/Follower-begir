package instahelper.ghonchegi.myfollower.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.WheelPiclerView.LuckyItem;
import instahelper.ghonchegi.myfollower.WheelPiclerView.LuckyWheelView;

public class LuckyWheelPickerDialog extends DialogFragment {
    List<LuckyItem> data = new ArrayList<>();

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

        setData(luckyWheelView);
//        setVariables(dialog);


        return dialog;


    }

    private void setData(LuckyWheelView luckyWheelView) {
        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "100";
        luckyItem1.icon = R.drawable.app_logo;
        luckyItem1.color = 0xffFFF3E0;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "200";
        luckyItem2.icon = R.drawable.follow_coin;
        luckyItem2.color = 0xffFFE0B2;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "300";
        luckyItem3.icon = R.drawable.follow_coin;
        luckyItem3.color = 0xffFFCC80;
        data.add(luckyItem3);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "400";
        luckyItem4.icon = R.drawable.follow_coin;
        luckyItem4.color = 0xffFFF3E0;
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "500";
        luckyItem5.icon = R.drawable.follow_coin;
        luckyItem5.color = 0xffFFE0B2;
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "600";
        luckyItem6.icon = R.drawable.follow_coin;
        luckyItem6.color = 0xffFFCC80;
        data.add(luckyItem6);
        //////////////////

        //////////////////////
        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "700";
        luckyItem7.icon = R.drawable.follow_coin;
        luckyItem7.color = 0xffFFF3E0;
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.topText = "800";
        luckyItem8.icon = R.drawable.follow_coin;
        luckyItem8.color = 0xffFFE0B2;
        data.add(luckyItem8);


        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.topText = "900";
        luckyItem9.icon = R.drawable.follow_coin;
        luckyItem9.color = 0xffFFCC80;
        data.add(luckyItem9);
        ////////////////////////

        LuckyItem luckyItem10 = new LuckyItem();
        luckyItem10.topText = "1000";
        luckyItem10.icon = R.drawable.follow_coin;
        luckyItem10.color = 0xffFFE0B2;
        data.add(luckyItem10);

        LuckyItem luckyItem11 = new LuckyItem();
        luckyItem11.topText = "2000";
        luckyItem11.icon = R.drawable.follow_coin;
        luckyItem11.color = 0xffFFE0B2;
        data.add(luckyItem11);

        LuckyItem luckyItem12 = new LuckyItem();
        luckyItem12.topText = "3000";
        luckyItem12.icon = R.drawable.follow_coin;
        luckyItem12.color = 0xffFFE0B2;
        data.add(luckyItem12);
        luckyWheelView.setData(data);
        luckyWheelView.setRound(5);
    }

//    private void setVariables(Dialog dialog) {
//
//        List<WheelItem> wheelItems = new ArrayList<>();
//        wheelItems.add(new WheelItem(Color.LTGRAY, BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_action_name)));
//        wheelItems.add(new WheelItem(Color.BLUE, BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_action_name)));
//        wheelItems.add(new WheelItem(Color.BLACK, BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_action_name)));
//        wheelItems.add(new WheelItem(Color.GRAY, BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_action_name)));
//        wheelItems.add(new WheelItem(Color.RED, BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_action_name)));
//        wheelItems.add(new WheelItem(Color.GREEN, BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_action_name)));
//
//
//        final LuckyWheel lw = (LuckyWheel) dialog.findViewById(R.id.lwv);
//        lw.addWheelItems(wheelItems);
//
//        lw.rotateWheelTo(5);
//
//        lw.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
//            @Override
//            public void onReachTarget() {
//                Toast.makeText(getActivity(), "Target Reached", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        Button start = dialog.findViewById(R.id.start);
//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                lw.rotateWheelTo(6);
//            }
//        });
//
//
//    }
}
