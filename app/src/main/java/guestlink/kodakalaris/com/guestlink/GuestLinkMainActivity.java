package guestlink.kodakalaris.com.guestlink;
// This is a desperate attempt to recreate the GuestLink PDA in Android
//While I appreciate the opportunity to learn something new I find it
//interesting that they assigned this to "the person that was not supposed
//to write any software"!

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.valueOf;

public class GuestLinkMainActivity extends AppCompatActivity {
    private static final String TAG = GuestLinkMainActivity.class.getSimpleName();
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechList;
    private static final int GetMessage = 2;
    private Integer totScans = 0, guestIdLength = 16;
    private String photographer = "Photographer", location = "Location 1", subjects = "Moe", eventID = "123456", guestidFile = "", deviceID = "";
    private String lastGuestID = "", metaData = "", camSerial = "", deviceName = "Scan_", logFile = "/sdcard/guestlink/guestLinkLog.txt";
    private DBHelper dbHelper;
    private JSONArray guestRecords;

    private final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            //Make sure the storage directory exists
            File file = new File("/sdcard/guestlink");
            if (!file.exists()) {
                file.mkdirs();
            }
            dbHelper = new DBHelper(this);

            Utilities.writeToLog("\r\n*************************\r\nApplication Startup\r\n *************************", logFile);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_guest_link_main);
            //Globals g = Globals.getInstance();     //Get a n instance of the Global Variables
            SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);

