package guestlink.kodakalaris.com.guestlink;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    public void btnOK_Click(View view){
       try {
            String pw = "";
            pw.equals(txtPassword.getText().toString());

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
           Utilities.writeToFile(ex.getMessage().toString());
        }
    }
    public void cancel(View view){
        super.onBackPressed();
    }

}
