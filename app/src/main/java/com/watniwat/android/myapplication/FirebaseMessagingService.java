package com.watniwat.android.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.RemoteMessage;
import com.watniwat.android.myapplication.Activity.ChatActivity;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Niwat on 06-Feb-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		RemoteMessage.Notification notification = remoteMessage.getNotification();
		Map<String, String> data = remoteMessage.getData();

		if (!data.get("userUId").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
			sendNotification(notification);
		}
	}

	private void sendNotification(RemoteMessage.Notification notification) {
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

		Intent intent = new Intent(this, ChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
				.setContentTitle(notification.getTitle())
				.setContentText(notification.getBody())
				.setAutoCancel(true)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentIntent(pendingIntent)
				.setContentInfo(notification.getTitle())
				.setLargeIcon(icon)
				.setColor(Color.RED)
				.setLights(Color.RED, 1000, 300)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setStyle(new NotificationCompat.InboxStyle());

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					"channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
			);
			channel.setDescription("channel description");
			channel.setShowBadge(true);
			channel.canShowBadge();
			channel.enableLights(true);
			channel.setLightColor(Color.RED);
			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0, notificationBuilder.build());
	}
}
