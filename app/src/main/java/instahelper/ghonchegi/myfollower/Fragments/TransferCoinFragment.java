package instahelper.ghonchegi.myfollower.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import instahelper.ghonchegi.myfollower.R;


public class TransferCoinFragment extends Fragment {
    private View view;
    private RecyclerView rcvPics;

    public TransferCoinFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transfer_coin, container, false);


        return view;

    }

}
