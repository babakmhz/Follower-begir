package instahelper.ghonchegi.myfollower.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import instahelper.ghonchegi.myfollower.Models.TopUsers;
import instahelper.ghonchegi.myfollower.R;

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
        this.context = parent.getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.cell_top_users, parent, false);
        return new TopUsersAdapter.Item(row);

    }

    @Override
    public void onBindViewHolder(@NonNull Item item, int position) {
        TopUsers topUser = topUsersList.get(position);
        item.tvRowNumber.setText(position +1+"");
        item.tvPrizeAmount.setText(topUser.getCount());
        item.tvUserName.setText(topUser.getUserName());
        if (type == 1) {
            item.imvPrizeType.setImageResource(R.drawable.follow_coin);
        } else {
            item.imvPrizeType.setImageResource(R.drawable.like_coin);

        }


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
