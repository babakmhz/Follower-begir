package ka.follow.app2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import ka.follow.app2.App;
import ka.follow.app2.Dialog.TicketAnswerDialog;
import ka.follow.app2.Models.Messages;
import ka.follow.app2.R;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.Item> {

    private Context context;
    private ArrayList<Messages> messageList;
    FragmentManager fm;

    public TicketsAdapter(Context context, ArrayList<Messages> messageList, FragmentManager childFragmentManager) {
        this.context = context;
        this.messageList = messageList;
        this.fm = childFragmentManager;

    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = App.currentActivity;
        View row = LayoutInflater.from(context).inflate(R.layout.cell_tickets, parent, false);
        return new TicketsAdapter.Item(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Messages messages = messageList.get(position);

        holder.tvTitle.setText(messages.getTitle());
        if (messages.getStatus() == 0) {
            holder.tvStatus.setText("پاسخ داده نشده");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.black));
        }
        else if  (messages.getStatus() == 1) {
            holder.tvStatus.setText("پاسخ داده شده");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.tvCreated.setText(messages.getCreated_at());
        holder.tvRowNmber.setText(position+1 + "");
        holder.root.setOnClickListener(v -> {
            TicketAnswerDialog dialog = new TicketAnswerDialog(messages.getId());
            dialog.show(fm, "");

        });

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
        private ConstraintLayout root;

        public Item(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreated = itemView.findViewById(R.id.tvDateTime);
            tvRowNmber = itemView.findViewById(R.id.tvRowNmber);
            root = itemView.findViewById(R.id.rootMessages);
        }
    }
}
