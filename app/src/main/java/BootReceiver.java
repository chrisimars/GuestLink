import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import guestlink.kodakalaris.com.guestlink.GuestLinkMainActivity;

/**
 * Created by Donald Chapman1 on 6/15/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, GuestLinkMainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}
