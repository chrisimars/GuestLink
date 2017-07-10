package guestlink.kodakalaris.com.guestlink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class getSubjects extends AppCompatActivity {
    private final String logFile = Environment.getExternalStorageDirectory().getPath() + "/guestlink/guestLinkLog.txt";
    private  String subjects = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_get_subjects);


        String[] subjectNames = getSubjectsFromFile();

        ListView listView = (ListView) findViewById(R.id.listView);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), subjectNames);
        listView.setAdapter(customAdapter);



    }

    //Function to read the subjects text file into an ArrayList, then return a SString Array of the names
    private String[] getSubjectsFromFile() {
        try {
            String filename = (Environment.getExternalStorageDirectory().getCanonicalPath() + "/guestlink/subjects.txt");
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines.toArray(new String[lines.size()]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void cancelMe(View view){
        Utilities.showYesNoDialog(this, "Exit", "Are You Sure You Want to Exit without Saving Your Selections?", new DialogInterface.OnClickListener() {
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
   }

    public void saveMe(View view){
        final SharedPreferences sharedPreferences = this.getSharedPreferences("guestlink.kodakalaris.com.guestlink", Context.MODE_PRIVATE);
       try {
            Utilities.showYesNoDialog(this, "Save Settings", "Are You Sure You Want to Save Settings and Exit?", new DialogInterface.OnClickListener() {
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
}
