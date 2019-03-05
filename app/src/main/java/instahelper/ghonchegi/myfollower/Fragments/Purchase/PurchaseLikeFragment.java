package instahelper.ghonchegi.myfollower.Fragments.Purchase;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Dialog.SelectPictureDialog;
import instahelper.ghonchegi.myfollower.Interface.ImagePickerInterface;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentPurchaseLikeBinding;

import static instahelper.ghonchegi.myfollower.App.TAG;


public class PurchaseLikeFragment extends Fragment implements ImagePickerInterface {


    ImagePickerInterface callback;
    FragmentPurchaseLikeBinding binding;

    public PurchaseLikeFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_purchase_like, container, false);
        callback = this;
        View view = binding.getRoot();

        binding.imvPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPictureDialog selectPictureDialog = new SelectPictureDialog(callback);
                selectPictureDialog.show(getChildFragmentManager(), ":");

            }
        });


        binding.tvLikeCoinCounts.setText(App.likeCoin + "");
        binding.tvLikeExpenseCount.setText(0 + "");
        binding.tvLikeOrderCount.setText("0");
        binding.seekBar.setProgress(0);
        binding.seekBar.setMax(App.likeCoin / 2);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvLikeOrderCount.setText(progress + "");
                binding.tvLikeExpenseCount.setText(progress * 2 + "");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return view;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void selectedPic(String imageId, String imageURL) {
        binding.constraintLayout.setBackground(null);
        binding.imvSelectPic.setVisibility(View.INVISIBLE);
        binding.tvSelectPic.setVisibility(View.INVISIBLE);
        binding.imvPickImage.setBackgroundDrawable(getActivity().getDrawable(R.drawable.rounded_orange));
        try {
            Picasso.get().load(imageURL).error(R.drawable.app_logo).into(binding.imvSelectedPic);
        } catch (Exception e) {
            Log.i(TAG, "selectedPic: " + e);
        }


    }

    private void submitOrder() {
        if (binding.imvSelectedPic == null) {
            Toast.makeText(getContext(), "یک عکس انتخاب کنید", Toast.LENGTH_SHORT).show();
        } else if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(getContext(), "تعداد سفارش را مشخص کنید", Toast.LENGTH_SHORT).show();
        } else {



        }
    }
}
