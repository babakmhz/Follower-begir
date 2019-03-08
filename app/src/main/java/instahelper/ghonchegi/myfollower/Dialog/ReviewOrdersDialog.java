package instahelper.ghonchegi.myfollower.Dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import instahelper.ghonchegi.myfollower.Adapters.OrdersAdapter;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Models.Orders;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogReviewOrdersBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

public class ReviewOrdersDialog extends DialogFragment {

    private DialogReviewOrdersBinding binding;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_review_orders, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);
        //endregion
        getUserCoins();


        return dialog;
    }

    private void getUserCoins() {
        final String requestBody = JsonManager.simpleJson();
        ArrayList<Orders> orders = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "transaction/list", (String response1) -> {
            if (response1 == null) {
                binding.llFirstOrder.setVisibility(View.VISIBLE);
            }
            if (response1 != null) {
                try {
                    JSONArray array = new JSONArray(response1);
                    if (array.length() == 0)
                        binding.llFirstOrder.setVisibility(View.VISIBLE);

                    if (array.length() != 0)
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObj = array.getJSONObject(i);
                            int requestCount = jsonObj.getInt("request_count");
                            int remaining_count = jsonObj.getInt("remaining_count");
                            String dateTime = jsonObj.getString("created_at");
                            int type = jsonObj.getInt("type");
                            String image_path = jsonObj.getString("image_path");
                            orders.add(new Orders(1, 1, image_path, remaining_count, dateTime, requestCount, type));
                        }

                    setView(orders);


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

    private void setView(ArrayList<Orders> orders) {
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        OrdersAdapter adapter = new OrdersAdapter(getContext(), orders);

        binding.rcvOrders.setLayoutManager(mLayoutManager);
        binding.rcvOrders.setItemAnimator(new DefaultItemAnimator());
        binding.rcvOrders.setAdapter(adapter);
        binding.rcvOrders.addItemDecoration(decoration);
        binding.rcvOrders.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rcvOrders.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < binding.rcvOrders.getChildCount(); i++) {
                    View v = binding.rcvOrders.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });
    }


}
