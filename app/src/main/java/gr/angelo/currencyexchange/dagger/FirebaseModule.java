package gr.angelo.currencyexchange.dagger;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gr.angelo.currencyexchange.utils.FirestoreHelper;

@Module
public class FirebaseModule {
    public final static String FIRESTORE = "firestore";
    public final static String AUTH = "auth";
    private FirestoreHelper mFirestore;
    private FirebaseAuth mAuth;

    public FirebaseModule() {
        mFirestore = new FirestoreHelper();
        mAuth = FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    @Named(FIRESTORE)
    FirestoreHelper getFirestore() {
        return mFirestore;
    }

    @Provides
    @Singleton
    @Named(AUTH)
    FirebaseAuth getAuth() {
        return mAuth;
    }
}
