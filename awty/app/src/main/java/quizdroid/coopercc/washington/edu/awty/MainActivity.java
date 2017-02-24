package quizdroid.coopercc.washington.edu.awty;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText message, phone, minutes;
    private Button start;
    private boolean isRunning;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


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

        //make button disabled until all editTexts have something in them
        //do something in bg

        isRunning = false;
        message = (EditText) findViewById(R.id.message);
        phone = (EditText) findViewById(R.id.phone);
        minutes = (EditText) findViewById(R.id.minutes);
        start = (Button) findViewById(R.id.start);

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
                            Intent intent = new Intent(MainActivity.this, Receiver.class);
                            intent.putExtra("Message", phone.getText() + ": " + message.getText());
                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                            mins = mins * 60 * 1000;
                            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                    SystemClock.elapsedRealtime() + mins, mins, pendingIntent);
                        }
                    } catch(NumberFormatException n) {
                        Toast.makeText(MainActivity.this, "Minutes must be a positive integer", Toast.LENGTH_LONG).show();
                    }



                } else {
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
}
