package guestlink.kodakalaris.com.guestlink;
/**
 * Created by Donald Chapman1 on 6/30/2017.
 * This helper class extends the SQLiteOpenHelper and gives easy access to
 * write to the database.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Environment;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = Environment.getExternalStorageDirectory().getPath() + "/guestlink/GuestIDs.db";
    private static final int DATABASE_VERSION = 1;
    private static final String GUESTID_TABLE_NAME = "guestids";
    private static final String GUESTID_COLUMN_ID = "_id";
    private static final String GUESTID_COLUMN_DEVICEID = "deviceid";
    private static final String GUESTID_COLUMN_CAMERAID = "cameraid";
    private static final String GUESTID_COLUMN_DATETIME = "datetime";
    private static final String GUESTID_COLUMN_GUESTID = "guestid";
    private static final String GUESTID_COLUMN_PHOTOGRAPHER = "photographer";
    private static final String GUESTID_COLUMN_LOCATION = "location";
    private static final String GUESTID_COLUMN_SUBJECTS = "subjects";
    private static final String GUESTID_COLUMN_EVENTID = "eventid";
    private final String logFile = Environment.getExternalStorageDirectory() + "guestlink/guestLinkLog.txt";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + GUESTID_TABLE_NAME + "(" +
                    GUESTID_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    GUESTID_COLUMN_DEVICEID + " TEXT, " +
                    GUESTID_COLUMN_CAMERAID + " TEXT, " +
                    GUESTID_COLUMN_DATETIME + " TEXT, " +
                    GUESTID_COLUMN_GUESTID + " TEXT, " +
                    GUESTID_COLUMN_PHOTOGRAPHER + " TEXT, " +
                    GUESTID_COLUMN_LOCATION + " TEXT, " +
                    GUESTID_COLUMN_SUBJECTS + " TEXT, " +
                    GUESTID_COLUMN_EVENTID + " TEXT)"
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }

    //@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GUESTID_TABLE_NAME);
        onCreate(db);
    }
    public void createNew(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + GUESTID_TABLE_NAME);
            onCreate(db);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utilities.writeToLog(ex.toString(), logFile);
        }
    }
    public void insertRecord(String deviceid, String cameraid, String datetime, String guestid, String photographer, String location,
                                String subjects, String eventid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues record = new ContentValues();
            record.put(GUESTID_COLUMN_DEVICEID, deviceid);
            record.put(GUESTID_COLUMN_CAMERAID, cameraid);
            record.put(GUESTID_COLUMN_DATETIME, datetime);
            record.put(GUESTID_COLUMN_GUESTID, guestid);
            record.put(GUESTID_COLUMN_PHOTOGRAPHER, photographer);
            record.put(GUESTID_COLUMN_LOCATION, location);
            record.put(GUESTID_COLUMN_SUBJECTS, subjects);
            record.put(GUESTID_COLUMN_EVENTID, eventid);

            db.insert(GUESTID_TABLE_NAME, null, record);
        } catch (Exception e) {
            e.printStackTrace();
         }
    }

}


