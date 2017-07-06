package guestlink.kodakalaris.com.guestlink;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class password extends AppCompatActivity {
    EditText txtPassword;
    private String logFile =  "sdcard/guestlink/guestLinkLog.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
       txtPassword = (EditText) findViewById(R.id.txtPassword);
    }

    // Method to handle the Click Event on System Setup Button
    public void getSysSetup(View view) {
        try {
            // Create The  Intent and Start The Activity to scan the camera Barcode
            Intent intentGetSysSetup = new Intent(this, sysSetup.class);
            startActivityForResult(intentGetSysSetup, 2);// Activity is started with requestCode 2
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }


    // SystemSetup Callback
    // Call Back method  to get the Message form other Activity    override the method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            // check if the request code is same as what is passed  here it is 2
            if (requestCode == 2) {
                // Call the System Setup Activity
                String message = data.getStringExtra("MESSAGE");


                Utilities.writeToLog("User Entered System Setup", logFile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }


    public void btnOK_Click(View view){
       try {
            String pw = "";
            pw.equals(txtPassword.getText().toString());

            if(txtPassword.getText().toString().equals("imagic")) {
                Intent intentMessage=new Intent();

                // put the message to return as result in Intent
                intentMessage.putExtra("MESSAGE","PasswordOK");
                // Set The Result in Intent
                setResult(2,intentMessage);
                super.onBackPressed();
           }else{
                Utilities.showDialog(this, "Incorrect Password", "Please Enter the Correct PAssword.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                txtPassword.requestFocus();
                        }
                    }
                });
            }
         } catch (Exception ex) {
           Utilities.writeToFile(ex.getMessage().toString());
        }
    }
    public void cancel(View view){
        super.onBackPressed();
    }

}
