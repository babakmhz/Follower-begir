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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.Adapters.OrdersAdapter;
import instahelper.ghonchegi.myfollower.Adapters.SelectPicAdapter;
import instahelper.ghonchegi.myfollower.Models.Orders;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.DialogReviewOrdersBinding;

public class ReviewOrdersDialog extends DialogFragment {

    private DialogReviewOrdersBinding binding;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        //region Dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimationFromDownToDown;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.dialog_review_orders,null,false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //endregion

        ArrayList<Orders> orders = new ArrayList<>();
        orders.add(new Orders(1, "23223", "sddsds", "10", "121121", 100, " کامنت "));
        orders.add(new Orders(1, "23223", "sddsds", "10", "121121", 100, " کامنت "));
        orders.add(new Orders(1, "23223", "sddsds", "10", "121121", 100, " کامنت "));
        orders.add(new Orders(1, "23223", "sddsds", "10", "121121", 100, " کامنت "));
        orders.add(new Orders(1, "23223", "sddsds", "10", "121121", 100, " کامنت "));
        orders.add(new Orders(1, "23223", "sddsds", "10", "121121", 100, " کامنت "));


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
                for (int i = 0; i <    binding.rcvOrders.getChildCount(); i++) {
                    View v =    binding.rcvOrders.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });

        return dialog;
    }


}
