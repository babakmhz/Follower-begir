package instahelper.ghonchegi.myfollower.Fragments.Purchase;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import instahelper.ghonchegi.myfollower.Adapters.PurchaseViewPagerDapter;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.instaAPI.InstaApiException;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;


public class PurchaseFragment extends Fragment {
    private View view;
    private TextView tvLike, tvComment, tvFollow, tvRobotLike;
    private ViewPager viewPager;

    public PurchaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase, container, false);
        PurchaseViewPagerDapter adapter = new PurchaseViewPagerDapter(getChildFragmentManager(), 3);
        viewPager = view.findViewById(R.id.viewPagerPurchases);
        viewPager.setAdapter(adapter);

        setVariables();
        init();

        setActive(0);

        return view;

    }

    private void init() {
        tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(0);
            }
        });
        tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(1);
            }
        });
        tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(2);
            }
        });
/*        tvRobotLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(3);
            }
        });*/
        viewPager.setOffscreenPageLimit(0);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setActive(i);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void setVariables() {
        tvLike = view.findViewById(R.id.tvLike);
        tvComment = view.findViewById(R.id.tvComment);
        tvFollow = view.findViewById(R.id.tvFollower);
        tvRobotLike = view.findViewById(R.id.tvRobotLike);
    }

    private void reset() {
        tvLike.setTypeface(tvComment.getTypeface(), Typeface.NORMAL);
        tvComment.setTypeface(tvComment.getTypeface(), Typeface.NORMAL);
        tvFollow.setTypeface(tvComment.getTypeface(), Typeface.NORMAL);
        tvRobotLike.setTypeface(tvComment.getTypeface(), Typeface.NORMAL);
        tvLike.setBackground(null);
        tvRobotLike.setBackground(null);
        tvComment.setBackground(null);
        tvFollow.setBackground(null);
        tvLike.setTextColor(getResources().getColor(R.color.black));
        tvComment.setTextColor(getResources().getColor(R.color.black));
        tvFollow.setTextColor(getResources().getColor(R.color.black));
        tvRobotLike.setTextColor(getResources().getColor(R.color.black));


    }

    private void setActive(int index) {
        switch (index) {
            case 0:
                reset();
                setViewPager(0);
                tvLike.setBackground(getResources().getDrawable(R.drawable.active_tab));
                tvLike.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                reset();
                setViewPager(1);
                tvComment.setBackground(getResources().getDrawable(R.drawable.active_tab));
                tvComment.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                reset();
                setViewPager(2);
                tvFollow.setBackground(getResources().getDrawable(R.drawable.active_tab));
                tvFollow.setTextColor(getResources().getColor(R.color.white));
                break;
//            case 3:
//                reset();
//                setViewPager(3);
//                tvRobotLike.setBackground(getResources().getDrawable(R.drawable.active_tab));
//                tvRobotLike.setTextColor(getResources().getColor(R.color.white));
//                break;
        }
    }

    private void setViewPager(int index) {
        viewPager.setCurrentItem(index);

    }


}
