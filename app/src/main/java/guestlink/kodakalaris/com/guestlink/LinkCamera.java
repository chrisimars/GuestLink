package guestlink.kodakalaris.com.guestlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LinkCamera extends Activity {
    TextView camSerial;

    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_camera);

        // Get the Reference of the Edit Text object that has the camera serial number
        camSerial=(TextView)findViewById(R.id.txtCamSerial);

         /* Set the app into full screen mode */
        getWindow().getDecorView().setSystemUiVisibility(flags);

        //Disable Keyboard Input for Guest ID value to prevent barcode scan and soft keyboard
        TextView csnBox = (TextView) findViewById((R.id.txtCamSerial));
        //csnBox.setKeyListener(null);
  }
    public void btnOK_Click(View view){

        //TODO  Check for empty string in Cam Serial and Alert user
        // get the Entered  message
        String message=camSerial.getText().toString();
        Intent intentMessage=new Intent();

        // put the message to return as result in Intent
        intentMessage.putExtra("MESSAGE",message);
        // Set The Result in Intent
        setResult(2,intentMessage);
        super.onBackPressed();
  }
  public void btnCancel_Click(View view){
      super.onBackPressed();
  }
}
