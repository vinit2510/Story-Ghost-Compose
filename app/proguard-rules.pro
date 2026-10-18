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