package guestlink.kodakalaris.com.guestlink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Spinner;
import static java.lang.String.valueOf;

public class sysSetup extends AppCompatActivity {
    private final String logFile = Environment.getExternalStorageDirectory().getPath() + "/guestlink/guestLinkLog.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sys_setup);
        Spinner spinGuestIdLength = (Spinner) findViewById(R.id.spinGuestIDLength);
    }

    public void save(View view){
        Globals t = Globals.getInstance();  //Get a n instance of the Global Variables
        SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);
        Spinner spin = (Spinner) findViewById(R.id.spinGuestIDLength);
        t.setguestIdLength(valueOf(spin.getSelectedItem()));
        sharedPreferences.edit().putString("guestIdLength", t.getguestIdLength()).apply();
        finishAndRemoveTask();
    }

    public void cancel(View view){
        try {
            Utilities.showYesNoDialog(this, "Exit", "Are You Sure You Want to Exit Settings without Saving?", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            finishAndRemoveTask();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
}
