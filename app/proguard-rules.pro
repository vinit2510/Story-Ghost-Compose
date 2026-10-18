# Preserve SourceFile and LineNumberTable for Crashlytics de-obfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve annotations and generic signatures
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Koin Dependency Injection - keep ViewModel constructors for reflection/injection
# Note: ViewModel class names and internal methods are safely obfuscated; only constructor signatures are retained
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Navigation 3 & Kotlinx Serialization - preserve serializers and companion objects for NavKey route serialization
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * extends kotlinx.serialization.internal.GeneratedSerializer {
    <init>(...);
}
-keep class in.vinit.storyghost.ui.navigation.Route** { *; }
-dontnote kotlinx.serialization.SerializationKt

# Suppress harmless warnings for optional / tooling annotations
-dontwarn javax.annotation.**
-dontwarn org.checkerframework.**
-dontwarn com.google.errorprone.annotations.**