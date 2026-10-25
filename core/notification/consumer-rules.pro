# ProGuard rules for core:notification

# Preserve FirebaseMessagingService subclass entry point for Android runtime instantiation
-keep public class * extends com.google.firebase.messaging.FirebaseMessagingService {
    public <init>();
}
