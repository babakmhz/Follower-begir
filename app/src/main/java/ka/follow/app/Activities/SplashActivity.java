package ka.follow.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import ka.follow.app.Dialog.AuthenticationDialog;
import ka.follow.app.Dialog.InstagramAutenticationDialog;
import ka.follow.app.Manager.DataBaseHelper;
import ka.follow.app.R;
import ka.follow.app.databinding.ActivitySplashBinding;
import ka.follow.app.instaAPI.InstagramApi;

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
        InstagramAutenticationDialog dialog = new InstagramAutenticationDialog(false, null, null);
        dialog.setCancelable(true);
        dialog.show(getSupportFragmentManager(), ":");


    }
}
