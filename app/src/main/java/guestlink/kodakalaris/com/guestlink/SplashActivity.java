package guestlink.kodakalaris.com.guestlink;

//Created by Donald Chapman1 on 7/10/2017.

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, GuestLinkMainActivity.class);
        startActivity(intent);
        finish();
    }
}
