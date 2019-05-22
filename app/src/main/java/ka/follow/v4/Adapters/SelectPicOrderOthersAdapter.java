package ka.follow.v4.Adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ka.follow.v4.App;
import ka.follow.v4.Dialog.SelectCountPurchaseOthersDialog;
import ka.follow.v4.Models.PictureModel;
import ka.follow.v4.R;


public class SelectPicOrderOthersAdapter extends RecyclerView.Adapter<SelectPicOrderOthersAdapter.Item> {

    //region Variables
    private Context context;
    private ArrayList<PictureModel> picList;
    private FragmentManager fm;


    public SelectPicOrderOthersAdapter(Context context, ArrayList<PictureModel> picList, FragmentManager fm) {
        this.context = context;
        this.picList = picList;
        this.fm = fm;

    }


    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = App.currentActivity;
        View row = LayoutInflater.from(context).inflate(R.layout.row_select_pic, parent, false);
        return new Item(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, final int position) {
        try {
            Picasso.get()
                    .load(picList.get(position).getUrl())
                    .centerCrop()
                    .resize(150, 150)
                    .into(holder.imvPic);
        } catch (Exception e) {
            Log.i("this", "onBindViewHolder: " + e.toString());
        }

        holder.tvLikeCounts.setText(picList.get(position).getLikeCount());
        holder.imvPic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.tvLikeCounts);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.action_purchase_others, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Integer itemType = 0;
                        switch (item.getItemId()) {
                            case R.id.like:
                                itemType = 0;
                                break;
                            case R.id.comment:
                                itemType = 2;
                                break;
                            case R.id.view:
                                itemType = 3;
                                break;

                        }
                        SelectCountPurchaseOthersDialog dialog = new SelectCountPurchaseOthersDialog(itemType,picList.get(position).getId(), picList.get(position).getUrl());
                        dialog.show(fm, "");
                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return picList.size();
    }

    public class Item extends RecyclerView.ViewHolder {

        TextView tvLikeCounts;
        ImageView imvPic;

        public Item(View itemView) {
            super(itemView);
            tvLikeCounts = itemView.findViewById(R.id.tvLikeCounts);
            imvPic = itemView.findViewById(R.id.imvSelectPic);

        }
    }


}
