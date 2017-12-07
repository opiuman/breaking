package go.opiuman.breaking;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by wilson on 11/26/17.
 */

public class NotificationForwarderService extends NotificationListenerService {

    private static final String TAG = "nfservice";
    private Set<String> packageNames;
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();


    public NotificationForwarderService() {
        packageNames = setPackageNames();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        String packageName = sbn.getPackageName();
        if (packageNames.contains(packageName)) {
            Notification notification = sbn.getNotification();
            BreakingNews bkn = new BreakingNews(packageName,notification.extras.getString("android.title"),notification.extras.getString("android.text") );
            updateMainActivity(bkn);
            postBreakingNews(bkn, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.v(TAG, response.body().string());
                }
            });
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
    }

    private void updateMainActivity(BreakingNews bkn) {
        Intent intent = new  Intent("go.opiuman.notificationforwarder");
        intent.putExtra("title", bkn.getTitle());
        intent.putExtra("content", bkn.getContent());
        sendBroadcast(intent);
    }


    private Call postBreakingNews(BreakingNews bkn, Callback callback) {
        Gson gson = new GsonBuilder().create();
        RequestBody body = RequestBody.create(JSON, gson.toJson(bkn));
        Request request = new Request.Builder()
                .url(getString(R.string.stabot_endpoint))
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    private Set<String> setPackageNames() {
        Set<String> packageNames = new HashSet<>();
//        packageNames.add("com.zumobi.msnbc.beta");
//        packageNames.add("bbc.mobile.news.ww");
//        packageNames.add("com.foxnews.android");
//        packageNames.add("ca.cbc.mobile.android.cbcnewsandroidwebview");
//        packageNames.add("mnn.Android");  //ap news
//        packageNames.add("com.nbcnews.today");
        packageNames.add("com.huffingtonpost.android");
        packageNames.add(("com.treemolabs.apps.cbsnews"));
        packageNames.add("com.usatoday.android.news");
        packageNames.add("com.bloomberg.android.plus");
        packageNames.add("com.guardian");
        packageNames.add("com.abc.abcnews");
        packageNames.add("com.rt.mobile.english");
        packageNames.add("com.app.p2371GE");  //pbs newshour
        packageNames.add("uk.co.economist");
        packageNames.add("net.aljazeera.english");
        packageNames.add("com.freerange360.mpp.businessinsider");
        packageNames.add("com.ft.news");
        packageNames.add("com.vice.news");
        packageNames.add("com.bskyb.skynews.android");
        packageNames.add("com.mirror.news");
        packageNames.add("com.yahoo.mobile.client.android.yahoo");
        packageNames.add("com.cnbc.client");
        packageNames.add("com.euronews.express");
        packageNames.add("com.buzzfeed.news");
        packageNames.add("com.dailymail.online");
        packageNames.add("com.thomsonreuters.reuters");
        packageNames.add("com.nytimes.android");
        return packageNames;
    }
}
