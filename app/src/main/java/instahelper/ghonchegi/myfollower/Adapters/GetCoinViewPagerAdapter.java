package instahelper.ghonchegi.myfollower.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinCommentFragment;
import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinFolloweFragment;
import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinLikeFragment;

public class GetCoinViewPagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    public GetCoinViewPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GetCoinLikeFragment();
            case 1:
                return new GetCoinCommentFragment();
            case 2:
                return new GetCoinFolloweFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
