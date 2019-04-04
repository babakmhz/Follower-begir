package instahelper.ghonchegi.myfollower.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinCommentFragment;
import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinFolloweFragment;
import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinLikeFragment;
import instahelper.ghonchegi.myfollower.Interface.AddCoinMultipleAccount;

public class GetCoinViewPagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;
    private AddCoinMultipleAccount addCoinMultipleAccount;

    public GetCoinViewPagerAdapter(FragmentManager fm, int numOfTabs, AddCoinMultipleAccount addCoinMultipleAccount) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.addCoinMultipleAccount=addCoinMultipleAccount;
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

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
