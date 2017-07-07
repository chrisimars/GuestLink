package guestlink.kodakalaris.com.guestlink;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import static java.lang.String.valueOf;

public class sysSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_setup);
        Spinner spinGuestIdLength = (Spinner) findViewById(R.id.spinGuestIDLength);
    }


    public void save(View view){
        Globals t = Globals.getInstance();  //Get a n instance of the Global Variables
        SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);

        Spinner spin = (Spinner) findViewById(R.id.spinGuestIDLength);
        t.setguestIdLength(valueOf(spin.getSelectedItem()));
        sharedPreferences.edit().putString("guestIdLength", t.getguestIdLength()).apply();

        finish();
    }

    public void cancel(View view){
        super.onBackPressed();
    }
}