//*********************************************************************
            //Get the Device MAC Address to use in Guest ID Filename, save it to shared preferences
            //macAddress = getMacAddr().replace(":", "");

            //MAC Address replaced with Android_ID because MAC is not available with radio turned off
            deviceID = (Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            guestidFile = "/sdcard/guestlink/" + deviceID + ".txt";
            sharedPreferences.edit().putString("guestidFile", guestidFile).apply();

            //Get Other shared Metadata from shared preferences run if any
            getMetadata();
            updateDispayValues();
//***********************************************************************

            //startLockTask();   //Put Application in Kiosk Mode  FUTURE

            //Disable Keyboard Input for Guest ID value to prevent barcode scan and soft keyboard
            TextView gidBox = (TextView) findViewById((R.id.txtGuestID));
            gidBox.setInputType(InputType.TYPE_NULL);
            gidBox.setKeyListener(null);

            //Get Battery Level
            batteryLevel();

        /* Set the app into full screen mode */
            getWindow().getDecorView().setSystemUiVisibility(flags);
            //

            //Make sure GuestID file exists - if not, create it here using the MAC address as filename
            //Make sure the directory exists
            file = new File(guestidFile);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Make sure the log file exists - if not, create it
            file = new File(logFile);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Refresh the file indicies
            refreshFileIndex(guestidFile);
            refreshFileIndex(logFile);

            //Used in development
            if (!Utilities.doesFileExist(guestidFile)) {
                Utilities.showDialog(this, "Bite Me", "File Does NOT Exist", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }

            //***********Setup the NFC Reader and the Intent *************
            mAdapter = NfcAdapter.getDefaultAdapter(this);
            if (mAdapter == null) {
                this.finish();
                return;
            }

            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            try {
                filter.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                e.printStackTrace();
            }
            mIntentFilters = new IntentFilter[]{filter};
            mTechList = new String[][]{new String[]{MifareClassic.class.getName()}};
            //************************************************************

            //*********** Populate GUI with the Date *************
            TextView txtDate = (TextView) findViewById(R.id.txtDate);
            String ct = DateFormat.getDateInstance().format(new Date());
            txtDate.setText(ct);

            //*** Check the External Storage for Read Write Access ***
            if (!Utilities.isExternalStorageWritable()) {
                burntToast("Check SD Card");
            }
            if (!Utilities.isExternalStorageReadable()) {
                toast("Check SD Card");
            }

            Utilities.writeToLog("Application OnCreate Completed", logFile);
            refreshFileIndex(logFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }



    @Override
    protected void onResume() {
        try {
            super.onResume();
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mTechList);
        /* Reset the app into full screen mode */
            getWindow().getDecorView().setSystemUiVisibility(flags);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }

        getMetadata();
        updateDispayValues();
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            mAdapter.disableForegroundDispatch(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            return;
        }
        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh:mm:ss:sss");      // a|SSS|");
        String dateTime = df.format(c.getTime());
        resolveIntent(intent, dateTime);
    }

    private void resolveIntent(Intent intent, String dateTime) {
        //debug("resolveIntent...", false);
//TODO  Need some kind of Alert if they Scan a Guest ID with no Camera Linked

        MifareClassic mfc = MifareClassic.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        if (mfc == null) {
            burntToast("Invalid Card Type");
            return;
        }
       try {
            String result1 = performRead(mfc);

            if (result1 != null) {
                //debug(result1, true);   /Used to display TOAST during development
                //Set variable LastGuestID to be used in the file writer in the utilities class
                lastGuestID = result1;

                //Write GuestID File
                Utilities.writeGuestIdFile(camSerial, result1, guestidFile, dateTime, metaData);
                Utilities.writeToLog("GUEST ID RECEIVED: " + result1, logFile);
                toast("Last Scan = " + result1);

                //Update Display with the just scanned GuestID value
                TextView editText = (TextView) findViewById(R.id.txtGuestID);
                editText.setText(result1, TextView.BufferType.NORMAL);
                editText.setTextColor(Color.WHITE);

                //Increment total scans variable and update GUI with the count
                totScans += 1;
                TextView txtTotScans = (TextView) findViewById(R.id.txtTotScans);
                txtTotScans.setText(String.valueOf(totScans));

                dbHelper.insertRecord(deviceID, camSerial, dateTime, result1, photographer, location,
                        subjects, eventID);
            }
        } catch (IOException ex) {
            Utilities.writeToLog(ex.toString(), logFile);
        } finally {

            try {
                mfc.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.e(TAG, ex.toString());
            }
        }
    }

    private String performRead(MifareClassic mfc) throws IOException {
        String ret = null;
        try {
            if (!mfc.isConnected()) {
                mfc.connect();
            }
            ret = null;
            int blockIndex = 12;       //Hard code to Block 12 for version 1
            int sectorIndex = mfc.blockToSector(blockIndex);
            boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
            if (auth) {
                byte[] data = readBlock(mfc, blockIndex);
                ret = convertBytes2String(data);
            } else {
                burntToast("Bad Card Read, Try Again");
                badVibrate();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
            refreshFileIndex(logFile);
        }
        return ret;
    }

    private byte[] readBlock(MifareClassic mfc, int blockIndex) throws IOException {
        try {
            return mfc.readBlock(blockIndex);
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
        return null;
    }

    private String convertBytes2String(byte[] data) throws UnsupportedEncodingException {
        try {
            String tempData = "";
            int pos = data.length;

            for (int i = data.length - 1; i >= 0; i--) {
                if (data[i] != 0) {
                    break;
                }
                pos = i;   //This value holds the number of actual characters out of the 16 available
            }

            //if Make sure there are either 8 or 16 actual values
            if ((pos == 8) || (pos == 16)) {
                //return a Sttring stripping out anything that is non Alpha-numeric
                String mCharset = "US-ASCII";
                tempData = new String(data, 0, pos, mCharset).replaceAll("[^A-Za-z0-9]", "");
                if (pos == 8) {
                    if (tempData.matches("^([A-Za-z]|[0-9])+$")) {
                        return tempData;
                    } else {
                        burntToast("Invalid Card Scan");
                        return null;
                    }
                } else {
                    if (tempData.matches("\\d+")) {
                        return tempData;
                    } else {
                        burntToast("Invalid Card Scan");
                        return null;
                    }
                }
            }else {
                burntToast("Invalid Card Scan");
                return null;
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
        return null;
    }

    public void alert(String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(msg);
            dialog.show();
        } catch (Exception ex) {
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

     private void batteryLevel() {
        try {
            BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
                @SuppressLint("SetTextI18n")
                public void onReceive(Context context, Intent intent) {
                    int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int level = -1;

                    if (rawlevel >= 0 && scale > 0) {
                        level = (rawlevel * 100) / scale;
                    }

                    //Update the Battery ICON label with the percent remaining
                    TextView txtBattLevel = (TextView) findViewById(R.id.txtBattLevel);
                    txtBattLevel.setText(String.valueOf(level) + "%");

                    if (level >= 61) {
                        ImageView img = (ImageView) findViewById(R.id.picBattLevel);
                        img.setImageResource(R.drawable.batterygreen);
                    } else if (level >= 26 && level <= 60) {
                        ImageView img = (ImageView) findViewById(R.id.picBattLevel);
                        img.setImageResource(R.drawable.batteryyellow);
                    } else if (level <= 25) {
                        ImageView img = (ImageView) findViewById(R.id.picBattLevel);
                        img.setImageResource(R.drawable.batteryred);
                    }
                }
            };
            IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
            Log.i("Info", ex.toString());
        }
    }

    // Method to handle the Click Event on Link Camera Button
    public void getCamera(View view) {
       try {
            // Create The  Intent and Start The Activity to scan the camera Barcode
            Intent intentGetCamera = new Intent(this, LinkCamera.class);
            startActivityForResult(intentGetCamera, 2);// Activity is started with requestCode 2
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    // Method to handle the Click Event on Setup Button
    public void getSetup(View view) {
     try {
            // Create The  Intent and Start The Activity to scan the camera Barcode
            Intent intentGetSetup = new Intent(this, setup.class);
            startActivity(intentGetSetup);
            //startActivityForResult(intentGetCamera, 2);// Activity is started with requestCode 2
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    // GETCAMERA Callback
    // Call Back method  to get the Message form other Activity    override the method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);
            // check if the request code is same as what is passed  here it is 2
            if (requestCode == 2) {
                // fetch the message String
                String message = data.getStringExtra("MESSAGE");

                // Set the message string in textView
                TextView s = (TextView) findViewById(R.id.txtCamSerial);
                s.setText(message);

                // Set the camera serial variable
                camSerial = message;
                sharedPreferences.edit().putString("camSerial", message).apply();
                Utilities.writeToLog("New Camera Linked: " + camSerial, logFile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.toString());
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    //Retreives the wireless MAC Address so we can use it for a unique number in the guest ID filename
    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Utilities.writeToLog(ex.toString(), logFile);
        }
        return "02:00:00:00:00:00";
    }

    //Reads Shared Preferences stored data into variables
    private void getMetadata() {
        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);
            photographer = sharedPreferences.getString("photographerName", "Photographer1");
            location = sharedPreferences.getString("locationName", "Location1");
            deviceName = sharedPreferences.getString("deviceName", "Scan_");
            guestIdLength = valueOf(sharedPreferences.getString("guestIdLength", "16"));
            camSerial = sharedPreferences.getString("camSerial", "");
            subjects = "Test Subject"; //sharedPreferences.getString("subjects", "");
            metaData = photographer + "," + eventID + "," + location + "," + subjects;
            Utilities.writeToLog("Metadata String = " + metaData, logFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    // }
    private void updateDispayValues() {
        try {
            TextView text = (TextView) findViewById(R.id.txtPhotographer);
            text.setText(photographer);

            text = (TextView) findViewById(R.id.txtLocation);
            text.setText(location);

            String empty = "";
            if (subjects.equals(empty)){
                text = (TextView) findViewById(R.id.txtSubjects);
                text.setVisibility(View.INVISIBLE);
                text = (TextView) findViewById(R.id.lblSubjects);
                text.setVisibility(View.INVISIBLE);
            }else{
                text = (TextView) findViewById(R.id.txtSubjects);
                text.setText(subjects);
            }
            text = (TextView) findViewById(R.id.txtDeviceName);
            text.setText(deviceName);

            text = (TextView) findViewById(R.id.txtCamSerial);
            text.setText(camSerial);

            TextView e = (TextView) findViewById((R.id.txtGuestID));
            e.setInputType(InputType.TYPE_NULL);

        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    private void toast(String message){
        try {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setBackgroundColor(Color.GREEN);
            v.setTextColor(Color.BLACK);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    private void burntToast(String message){
        try {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setBackgroundColor(Color.RED);
            v.setTextColor(Color.BLACK);
            toast.show();
            badVibrate();
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    public void closeSession(View view) {
        try {
            Utilities.showYesNoDialog(this, "Close Session", "Are You Sure You Want to Close this Session?", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //String ct = DateFormat.getDateInstance().format(new Date());
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat ct = new SimpleDateFormat("HH:mm:ss");
                            String time = ct.format(cal.getTime());
                            time = time.replace(":", "");
                            File from = new File(guestidFile);
                            String newFile = Environment.getExternalStorageDirectory().getPath() + "/guestlink/" + deviceID + "_CloseSession_" + time + ".txt";

                            File to = new File(newFile);
                            if (from.exists())
                                from.renameTo(to);

                            //Write CloseSession record to database
                            dbHelper.insertRecord(null, null, null, "CloseSession", null, null, null, null);
                            guestRecords = new JSONArray();
                            guestRecords = getRecords();

                            Utilities.writeToFile(guestRecords.toString());


                            //If we close the session, refresh the folder and file index
                            refreshFileIndex(Environment.getExternalStorageDirectory().getPath() + "/guestlink");//Yes button clicked
                            refreshFileIndex(newFile);//Yes button clicked
                            refreshFileIndex(logFile);
                            refreshFileIndex("/sdcard/guestlink/GuestIDs.db");

                            //Reset values and clear display
                            totScans = 0;
                            TextView view = (TextView) findViewById(R.id.txtTotScans);
                            view.setText("0");

                            TextView txt = (TextView) findViewById(R.id.txtGuestID);
                            txt.setText("Session Closed", TextView.BufferType.NORMAL);
                            txt.setTextColor(Color.GREEN);

                            // Set the app into full screen mode
                            getWindow().getDecorView().setSystemUiVisibility(flags);
                            toast("Session Closed");
                            Utilities.writeToLog("User Closed Session", logFile);
                            dbHelper.createNew();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            toast("Close Session Cancelled");
                            // Set the app into full screen mode
                            getWindow().getDecorView().setSystemUiVisibility(flags);
                            break;
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    public void btnExit_Click(View view) {
        try {
            Utilities.showYesNoDialog(this, "Exit GuestLink", "Are You Sure You Want to Exit GuestLink?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Utilities.writeToLog("User Exited Application", logFile);
                            refreshFileIndex(logFile);
                            finishAndRemoveTask();
                    }
                }
            });
        } catch (Exception ex) {
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    private void refreshFileIndex(String file) {
        try {
            File f = new File(file);
            //File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());     //  + "/guestlink/guestLink.log");
            Uri contentUri = Uri.fromFile(f);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
            this.sendBroadcast(mediaScanIntent);
            this.sendBroadcast(mediaScanIntent);


            ArrayList<String> toBeScanned = new ArrayList<String>();
            toBeScanned.add(logFile);
            toBeScanned.add(guestidFile);
            String[] toBeScannedStr = new String[toBeScanned.size()];
            toBeScannedStr = toBeScanned.toArray(toBeScannedStr);

            MediaScannerConnection.scanFile(this, toBeScannedStr, null, new MediaScannerConnection.OnScanCompletedListener() {

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    System.out.println("SCAN COMPLETED: " + path);
                }
            });
       } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    public static void disableSoftInput(EditText editText) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
    }
    private void badVibrate(){
        Vibrator vibrator;
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.buzzer);
        long pattern[]={0,200,200,200,200};
        vibrator.vibrate(pattern, -1);
        mp.start();
    }

    private void goodVibrate(){
        Vibrator vibrator;
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.beep);
        long pattern[]={0,300,300,300,300};
        vibrator.vibrate(pattern, 0);
        vibrator.cancel();
        mp.start();
    }

    private JSONArray getRecords(){     //Here we grab the GuestIDs table and output it to a JSON Array Object
        try {
            Context context = getApplicationContext();
            context.getDatabasePath(DBHelper.DATABASE_NAME);
            String myPath = context.getDatabasePath(DBHelper.DATABASE_NAME).toString();     // Set path to your database
            String myTable = "guestids";
            SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

            String searchQuery = "SELECT  * FROM " + myTable;
            Cursor cursor = myDataBase.rawQuery(searchQuery, null );

            JSONArray resultSet  = new JSONArray();

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();
                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( cursor.getColumnName(i) != null )
                    {
                        try
                        {
                            if( cursor.getString(i) != null )
                            {
                                Log.d("TAG_NAME", cursor.getString(i) );
                                rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                            }
                            else
                            {
                                rowObject.put( cursor.getColumnName(i) ,  "" );
                            }
                        }
                        catch( Exception e )
                        {
                            Log.d("TAG_NAME", e.getMessage()  );
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
            cursor.close();
            Log.d("TAG_NAME", resultSet.toString() );
            return resultSet;
        } catch (Exception ex) {
            Utilities.writeToLog(ex.toString(), logFile);
            return null;
        }
    }
}


