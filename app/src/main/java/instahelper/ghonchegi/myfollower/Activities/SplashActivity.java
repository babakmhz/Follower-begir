package instahelper.ghonchegi.myfollower.Activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import instahelper.ghonchegi.myfollower.Dialog.InstagramAutenticationDialog;
import instahelper.ghonchegi.myfollower.Manager.DataBaseHelper;
import instahelper.ghonchegi.myfollower.R;
import instahelper.ghonchegi.myfollower.databinding.ActivitySplashBinding;
import instahelper.ghonchegi.myfollower.instaAPI.InstagramApi;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySplashBinding binding;
    private InstagramApi api = InstagramApi.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        try {
            dataBaseHelper.createDatabase();
            dataBaseHelper.openDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (api.IsLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        viewClickListener();
    }

    private void viewClickListener() {
        binding.btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                authenticate();
                break;
        }

    }

    private void authenticate() {
        // startActivity(new Intent(this,ActivityLoginWebview.class));
        InstagramAutenticationDialog dialog = new InstagramAutenticationDialog();
        dialog.setCancelable(true);
        dialog.show(getSupportFragmentManager(), ":");


    }
}
