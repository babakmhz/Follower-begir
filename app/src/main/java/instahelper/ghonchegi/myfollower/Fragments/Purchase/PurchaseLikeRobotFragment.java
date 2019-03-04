package instahelper.ghonchegi.myfollower.Fragments.Purchase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import instahelper.ghonchegi.myfollower.R;


public class PurchaseLikeRobotFragment extends Fragment {
    private View view;

    public PurchaseLikeRobotFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase, container, false);
        


        return view;

    }


}
