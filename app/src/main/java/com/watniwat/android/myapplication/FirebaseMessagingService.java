package com.watniwat.android.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.RemoteMessage;
import com.watniwat.android.myapplication.Activity.ChatActivity;
import com.watniwat.android.myapplication.Activity.RoomListActivity;

import java.util.Map;

import static com.watniwat.android.myapplication.Activity.RoomListActivity.EXTRA_ROOM_NAME;

/**
 * Created by Niwat on 06-Feb-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

	private static final String EXTRA_ROOM_UID = "extra-room-uid";
	private static final String EXTRA_ROOM_NAME = "extra-room-name";

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		Map<String, String> data = remoteMessage.getData();

		Log.d(Constant.LOG_TAG, data.toString());
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			if (data.get("senderUId") != null &&
					!data.get("senderUId").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
				sendNotification(data);
			}
		}
	}

	private void sendNotification(Map<String, String> data) {
		String senderName = data.get("senderName");
		String content = data.get("content");
		String roomName = data.get("roomName");
		String roomUId = data.get("roomUId");
		String type = data.get("type");

		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(EXTRA_ROOM_UID, roomUId);
		intent.putExtra(EXTRA_ROOM_NAME, roomName);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

		long[] vibrate = {0, 500, 500, 500};

		String contentText = type.equals("text") ? "@" + senderName + " says: " + content : "@" + senderName + " " + content;

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
				.setContentTitle(roomName)
				.setContentText(contentText)
				.setAutoCancel(true)
				.setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.unconvinced))
				.setVibrate(vibrate)
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(icon)
				.setPriority(NotificationCompat.PRIORITY_HIGH);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, notificationBuilder.build());
	}
}
