package guestlink.kodakalaris.com.guestlink;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Environment;


/**
 * Created by Donald Chapman1 on 6/30/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "GuestIDs.db";
    private static final int DATABASE_VERSION = 1;
    public static final String GUESTID_TABLE_NAME = "guestids";
    public static final String GUESTID_COLUMN_ID = "_id";
    public static final String GUESTID_COLUMN_DEVICEID = "deviceid";
    public static final String GUESTID_COLUMN_CAMERAID = "cameraid";
    public static final String GUESTID_COLUMN_DATETIME = "datetime";
    public static final String GUESTID_COLUMN_GUESTID = "guestid";
    public static final String GUESTID_COLUMN_PHOTOGRAPHER = "photographer";
    public static final String GUESTID_COLUMN_LOCATION = "location";
    public static final String GUESTID_COLUMN_SUBJECTS = "subjects";
    public static final String GUESTID_COLUMN_EVENTID = "eventid";
    private String logFile = logFile = Environment.getExternalStorageDirectory().getPath() + "/guestlink/guestLinkLog.txt";


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
    public boolean insertRecord(String deviceid, String cameraid, String datetime, String guestid, String photographer, String location,
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, GUESTID_TABLE_NAME);
        return numRows;
    }

    /*public boolean updatePerson(Integer id, String name, String gender, int age) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PERSON_COLUMN_NAME, name);
        contentValues.put(PERSON_COLUMN_GENDER, gender);
        contentValues.put(PERSON_COLUMN_AGE, age);
        db.update(PERSON_TABLE_NAME, contentValues, PERSON_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }*/

    public Integer deleteRecord(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(GUESTID_TABLE_NAME,
                GUESTID_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + GUESTID_TABLE_NAME + " WHERE " +
                GUESTID_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + GUESTID_TABLE_NAME, null );
        return res;
    }
}


