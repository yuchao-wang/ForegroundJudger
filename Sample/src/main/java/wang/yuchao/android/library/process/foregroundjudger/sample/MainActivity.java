package wang.yuchao.android.library.process.foregroundjudger.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import wang.yuchao.android.library.process.foregroundjudger.ForegroundJudgerLibrary;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;

    private NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat
                .Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });
    }

    /**
     * 放在onStop是因为onStop也是界面真正消失的方法回调
     */
    @Override
    protected void onStop() {
        super.onStop();
        updateNotification();
    }

    private void updateNotification() {
        boolean[] result = new boolean[6];
        result[0] = ForegroundJudgerLibrary.isForegroundFromRunningTaskInfo(this, getPackageName());
        result[1] = ForegroundJudgerLibrary.isForegroundFromRunningAppProcessInfo(this, getPackageName());
        result[2] = ForegroundJudgerLibrary.isForegroundFromApplication();
        result[3] = ForegroundJudgerLibrary.isForegroundFromUsageState(this, getPackageName());
        result[4] = ForegroundJudgerLibrary.isForegroundFromAccessibilityService(this, getPackageName());
        result[5] = ForegroundJudgerLibrary.isForegroundFromLinuxInfo(this, getPackageName());

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            stringBuffer.append((i + 1) + "." + (result[i] ? "前" : "后"));
        }

        Toast.makeText(this, stringBuffer.toString(), Toast.LENGTH_LONG).show();

        builder.setContentTitle("测试")
                .setContentText(stringBuffer.toString());
        notificationManager.notify(1000, builder.build());
    }

}
