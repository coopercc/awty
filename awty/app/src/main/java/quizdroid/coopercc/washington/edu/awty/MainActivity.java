package quizdroid.coopercc.washington.edu.awty;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class MainActivity extends Activity {

    private EditText message, phone, minutes;
    private Button start;
    private boolean isRunning;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;


    private TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        //make button disabled until all editTexts have something in them
        //do something in bg

        isRunning = false;
        message = (EditText) findViewById(R.id.message);
        phone = (EditText) findViewById(R.id.phone);
        minutes = (EditText) findViewById(R.id.minutes);
        start = (Button) findViewById(R.id.start);
        start.setEnabled(false);

        message.addTextChangedListener(tw);
        phone.addTextChangedListener(tw);
        minutes.addTextChangedListener(tw);

        alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    try {
                        int mins = Integer.parseInt(minutes.getText().toString());
                        if (mins <= 0) {
                            Toast.makeText(MainActivity.this, "Minutes must be positive", Toast.LENGTH_LONG).show();
                        } else {

                            isRunning = true;
                            start.setText("Stop");
                            Log.i("MainActivity", String.valueOf(isRunning));
                            Log.i("MainActivity", phone.getText() + ": " + message.getText());


                            Intent intent = new Intent(MainActivity.this, Receiver.class);
                            intent.putExtra("Phone", phone.getText().toString());
                            intent.putExtra("Message", message.getText().toString());
                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            mins = mins * 60 * 1000;
                            Log.i("MainActivity",String.valueOf(SystemClock.elapsedRealtime() + mins));
                            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                    SystemClock.elapsedRealtime() + mins, mins, pendingIntent);
                        }
                    } catch(NumberFormatException n) {
                        Toast.makeText(MainActivity.this, "Minutes must be a positive integer", Toast.LENGTH_LONG).show();
                    }



                } else {
                    Log.i("MainActivity", "Ended current message");
                    start.setText("Start");
                    isRunning = false;
                    alarmManager.cancel(pendingIntent);
                }

            }
        });

    }

    private void checkFieldsForEmptyValues(){
        String s1 = message.getText().toString();
        String s2 = phone.getText().toString();
        String s3 = minutes.getText().toString();
        Log.i("MainActivity", s1 + "  / " + s2 + "  / " + s3);

        if (s1.equals("") || s2.equals("") || s3.equals("")) {
            start.setEnabled(false);
        } else {
            start.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    return;
                }
            }
        }
    }

}
