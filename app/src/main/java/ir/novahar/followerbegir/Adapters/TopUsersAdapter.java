package ir.novahar.followerbegir.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.novahar.followerbegir.App;
import ir.novahar.followerbegir.Models.TopUsers;

import ir.novahar.followerbegir.R;

public class TopUsersAdapter extends RecyclerView.Adapter<TopUsersAdapter.Item> {

    private ArrayList<TopUsers> topUsersList;
    private Context context;
    private int type;

    public TopUsersAdapter(ArrayList<TopUsers> topUsersList, int type) {
        this.topUsersList = topUsersList;
        this.type = type;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.context = App.currentActivity;
        View row = LayoutInflater.from(context).inflate(R.layout.cell_top_users, parent, false);
        return new TopUsersAdapter.Item(row);

    }

    @Override
    public void onBindViewHolder(@NonNull Item item, int position) {
        TopUsers topUser = topUsersList.get(position);
        item.tvRowNumber.setText(position + 1 + "");
        item.tvPrizeAmount.setText(topUser.getCount());
        item.tvUserName.setText(topUser.getUserName());
        if (type == 1) {
            item.imvPrizeType.setImageResource(R.drawable.ic_team);
        } else {
            item.imvPrizeType.setImageResource(R.drawable.ic_heart_coin);

        }
        if (topUser.getPicUrl() != null)
            Picasso.get().load(topUser.getPicUrl()).into(item.profilePic);


    }

    @Override
    public int getItemCount() {
        return topUsersList.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        private CircleImageView profilePic;
        private TextView tvUserName;
        private TextView tvPrizeAmount, tvRowNumber;
        private AppCompatImageView imvPrizeType;

        public Item(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.imgProfileImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPrizeAmount = itemView.findViewById(R.id.tvCoinCount);
            imvPrizeType = itemView.findViewById(R.id.imvCoin);
            tvRowNumber = itemView.findViewById(R.id.tvRowNumber);

        }
    }
}
