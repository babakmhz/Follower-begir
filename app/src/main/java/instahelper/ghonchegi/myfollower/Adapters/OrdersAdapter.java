package instahelper.ghonchegi.myfollower.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Dialog.NewMessageDialog;
import instahelper.ghonchegi.myfollower.Models.Orders;
import instahelper.ghonchegi.myfollower.R;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.Item> {
    private Context context;
    private ArrayList<Orders> orders;
    FragmentManager fm;

    public OrdersAdapter(Context context, ArrayList<Orders> orders, FragmentManager childFragmentManager) {
        this.context = context;
        this.orders = orders;
        this.fm=childFragmentManager;
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
                type = " لایک ";
                break;
            case 1:
                type = " فالو ";

                break;
            case 2:
                type = " کامنت ";
                break;
        }
        holder.tvTitle.setText("درخواست " + orders.get(position).getOrdered() + type);
        //TODO holder.tvTrackingCode.setText(orders.get(position).getTrackingCode() + "");
        holder.tvDoneReport.setText("از تعداد  " + orders.get(position).getOrdered() + type + " درخواستی تعداد  " + orders.get(position).getNumberOfReceived() + " انجام شده ");
        holder.tvDateTime.setText(orders.get(position).getDateTime());
        Picasso.get().load(orders.get(position).getPicUrl()).into(holder.imvOrder);
        holder.btnReOrder.setOnClickListener(v->{
            String text=  "گزارش برای سفارش  "   + "  "+App.user;
            NewMessageDialog dialog=new NewMessageDialog(text);
            dialog.show(fm,"");
        });


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public class Item extends RecyclerView.ViewHolder {
        AppCompatImageView imvOrder;
        private TextView tvTitle, tvTrackingCode, tvDateTime, tvDoneReport;
        private Button btnReOrder;

        public Item(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvReportTitle);
            tvTrackingCode = itemView.findViewById(R.id.tvTrackingCode);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDoneReport = itemView.findViewById(R.id.tvDoneReport);
            imvOrder = itemView.findViewById(R.id.imvPic);
            btnReOrder=itemView.findViewById(R.id.btnReOrder);
        }
    }
}
