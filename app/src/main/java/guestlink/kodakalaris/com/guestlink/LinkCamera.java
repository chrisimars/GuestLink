package guestlink.kodakalaris.com.guestlink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LinkCamera extends Activity {
    private TextView camSerial;
    private EditText tempText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_camera);

        // Get the Reference of the Edit Text object that has the camera serial number
        camSerial=(EditText)findViewById(R.id.txtCamSerial);
        camSerial.setSelectAllOnFocus(true);
        tempText = (EditText)findViewById(R.id.tempText);
        tempText.requestFocus();

         /* Set the app into full screen mode */
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }


    public void btnOK_Click(View view){

        try {
            //TODO  Check for empty string in Cam Serial and Alert user
            // get the Entered  message
            String message=camSerial.getText().toString();
            Intent intentMessage=new Intent();

            // put the message to return as result in Intent
            intentMessage.putExtra("MESSAGE",message);
            // Set The Result in Intent
            setResult(2,intentMessage);
            //super.onBackPressed();

            finishAndRemoveTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  public void btnCancel_Click(View view){
      //super.onBackPressed();
      finishAndRemoveTask();
  }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                //TODO Handle Enter Key Here

                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        try {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                camSerial.setText("");
                camSerial.setText(tempText.getText().toString());
                tempText.setText("");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
