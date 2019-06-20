package gr.angelo.currencyexchange.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import gr.angelo.currencyexchange.BottomNavigation;

public class FirestoreHelper {
    private final static String COLLECTION_NAME = "preferences";
    private final static String TAG = FirestoreHelper.class.getCanonicalName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirestoreHelper() {
    }

    public void savePreferences(FirebaseUser user, Map<String, Object> preferences) {
        db.collection(COLLECTION_NAME).document(user.getUid())
                .set(preferences)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    public void getPreferences(Activity activity, FirebaseUser user, SharedPreferences sharedPref) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> preferences = document.getData();
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("base", (String) preferences.get("base"));
                    editor.putString("chartBase", (String) preferences.get("chartBase"));
                    editor.putString("chartTo", (String) preferences.get("chartTo"));
                    editor.apply();
                    activity.finish();
                    activity.startActivity(new Intent(activity, BottomNavigation.class));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public Map<String, Object> localPrefToMap(SharedPreferences sharedPref) {
        String base = sharedPref.getString("base", "EUR");
        String chartBase = sharedPref.getString("chartBase", "EUR");
        String chartTo = sharedPref.getString("chartTo", "USD");
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("base", base);
        preferences.put("chartBase", chartBase);
        preferences.put("chartTo", chartTo);
        return preferences;
    }
}
