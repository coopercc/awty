package quizdroid.coopercc.washington.edu.awty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by coopercain on 2/23/17.
 */

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "Message Received");
        String message = intent.getStringExtra("Message");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
