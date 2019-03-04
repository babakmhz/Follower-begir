package instahelper.ghonchegi.myfollower.Fragments.GetCoin;

import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import instahelper.ghonchegi.myfollower.Adapters.GetCoinViewPagerAdapter;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentGetCoinBinding;


public class GetCoinFragment extends Fragment {
    private View view;

    private FragmentGetCoinBinding binding;
    private GetCoinViewPagerAdapter adapter;

    public GetCoinFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_get_coin, container, false);
        view = binding.getRoot();


        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GetCoinViewPagerAdapter(getChildFragmentManager(), 3);

        binding.viewPagerGetCoin.setAdapter(adapter);

        init();

        setActive(0);
    }

    private void init() {
        binding.tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(0);
            }
        });
        binding.tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(1);
            }
        });
        binding.tvFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(2);
            }
        });

        binding.viewPagerGetCoin.setOffscreenPageLimit(0);

        binding.viewPagerGetCoin.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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


    private void reset() {
        binding.tvLike.setTypeface(binding.tvComment.getTypeface(), Typeface.NORMAL);
        binding.tvComment.setTypeface(binding.tvComment.getTypeface(), Typeface.NORMAL);
        binding.tvFollower.setTypeface(binding.tvComment.getTypeface(), Typeface.NORMAL);
        binding.tvLike.setBackground(null);
        binding.tvComment.setBackground(null);
        binding.tvFollower.setBackground(null);
        binding.tvLike.setTextColor(getResources().getColor(R.color.black));
        binding.tvComment.setTextColor(getResources().getColor(R.color.black));
        binding.tvFollower.setTextColor(getResources().getColor(R.color.black));


    }

    private void setActive(int index) {
        switch (index) {
            case 0:
                reset();
                setViewPager(0);
                binding.tvLike.setBackground(getResources().getDrawable(R.drawable.active_tab));
                binding.tvLike.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                reset();
                setViewPager(1);
                binding.tvComment.setBackground(getResources().getDrawable(R.drawable.active_tab));
                binding.tvComment.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                reset();
                setViewPager(2);
                binding.tvFollower.setBackground(getResources().getDrawable(R.drawable.active_tab));
                binding.tvFollower.setTextColor(getResources().getColor(R.color.white));
                break;

        }
    }

    private void setViewPager(int index) {
        binding.viewPagerGetCoin.setCurrentItem(index);

    }



}
