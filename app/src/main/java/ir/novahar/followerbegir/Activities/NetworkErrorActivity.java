package ir.novahar.followerbegir.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import ir.novahar.followerbegir.App;
import ir.novahar.followerbegir.R;
import ir.novahar.followerbegir.databinding.DialogNetworkErrorBinding;

public class NetworkErrorActivity extends AppCompatActivity {

    private DialogNetworkErrorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_network_error);

        binding.btnReconnect.setOnClickListener(v -> {
            if (App.isNetworkAvailable()) {
                startActivity(new Intent(NetworkErrorActivity.this, SplashActivity.class));
                finish();
            }
        });
    }
}
