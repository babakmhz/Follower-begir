package com.follow.nobahar.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.follow.nobahar.Fragments.GetCoin.GetCoinFolloweFragment;
import com.follow.nobahar.Fragments.GetCoin.GetCoinViewFragment;
import com.follow.nobahar.Interface.AddCoinMultipleAccount;
import com.follow.nobahar.Fragments.GetCoin.GetCoinCommentFragment;
import com.follow.nobahar.Fragments.GetCoin.GetCoinLikeFragment;

public class GetCoinViewPagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;
    private AddCoinMultipleAccount addCoinMultipleAccount;

    public GetCoinViewPagerAdapter(FragmentManager fm, int numOfTabs, AddCoinMultipleAccount addCoinMultipleAccount) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.addCoinMultipleAccount = addCoinMultipleAccount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GetCoinLikeFragment(addCoinMultipleAccount);
            case 1:
                return new GetCoinCommentFragment(addCoinMultipleAccount);
            case 2:
                return new GetCoinFolloweFragment(addCoinMultipleAccount);
            case 3:
                return new GetCoinViewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
