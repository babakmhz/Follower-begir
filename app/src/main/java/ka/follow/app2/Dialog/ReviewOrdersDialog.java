package ka.follow.app2.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ka.follow.app2.Adapters.OrdersAdapter;
import ka.follow.app2.App;
import ka.follow.app2.Models.Orders;
import ka.follow.app2.R;
import ka.follow.app2.Retrofit.ApiClient;
import ka.follow.app2.Retrofit.ApiInterface;
import ka.follow.app2.Retrofit.Order;
import ka.follow.app2.databinding.DialogReviewOrdersBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ka.follow.app2.Retrofit.ApiClient.retrofit;

public class ReviewOrdersDialog extends DialogFragment {

    private DialogReviewOrdersBinding binding;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(App.currentActivity), R.layout.dialog_review_orders, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Picasso.get().load(App.profilePicURl).into(binding.imgProfileImage);
        binding.tvReturn.setOnClickListener(v -> dialog.dismiss());
        binding.tvUserName.setText(App.user.getUserName());
        ApiClient.getClient();

        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        //endregion
        getUserCoins(apiInterface);

        return dialog;
    }

    private void getUserCoins(ApiInterface apiInterface) {
        apiInterface.OrderList(App.UUID, App.Api_Token).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (!response.isSuccessful()) {
                    binding.llFirstOrder.setVisibility(View.VISIBLE);

                } else {
                    if (response.body() != null) {
                        ArrayList<Orders> orders = new ArrayList<>();
                        for (Order order : response.body()) {
                            orders.add(new Orders(1, Integer.valueOf(order.getTrackingCode()), order.getImagePath(),
                                    order.getRemainingCount(), order.getCreatedAt(), order.getRequestCount(), order.getType()));

                        }
                        setView(orders);
                    }


                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

            }
        });


    }

    private void setView(ArrayList<Orders> orders) {

        try {

            DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
            OrdersAdapter adapter = new OrdersAdapter(App.currentActivity, orders, getChildFragmentManager());

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
        } catch (Exception e) {

        }


    }
}
