package instahelper.ghonchegi.myfollower.Fragments.GetCoin;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentGetCoinLikeBinding;


public class GetCoinLikeFragment extends Fragment {
    FragmentGetCoinLikeBinding binding;
    private View view;

    public GetCoinLikeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_get_coin_like, container, false);

        view = binding.getRoot();


        return view;

    }


}
