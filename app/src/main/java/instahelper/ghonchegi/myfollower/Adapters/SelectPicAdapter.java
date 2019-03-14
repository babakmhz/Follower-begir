package instahelper.ghonchegi.myfollower.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.Interface.RecievedImageFromAdapterInterface;
import instahelper.ghonchegi.myfollower.Models.PictureModel;
import instahelper.ghonchegi.myfollower.R;


public class SelectPicAdapter extends RecyclerView.Adapter<SelectPicAdapter.Item> {

    //region Variables
    private Context context;
    private ArrayList<PictureModel> picList;
    private RecievedImageFromAdapterInterface callback;

    public SelectPicAdapter(Context context, ArrayList<PictureModel> picList, RecievedImageFromAdapterInterface localCallBack) {
        this.context = context;
        this.picList = picList;
        this.callback = localCallBack;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
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
            @Override
            public void onClick(View v) {
                callback.isRecieved(picList.get(position).getId(),
                        picList.get(position).getUrl()
                );
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
