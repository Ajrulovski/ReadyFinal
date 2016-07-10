package gazmend.com.mk.ready;

/**
 * Created by Gazmend on 7/4/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import gazmend.com.mk.ready.models.BusinessUser;
import gazmend.com.mk.ready.models.OrderObject;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    DatabaseReference mDatabase;
    String username;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // keep the current token in sharedprefs
        SharedPreferences prefs = getSharedPreferences("gazmend.com.mk.ready", Context.MODE_PRIVATE);
        username = prefs.getString("username",null);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("firebasetoken", refreshedToken);
        editor.commit();

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken,username);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token, final String username) {
        // here save the token as a part of the firebase user model
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(username != null) {
            // so first check if there is such auser in the db
            mDatabase.child("businessUsers").child(username).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value

                            BusinessUser bu = dataSnapshot.getValue(BusinessUser.class);
                            if (bu == null) {
                                Log.e("NO_ORDERS_FOUND", "NO_ORDERS_FOUND");
                                //Toast.makeText(, "No user found.", Toast.LENGTH_LONG).show();
                            } else {
                                // here update the token value in firebase
                                bu.setFirebasetoken(token);
                                Map<String, Object> businessUserValues = bu.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/businessUsers/" + username, businessUserValues);
                                //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                                mDatabase.updateChildren(childUpdates);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("CANCELED_ORDER", "getOrder:onCancelled", databaseError.toException());
                        }
                    });
        }
    }
}
