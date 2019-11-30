package com.follow.irani.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.follow.irani.App;
import com.follow.irani.Dialog.AuthenticationDialog;
import com.follow.irani.Manager.DataBaseHelper;
import com.follow.irani.R;
import com.follow.irani.databinding.ActivitySplashBinding;
import com.follow.irani.instaAPI.InstagramApi;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySplashBinding binding;
    private InstagramApi api = InstagramApi.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.currentActivity=this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        if (!App.isNetworkAvailable()) {
            startActivity(new Intent(SplashActivity.this, NetworkErrorActivity.class));
            finish();
            return;
        }

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
        AuthenticationDialog dialog = new AuthenticationDialog(false, null, null);
        dialog.setCancelable(true);
        dialog.show(getSupportFragmentManager(), ":");


    }
}