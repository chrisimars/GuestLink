package guestlink.kodakalaris.com.guestlink;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

 //Created by Donald Chapman1 on 6/16/2017.

final class Utilities {
public String guestidFile = "";
private String logFile = Environment.getExternalStorageDirectory().getPath() + "/guestlink/guestLinkLog.txt";

    public static void showYesNoDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setPositiveButton("Yes", onClickListener);
            dialog.setNegativeButton("No", null);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setPositiveButton("Ok", onClickListener);
            //dialog.setNegativeButton("Cancel", null);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean doesFileExist(String filename){
        try {
            File f = new File(filename);
            if(f.exists() && !f.isDirectory()){
            return true;
     }
       } catch (Exception e) {
            e.printStackTrace();
       }
        return false;
    }

    public static void writeGuestIdFile(String camSerial, String lastGuestID, String fileName, String dateTime, String metaData) {
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        //Date now = new Date();

        try {
            //Make sure the directory exists
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/guestlink");
            if (!file.exists()) {
                file.mkdirs();
            }

            //Check for the Guest ID file. Create it if it does not exist
            File gidfile = new File(fileName);
            if (!gidfile.exists()) {
                gidfile.createNewFile();
            }

            //Append the Guest ID string to the file
            String sBody = camSerial + "," + dateTime + lastGuestID + "," + metaData + "\r\n";
            FileWriter writer = new FileWriter(fileName, true);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void writeToLog(String message, String fileName) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat ct = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        String time = ct.format(cal.getTime());
        try {

            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/guestlink");
            if (!dir.exists()) {
                dir.mkdir();
            }
            //Check for the error log file. Create it if it does not exist
            File logfile = new File(fileName);
            if (!logfile.exists()) {
                logfile.createNewFile();
            }

            //Append the error message string to the file
            String sBody = time + " " + message + "\r\n";
            FileWriter writer = new FileWriter(fileName, true);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String data) {
        try {
            String fileName = Environment.getExternalStorageDirectory().getPath() + "/guestLink/json.txt";

            File datafile = new File(fileName);
            if (!datafile.exists()) {
                datafile.createNewFile();
            }

            //Append the error message string to the file
            String sBody = data  + "\r\n";
            FileWriter writer = new FileWriter(fileName, true);
            writer.append(sBody);
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public static void createDefatulMetadataFiles(){
        String photographers = Environment.getExternalStorageDirectory().getPath() + "/guestLink/photographers.txt";
        String locations = Environment.getExternalStorageDirectory().getPath() + "/guestLink/locations.txt";
        String subjects = Environment.getExternalStorageDirectory().getPath() + "/guestLink/subjects.txt";

        try {
            //Create the default photographers file
            File datafile = new File(photographers);
            if (!datafile.exists()) {
                datafile.createNewFile();
                FileWriter writer = new FileWriter(photographers, true);
                writer.append("Photographer1" + "\r\n");
                writer.append("Photographer2" + "\r\n");
                writer.append("Photographer3" + "\r\n");
                writer.flush();
                writer.close();
                }

            datafile = new File(locations);
            if (!datafile.exists()) {
                datafile.createNewFile();
            FileWriter writer = new FileWriter(locations, true);
            writer.append("Location1" + "\r\n");
            writer.append("Location2" + "\r\n");
            writer.append("Location3" + "\r\n");
            writer.flush();
            writer.close();
        }

            datafile = new File(subjects);
            if (!datafile.exists()) {
                datafile.createNewFile();

            FileWriter writer = new FileWriter(subjects, true);
            writer.append("subject1"+ "\r\n");
            writer.append("subject2"+ "\r\n");
            writer.append("subject3"+ "\r\n");
            writer.flush();
            writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
