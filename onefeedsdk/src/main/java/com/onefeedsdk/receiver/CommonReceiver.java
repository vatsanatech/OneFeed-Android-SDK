package com.onefeedsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.job.PostUserTrackingJob;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 28-July-2018
 * Time: 18:03
 */
public class CommonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, intent, context);
        asyncTask.execute();
    }

    private static class Task extends AsyncTask<String, String, String> {

        private static final String TAG = "MyBroadcastReceiver";
        private final PendingResult pendingResult;
        private final Intent intent;
        private final Context context;

        private Task(PendingResult pendingResult, Intent intent, Context context) {
            this.pendingResult = pendingResult;
            this.intent = intent;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            /*StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);*/

            // User activity tracking added by yogesh 08-12-2018
            checkReceiver(intent, context);
            return "";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }

        private void checkReceiver(Intent intent, Context context) {
            if (intent.getAction() == Intent.ACTION_POWER_CONNECTED) {
                OneFeedSdk.getInstance().getJobManager()
                        .addJobInBackground(new PostUserTrackingJob(Constant.PLUG_IN, Constant.RSRC));

            } else if (intent.getAction() == Intent.ACTION_POWER_DISCONNECTED) {
                OneFeedSdk.getInstance().getJobManager()
                        .addJobInBackground(new PostUserTrackingJob(Constant.PLUG_OUT, Constant.RSRC));

            } else if (intent.getAction() == Intent.ACTION_HEADSET_PLUG) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        //OneFeedSdk.getInstance().getJobManager()
                        //        .addJobInBackground(new PostUserTrackingJob(Constant.HEAD_SET_OUT, Constant.RSRC));
                        break;
                    case 1:
                        OneFeedSdk.getInstance().getJobManager()
                                .addJobInBackground(new PostUserTrackingJob(Constant.HEAD_SET_IN, Constant.RSRC));
                        break;
                    default:
                        Toast.makeText(context, "Head Set Unknown", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {

            } else if (intent.getAction() == Intent.ACTION_SCREEN_ON) {

            } else if (intent.getAction() == Intent.ACTION_USER_PRESENT) {
                OneFeedSdk.getInstance().getJobManager()
                        .addJobInBackground(new PostUserTrackingJob(Constant.SCREEN_UNLOCK, Constant.RSRC));

            } else if (intent.getAction() == Intent.ACTION_USER_UNLOCKED) {
                Log.e("USER_UNLOCKED", "USER_UNLOCKED");
            }
        }
    }

}