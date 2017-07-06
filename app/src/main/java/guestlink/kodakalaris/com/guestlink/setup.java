package guestlink.kodakalaris.com.guestlink;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.String.valueOf;

public class setup extends AppCompatActivity {
    private String logFile =  "sdcard/guestlink/guestLinkLog.txt";
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Globals g = Globals.getInstance();  //Get a n instance of the Global Variables
        SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);

         /* Set the app into full screen mode */
        //getWindow().getDecorView().setSystemUiVisibility(flags);

        //Get instances of the Spinners and apply OnItemSelectedListener on it
        Spinner spinPhotographer = (Spinner) findViewById(R.id.spinPhotographer);
        Spinner spinLocation = (Spinner) findViewById(R.id.spinLocation);
        Spinner spinGuestIdLength = (Spinner) findViewById(R.id.spinGuestidLength);

        //Populate Spinners
        populateSpinner("Photographers");
        populateSpinner("Locations");

        EditText devName = (EditText) findViewById(R.id.editDeviceName);
        devName.setText(sharedPreferences.getString("deviceName", "Scan_"));
    }

    private void populateSpinner(String spinnerName) {
        String fileName = ("/sdcard/guestlink/" + spinnerName + ".txt");
        String line;
        ArrayList<String> names = new ArrayList<String>();

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
                case "Photographers": {
                    //Get handle for spinner object
                    Spinner spinPhotographers = (Spinner) findViewById(R.id.spinPhotographer);
                    //Populate the spinner from the array
                    spinPhotographers.setAdapter(adapter);
                    //spinPhotographers.setOnItemSelectedListener(this);
                }
                case "Locations":{
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
            boolean ret = true;
            if (ret = true) {
                Globals t = Globals.getInstance();  //Get a n instance of the Global Variables
                SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);

                Spinner spin = (Spinner) findViewById(R.id.spinPhotographer);
                t.setPhotographer(valueOf(spin.getSelectedItem()));
                sharedPreferences.edit().putString("photographerName", t.getPhotographer()).apply();

                spin = (Spinner) findViewById(R.id.spinLocation);
                t.setLocation(valueOf(spin.getSelectedItem()));
                String h = t.getPhotographer();
                sharedPreferences.edit().putString("locationName", t.getLocation()).apply();

                spin = (Spinner) findViewById(R.id.spinGuestidLength);
                t.setguestIdLength(valueOf(spin.getSelectedItem()));
                sharedPreferences.edit().putString("guestIdLength", t.getguestIdLength()).apply();

                EditText v = (EditText) findViewById(R.id.editDeviceName);
                String name = v.getText().toString();
                t.setdeviceName(v.getText().toString());
                sharedPreferences.edit().putString("deviceName", name).apply();
                finish();
            }
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
        super.onBackPressed();
    }

}