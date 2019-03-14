package instahelper.ghonchegi.myfollower.Fragments.GetCoin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import instahelper.ghonchegi.myfollower.R;


public class GetCoinFolloweFragment extends Fragment {
    private View view;

    public GetCoinFolloweFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_coin_follower, container, false);



        return view;

    }


}
