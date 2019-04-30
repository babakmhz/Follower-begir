package ka.follow.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ka.follow.app.Interface.ShopItemInterface;
import ka.follow.app.Manager.Config;
import ka.follow.app.Models.Offers;
import ka.follow.app.R;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.Item> {
    private final ShopItemInterface callBackShopItem;
    private Context context;
    private ArrayList<Offers> orders;

    public OffersAdapter(Context context, ArrayList<Offers> orders, ShopItemInterface callBackShopItem) {
        this.context = context;
        this.orders = orders;
        this.callBackShopItem = callBackShopItem;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_offers, parent, false);
        return new OffersAdapter.Item(row);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        colorChecker(holder.root, position);
        Offers offers = orders.get(position);
        if (offers.getType() == 1) {
            holder.imvCoin.setImageResource(R.drawable.ic_follower_coin);
        } else {
            holder.imvCoin.setImageResource(R.drawable.ic_heart_coin);
        }
        holder.tvDiscount.setText(offers.getPrice_discount() + " سکه");
        holder.tvDiscount.setPaintFlags(holder.tvDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvTotal.setText(offers.getCount() + " سکه");
        holder.tvPrice.setText(offers.getPrice() + "تومان");
        holder.root.setOnClickListener(v -> {
            callBackShopItem.shopItemBuy(offers.getRsa(), offers.getType(), offers.getCount(), Config.requestShopItems);
        });


    }

    private void colorChecker(ConstraintLayout root, int position) {
        switch (position % 4) {
            case 0:
                root.setBackground(context.getResources().getDrawable(R.drawable.shop_bg_1));
                break;
            case 1:
                root.setBackground(context.getResources().getDrawable(R.drawable.shop_bg_2));

                break;
            case 2:
                root.setBackground(context.getResources().getDrawable(R.drawable.shop_bg_3));

                break;
            case 3:
                root.setBackground(context.getResources().getDrawable(R.drawable.shop_bg_4));

                break;
        }
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }


    public class Item extends RecyclerView.ViewHolder {
        AppCompatImageView imvCoin;
        ConstraintLayout root, mainContainer;
        private TextView tvDiscount, tvTotal, tvPrice;

        public Item(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTotal = itemView.findViewById(R.id.tvAmount);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            imvCoin = itemView.findViewById(R.id.imvCoins);
            root = itemView.findViewById(R.id.constraintLayout5);
            mainContainer = itemView.findViewById(R.id.mainContainer);
        }
    }
}