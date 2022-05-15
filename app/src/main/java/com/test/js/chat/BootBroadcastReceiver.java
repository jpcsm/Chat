package com.test.js.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lenovo on 2017-07-05.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("BootBroadcastReceiver", "BootBroadcastReceiver called : " + intent.getAction());

        /**
         * 소켓서비스 죽었을 때 알람으로 다시 서비스 등록
         */
        if (intent.getAction().equals("ACTION.RESTART.SocketService")) {

            Log.i("BootBroadcastReceiver", "ACTION.RESTART.SocketService ");

            Intent i = new Intent(context, SocketService.class);
            context.startService(i);
        }

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Log.i("BootBroadcastReceiver", "ACTION_BOOT_COMPLETED");
            Intent i = new Intent(context, SocketService.class);
            context.startService(i);
        }
    }
}
