package com.follow.irani.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.follow.irani.Fragments.Purchase.PurchaseCommentFragment;
import com.follow.irani.Fragments.Purchase.PurchaseFolloweFragment;
import com.follow.irani.Fragments.Purchase.PurchaseLikeFragment;
import com.follow.irani.Fragments.Purchase.PurchaseViewFragment;
import com.follow.irani.Interface.DirectPurchaseDialogInterface;

public class PurchaseViewPagerDapter extends FragmentPagerAdapter {
    private final DirectPurchaseDialogInterface callBackDirectPurchase;
    private int numOfTabs;

    public PurchaseViewPagerDapter(FragmentManager fm, int numOfTabs, DirectPurchaseDialogInterface callBackDirectPurchase) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.callBackDirectPurchase=callBackDirectPurchase;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PurchaseLikeFragment(callBackDirectPurchase);
            case 1:
                return new PurchaseCommentFragment(callBackDirectPurchase);
            case 2:
                return new PurchaseFolloweFragment(callBackDirectPurchase);
            case 3:
                return new PurchaseViewFragment(callBackDirectPurchase);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
