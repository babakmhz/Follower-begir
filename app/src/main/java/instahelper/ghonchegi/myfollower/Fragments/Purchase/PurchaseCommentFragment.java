package instahelper.ghonchegi.myfollower.Fragments.Purchase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

import instahelper.ghonchegi.myfollower.Adapters.SelectPicAdapter;
import instahelper.ghonchegi.myfollower.Models.PictureModel;
import instahelper.ghonchegi.myfollower.R;


public class PurchaseCommentFragment extends Fragment {
    private View view;
    private RecyclerView rcvPics;

    public PurchaseCommentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase_comment, container, false);



        return view;

    }
/*
    private void init() {
        ArrayList<PictureModel> pictureModelArrayList = new ArrayList<>();
        pictureModelArrayList.add(new PictureModel("0", "2500", "https://img.p30download.com/tutorial/image/2019/01/1547735632_365-days-of-creativity-tutorial-series.jpg"));
        pictureModelArrayList.add(new PictureModel("0", "2500", "https://img.p30download.com/tutorial/image/2019/01/1547735632_365-days-of-creativity-tutorial-series.jpg"));
        pictureModelArrayList.add(new PictureModel("0", "2500", "https://img.p30download.com/tutorial/image/2019/01/1547735632_365-days-of-creativity-tutorial-series.jpg"));
        pictureModelArrayList.add(new PictureModel("0", "2500", "https://img.p30download.com/tutorial/image/2019/01/1547735632_365-days-of-creativity-tutorial-series.jpg"));
        pictureModelArrayList.add(new PictureModel("0", "2500", "https://img.p30download.com/tutorial/image/2019/01/1547735632_365-days-of-creativity-tutorial-series.jpg"));
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        SelectPicAdapter adapter = new SelectPicAdapter(getContext(), pictureModelArrayList);

        rcvPics.setLayoutManager(layoutManager);
        rcvPics.setItemAnimator(new DefaultItemAnimator());
        rcvPics.setAdapter(adapter);
        rcvPics.addItemDecoration(decoration);
        rcvPics.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rcvPics.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < rcvPics.getChildCount(); i++) {
                    View v = rcvPics.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });

    }

    private void setVariables(View dialog) {
        rcvPics = dialog.findViewById(R.id.rcvSelectPic);
    }*/


}
