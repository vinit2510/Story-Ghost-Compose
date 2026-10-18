# Preserve all data and response models for Gson serialization/deserialization
-keep class in.vinit.storyghost.data.model.** { *; }
-keepclassmembers class in.vinit.storyghost.data.model.** { *; }
-keep class in.vinit.storyghost.core.model.** { *; }
-keepclassmembers class in.vinit.storyghost.core.model.** { *; }
-keep class in.vinit.storyghost.config.AppConfig { *; }
-keepclassmembers class in.vinit.storyghost.config.AppConfig { *; }

# Retain fields annotated with @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Preserve Gson reflection on TypeToken for generic collections
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers class **$TypeAdapterFactory { <fields>; }

