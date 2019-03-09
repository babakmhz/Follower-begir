package instahelper.ghonchegi.myfollower.Dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramUser;
import instahelper.ghonchegi.myfollower.databinding.DialogWheelPickerBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class LuckyWheelPickerDialog extends DialogFragment {
    private DialogWheelPickerBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_wheel_picker, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setVariables(dialog);


        return dialog;


    }

    private void setVariables(Dialog dialog) {

        List<WheelItem> wheelItems = new ArrayList<>();
        wheelItems.add(new WheelItem(Color.LTGRAY, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_name)));
        wheelItems.add(new WheelItem(Color.BLUE, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_name)));
        wheelItems.add(new WheelItem(Color.BLACK, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_name)));
        wheelItems.add(new WheelItem(Color.GRAY, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_name)));
        wheelItems.add(new WheelItem(Color.RED, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_name)));
        wheelItems.add(new WheelItem(Color.GREEN, BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_name)));


        final LuckyWheel lw = (LuckyWheel) dialog.findViewById(R.id.lwv);
        lw.addWheelItems(wheelItems);

        lw.rotateWheelTo(5);


}
