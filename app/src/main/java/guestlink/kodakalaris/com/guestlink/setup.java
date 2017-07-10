package guestlink.kodakalaris.com.guestlink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import static java.lang.String.valueOf;

public class setup extends AppCompatActivity {
    private final String logFile =  Environment.getExternalStorageDirectory().getPath() + "/guestlink/guestLinkLog.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setup);
        SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

         /* Set the app into full screen mode */
        //getWindow().getDecorView().setSystemUiVisibility(flags);
        setContentView(R.layout.activity_setup);

       //Populate Spinners
        populateSpinner("photographers");
        populateSpinner("locations");
        EditText devName = (EditText) findViewById(R.id.editDeviceName);
        devName.setText(sharedPreferences.getString("deviceName", "Scan_"));
    }

    private void populateSpinner(String spinnerName) {
        String fileName = (Environment.getExternalStorageDirectory().getPath() + "/guestlink/" + spinnerName + ".txt");
        String line;
        ArrayList<String> names = new ArrayList<>();

        //Read the lines of text into an ArrayList
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));
            if (!input.ready()) {
                throw new IOException();
            }
            while ((line = input.readLine()) != null) {
                names.add(line);
            }
            input.close();
            //Sort the list alphabetically
            Collections.sort(names);
            //Build the ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names);

            switch (spinnerName) {
                case "photographers": {
                    //Get handle for spinner object
                    Spinner spinPhotographers = (Spinner) findViewById(R.id.spinPhotographer);
                    //Populate the spinner from the array
                    spinPhotographers.setAdapter(adapter);
                    //spinPhotographers.setOnItemSelectedListener(this);
                }
                case "locations":{
                    //Get handle for spinner object
                    Spinner spinLocations = (Spinner) findViewById(R.id.spinLocation);
                    //Populate the spinner from the array
                    spinLocations.setAdapter(adapter);
                    spinLocations.setBackgroundColor(Color.WHITE);
                    //spinLocations.setOnItemSelectedListener(this);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    public void save(View view){
        try {
                Globals t = Globals.getInstance();  //Get a n instance of the Global Variables
                SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);

                Spinner spin = (Spinner) findViewById(R.id.spinPhotographer);
                t.setPhotographer(valueOf(spin.getSelectedItem()));
                sharedPreferences.edit().putString("photographerName", t.getPhotographer()).apply();

                spin = (Spinner) findViewById(R.id.spinLocation);
                t.setLocation(valueOf(spin.getSelectedItem()));
                sharedPreferences.edit().putString("locationName", t.getLocation()).apply();

                EditText v = (EditText) findViewById(R.id.editDeviceName);
                String name = v.getText().toString();
                t.setdeviceName(v.getText().toString());
                sharedPreferences.edit().putString("deviceName", name).apply();
                finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    private boolean checkDeviceName() {
        EditText v = (EditText) findViewById(R.id.editDeviceName);
        String name = v.getText().toString();
        String defaultName = "SCAN_";
        boolean mResult = false;

        if (name.equals(defaultName)) {
            Utilities.showDialog(this, "Set Device Name", "Please Enter a Device Name. Cannot be SCAN_", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            });
        }
       return mResult;
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
    // Method to handle the Click Event on System Setup Button
    public void getSysSetup(View view) {
        try {
            // Create The  Intent and Start The Activity to get the password
            Intent intentGetSysSetup = new Intent(this, password.class);
            startActivityForResult(intentGetSysSetup, 2);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            // check if the request code is same as what is passed  here it is 2
            if (requestCode == 2) {
                // fetch the message String
                String message = data.getStringExtra("MESSAGE");
                    if (message.equals("passwordOK")){
                        // Create The  Intent and Start The Activity to scan the camera Barcode
                        Intent intentGetSysSetup = new Intent(this, sysSetup.class);
                        startActivityForResult(intentGetSysSetup, 2);
                        Utilities.writeToLog("User entered system Setup.", logFile);
                    }
            }
        } catch (Exception ex) {
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();

        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    @Override
    protected void onResume() {
        try {
            super.onResume();


        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }

    }
  public void getSubjects(View view){
      try {
          // Create The  Intent and Start The Activity to scan the camera Barcode
          Intent intentGetSubjects = new Intent(this, getSubjects.class);
          startActivity(intentGetSubjects);
          Utilities.writeToLog("User is selecting subjects.", logFile);
      } catch (Exception ex) {
          ex.printStackTrace();
          Utilities.writeToLog(ex.toString(), logFile);
      }

  }

}