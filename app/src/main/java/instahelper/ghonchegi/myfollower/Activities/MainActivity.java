package instahelper.ghonchegi.myfollower.Activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import instahelper.ghonchegi.myfollower.App;
import instahelper.ghonchegi.myfollower.Fragments.GetCoin.GetCoinFragment;
import instahelper.ghonchegi.myfollower.Fragments.HomeFragment;
import instahelper.ghonchegi.myfollower.Fragments.Purchase.PurchaseFragment;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.Models.User;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.data.InstagramUser;
import instahelper.ghonchegi.myfollower.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private HomeFragment homeFragment=new HomeFragment();
    private PurchaseFragment purchaseFragment=new PurchaseFragment();
    private GetCoinFragment getCoinFragment=new GetCoinFragment();
    private int currentItemId;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setVariables();
        fm.beginTransaction().replace(R.id.fragmentHolder, homeFragment, "shopFragment").commit();
        currentItemId = R.id.action_home;


        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() != currentItemId)
                    switch (item.getItemId()) {
                        case R.id.action_advertise:

                            Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.action_navigation:
                            currentItemId = R.id.action_navigation;
                            fm.beginTransaction().replace(R.id.fragmentHolder, getCoinFragment, "shopFragment").commit();

                            break;
                        case R.id.action_home:
                            currentItemId = R.id.action_home;
                            fm.beginTransaction().replace(R.id.fragmentHolder, homeFragment, "shopFragment").commit();


                            break;
                        case R.id.action_purchase:
                            currentItemId = R.id.action_purchase;
                            fm.beginTransaction().replace(R.id.fragmentHolder, purchaseFragment, "shopFragment").commit();

                            break;
                        case R.id.action_shopping:
                            Toast.makeText(MainActivity.this, "5", Toast.LENGTH_SHORT).show();

                            break;
                    }
                return true;
            }
        });
    }

    private void setVariables() {
        fm = getSupportFragmentManager();

    }

}

