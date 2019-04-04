package instahelper.ghonchegi.myfollower.Fragments.Offers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import instahelper.ghonchegi.myfollower.Adapters.OffersAdapter;
import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Interface.PurchaseInterface;
import instahelper.ghonchegi.myfollower.Interface.ShopItemInterface;
import instahelper.ghonchegi.myfollower.Manager.BroadcastManager;
import instahelper.ghonchegi.myfollower.Manager.Config;
import instahelper.ghonchegi.myfollower.Manager.JsonManager;
import instahelper.ghonchegi.myfollower.Models.Offers;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.FragmentShopBinding;

import static instahelper.ghonchegi.myfollower.App.Base_URL;
import static instahelper.ghonchegi.myfollower.App.requestQueue;

@SuppressLint("ValidFragment")
public class ShopFragment extends Fragment {
    private final PurchaseInterface callbackPurchaseBanner;
    private final ShopItemInterface callBackShopItem;
    FragmentShopBinding binding;
    private View view;
    private String specialBannerItemId;
    private Dialog progressDialog;

    public ShopFragment(PurchaseInterface callBack, ShopItemInterface callBackShopItem) {
        this.callbackPurchaseBanner = callBack;
        this.callBackShopItem = callBackShopItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_shop, container, false);
        view = binding.getRoot();

        init();
        getOffers();

        binding.edtGiftCode.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                validateGiftCode();
                return true;
            }
            return false;
        });

        return view;
    }

    private void init() {

        Picasso.get().load(App.profilePicURl).into(binding.profileImage);
        binding.tvFollowerCoinCount.setText(App.followCoin + "");
        binding.tvLikeCoinCount.setText(App.likeCoin + "");
        binding.tvUserName.setText(App.user.getUserName());

        JSONObject jsonRootObject = null;
        try {
            jsonRootObject = new JSONObject(App.responseBanner);
            JSONObject childJson = jsonRootObject.getJSONObject("special_banner");
            binding.tvGoldTitle.setText(childJson.getInt("follow_coin") + " سکه فالو و" + childJson.getInt("like_coin") + " سکه لایک");
            binding.tvGoldSubtitle.setText("تنها با " + childJson.getInt("price") + " تومان");
            specialBannerItemId = childJson.getString("special_banner_RSA");
            binding.tvSpecialBannerPrice.setText(childJson.getInt("price") + " تومان");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        binding.secondContainer.setOnClickListener(v -> {
            try {
                JSONObject jsonObject = new JSONObject(App.responseBanner);
                JSONObject bannerCount = jsonObject.getJSONObject("special_banner");
                Config.SKUSpecialBanner = bannerCount.getString("special_banner_RSA");
                callbackPurchaseBanner.specialBanner("SpecialBanner", Config.ReqeuestSpeciialBanner, bannerCount.getInt("follow_coin"), bannerCount.getInt("like_coin"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        binding.btnCheckGiftCode.setOnClickListener(v -> validateGiftCode());


    }

    private void getOffers() {
        ProgressDialog(" در حال دریافت پیشنهادات ویژه شما... ");


        StringRequest request = new StringRequest(Request.Method.GET, Base_URL + "offer/list", response1 -> {
            if (response1 != null) {
                ArrayList<Offers> offers = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response1);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Offers offer = new Offers();
                        offer.setId(object.optInt("id"));
                        offer.setPrice(object.optInt("price"));
                        offer.setType(object.optInt("type"));
                        offer.setCount(object.optInt("count"));
                        offer.setPrice_discount(object.optInt("price_discount"));
                        offer.setRsa(object.optString("RSAKey"));
                        offers.add(offer);
                        progressDialog.dismiss();

                    }
                    setView(offers);

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
            progressDialog.dismiss();

        });
        request.setTag(this);
        requestQueue.add(request);
    }


    private void setView(ArrayList<Offers> offers) {
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        @SuppressLint("WrongConstant") LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //decoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        OffersAdapter adapter = new OffersAdapter(getContext(), offers, callBackShopItem);
        binding.rcvOffers.setLayoutManager(mLayoutManager);
        binding.rcvOffers.setItemAnimator(new DefaultItemAnimator());
        binding.rcvOffers.setAdapter(adapter);
        binding.rcvOffers.addItemDecoration(decoration);
        binding.rcvOffers.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rcvOffers.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < binding.rcvOffers.getChildCount(); i++) {
                    View v = binding.rcvOffers.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(300)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });
    }


    private void validateGiftCode() {
        if (TextUtils.isEmpty(binding.edtGiftCode.getText().toString())) {
            Toast.makeText(getContext(), "گد را وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        final String requestBody = JsonManager.validateOfferCode(binding.edtGiftCode.getText().toString());

        StringRequest request = new StringRequest(Request.Method.POST, Base_URL + "check_gift_code", response1 -> {
            if (response1 != null) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response1);
                    if (!jsonRootObject.optBoolean("status")) {
                        Toast.makeText(getContext(), "کد وارد شده معتبر نیست", Toast.LENGTH_SHORT).show();
                    } else if (jsonRootObject.getBoolean("status")) {
                        if (jsonRootObject.getInt("type") == 0) {
                            Toast.makeText(getContext(), "تبریک!‌به سکه های شما " + jsonRootObject.getInt("amount") + " سکه لایک اضافه شد ", Toast.LENGTH_SHORT).show();
                        } else if (jsonRootObject.getInt("type") == 1) {
                            Toast.makeText(getContext(), "تبریک!‌به سکه های شما " + jsonRootObject.getInt("amount") + " سکه فالو اضافه شد ", Toast.LENGTH_SHORT).show();
                        }

                        App.followCoin = jsonRootObject.getInt("follow_coin");
                        App.likeCoin = jsonRootObject.getInt("like_coin");

                        BroadcastManager.sendBroadcast(getContext());

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> {
            Log.i("volley", "onErrorResponse: " + error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };
        request.setTag(this);
        requestQueue.add(request);

    }

    public void ProgressDialog(String progressMessage) {
        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_get_offers);
        TextView txtProgressMessage = progressDialog.findViewById(R.id.txtProgressMessage);
        if (progressMessage != null) {
            txtProgressMessage.setText(progressMessage);
        }

        progressDialog.show();
    }


}
