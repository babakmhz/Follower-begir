package instahelper.ghonchegi.myfollower.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import instahelper.ghonchegi.myfollower.Models.Messages;
import instahelper.ghonchegi.myfollower.R;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.Item> {

    private Context context;
    private ArrayList<Messages> messageList;

    public TicketsAdapter(Context context, ArrayList<Messages> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_tickets, parent, false);
        return new TicketsAdapter.Item(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Messages messages = messageList.get(position);

        holder.tvTitle.setText(messages.getTitle());
        if (messages.getStatus() == 0) {
            holder.tvStatus.setText("پاسخ داده نشده");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
        }
        else if  (messages.getStatus() == 1) {
            holder.tvStatus.setText("پاسخ داده شده");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        }
        holder.tvCreated.setText(messages.getCreated_at());
        holder.tvRowNmber.setText(position+1 + "");

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvStatus;
        private TextView tvCreated;
        private TextView tvRowNmber;

        public Item(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreated = itemView.findViewById(R.id.tvDateTime);
            tvRowNmber = itemView.findViewById(R.id.tvRowNmber);
        }
    }
}
