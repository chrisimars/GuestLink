package guestlink.kodakalaris.com.guestlink;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class password extends AppCompatActivity {
    private EditText txtPassword;
    private final String logFile =  "sdcard/guestlink/guestLinkLog.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_password);
       txtPassword = (EditText) findViewById(R.id.txtPassword);
       }

    public void btnOK_Click(View view){
       try {

            if(txtPassword.getText().toString().equals("imagic")) {
                Intent intentMessage=new Intent();

                // put the message to return as result in Intent
                intentMessage.putExtra("MESSAGE","passwordOK");
                // Set The Result in Intent
                setResult(2,intentMessage);
                super.onBackPressed();
           }else{
                Utilities.showDialog(this, "Incorrect Password", "Please Enter the Correct Password.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                txtPassword.requestFocus();
                                txtPassword.setText("");

                        }
                    }
                });
            }
         } catch (Exception ex) {
           Utilities.writeToFile(ex.getMessage());

        }
    }
    public void cancel(View view){
        try {
            Utilities.showYesNoDialog(this, "Cancel", "Cancel Entering the Password?", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            finish();
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
