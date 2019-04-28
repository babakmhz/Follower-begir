package ka.follow.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ka.follow.app.Models.Messages;
import ka.follow.app.R;

public class TicketsAnswerAdapter extends RecyclerView.Adapter<TicketsAnswerAdapter.Item> {

    private Context context;
    private ArrayList<Messages> messageList;

    public TicketsAnswerAdapter(Context context, ArrayList<Messages> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_ticket_answer, parent, false);
        return new TicketsAnswerAdapter.Item(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Messages messages = messageList.get(position);

        if (messages.getUser_name() == 1) {
            holder.tvAdminMessage.setText(messages.getDescription());
            holder.tvAdminMessage.setVisibility(View.VISIBLE);
        } else {
            holder.tvUserMessage.setText(messages.getDescription());
            holder.tvUserMessage.setVisibility(View.VISIBLE);


        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        private TextView tvAdminMessage;
        private TextView tvUserMessage;

        public Item(@NonNull View itemView) {
            super(itemView);
            tvAdminMessage = itemView.findViewById(R.id.tvAdminMessage);
            tvUserMessage = itemView.findViewById(R.id.tvAdminMessage);
        }
    }
}
