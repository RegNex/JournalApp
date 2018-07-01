package co.etornam.journalapp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.etornam.journalapp.utils.FontOverride;
import io.fabric.sdk.android.Fabric;

import static co.etornam.journalapp.common.Constants.JOURNALS;

public class JournalApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(JOURNALS);
        usersRef.keepSynced(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FontOverride.setDefaultFont(JournalApp.this, "DEFAULT", "Comfortaa-Light.ttf");
                FontOverride.setDefaultFont(JournalApp.this, "MONOSPACE", "Comfortaa-Regular.ttf");
                FontOverride.setDefaultFont(JournalApp.this, "SERIF", "Comfortaa-Bold.ttf");
                FontOverride.setDefaultFont(JournalApp.this, "SANS_SERIF", "Comfortaa-Bold.ttf");
            }
        }).start();
    }
}
