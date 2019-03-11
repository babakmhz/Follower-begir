package instahelper.ghonchegi.myfollower.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.Models.Orders;
import instahelper.ghonchegi.myfollower.R;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.Item> {
    private Context context;
    private ArrayList<Orders> orders;

    public OrdersAdapter(Context context, ArrayList<Orders> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_orders, parent, false);
        return new OrdersAdapter.Item(row);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        String type = "";
        switch (orders.get(position).getType()) {
            case 0:
                type = "لایک";
                break;
            case 1:
                type = "فالو";

                break;
            case 2:
                type = "کامنت";
                break;
        }
        holder.tvTitle.setText("درخواست " + orders.get(position).getOrdered() + type);
        holder.tvTrackingCode.setText(orders.get(position).getTrackingCode() + "");
        holder.tvDoneReport.setText("از تعداد  " + orders.get(position).getOrdered() + type + " درخواستی تعداد  " +orders.get(position).getNumberOfReceived()+ "باقیمانده");
        holder.tvDateTime.setText(orders.get(position).getDateTime());


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public class Item extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvTrackingCode, tvDateTime, tvDoneReport;

        public Item(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvReportTitle);
            tvTrackingCode = itemView.findViewById(R.id.tvTrackingCode);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDoneReport = itemView.findViewById(R.id.tvDoneReport);
        }
    }
}
