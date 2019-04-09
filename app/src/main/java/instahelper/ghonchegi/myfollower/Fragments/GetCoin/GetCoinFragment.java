package instahelper.ghonchegi.myfollower.Fragments.GetCoin;

import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import instahelper.ghonchegi.myfollower.Adapters.GetCoinViewPagerAdapter;
import instahelper.ghonchegi.myfollower.Interface.AddCoinMultipleAccount;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentGetCoinBinding;


@SuppressLint("ValidFragment")
public class GetCoinFragment extends Fragment {
    private View view;
    private AddCoinMultipleAccount addCoinMultipleAccount;
    private FragmentGetCoinBinding binding;
    private GetCoinViewPagerAdapter adapter;



    public GetCoinFragment(AddCoinMultipleAccount addCoinMultipleAccount) {
        this.addCoinMultipleAccount=addCoinMultipleAccount;
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
        adapter = new GetCoinViewPagerAdapter(getChildFragmentManager(), 4,addCoinMultipleAccount);

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
        binding.tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(3);
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
        binding.tvView.setTypeface(binding.tvComment.getTypeface(), Typeface.NORMAL);

        binding.tvLike.setBackground(null);
        binding.tvComment.setBackground(null);
        binding.tvFollower.setBackground(null);
        binding.tvView.setBackground(null);



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
            case 3:
                reset();
                setViewPager(3);
                binding.tvView.setBackground(getResources().getDrawable(R.drawable.active_tab));
                binding.tvView.setTextColor(getResources().getColor(R.color.white));
                break;

        }
    }

    private void setViewPager(int index) {
        binding.viewPagerGetCoin.setCurrentItem(index);

    }



}
