package instahelper.ghonchegi.myfollower.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import instahelper.ghonchegi.myfollower.Fragments.Purchase.PurchaseCommentFragment;
import instahelper.ghonchegi.myfollower.Fragments.Purchase.PurchaseFolloweFragment;
import instahelper.ghonchegi.myfollower.Fragments.Purchase.PurchaseLikeFragment;
import instahelper.ghonchegi.myfollower.Fragments.Purchase.PurchaseLikeRobotFragment;

public class PurchaseViewPagerDapter extends FragmentPagerAdapter {
    private int numOfTabs;

    public PurchaseViewPagerDapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PurchaseLikeFragment();
            case 1:
                return new PurchaseCommentFragment();
            case 2:
                return new PurchaseFolloweFragment();
            case 3:
                return new PurchaseLikeRobotFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
