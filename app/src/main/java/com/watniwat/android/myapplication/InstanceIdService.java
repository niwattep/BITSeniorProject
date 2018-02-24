package com.watniwat.android.myapplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Niwat on 06-Feb-18.
 */

public class InstanceIdService extends FirebaseInstanceIdService {
	@Override
	public void onTokenRefresh() {
		super.onTokenRefresh();

		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
			dbRef.child(user.getUid()).child("fcmToken").setValue(refreshedToken);
		}
	}
}
