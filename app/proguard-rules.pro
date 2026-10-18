# Preserve SourceFile and LineNumberTable for Crashlytics de-obfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve annotations and generic signatures
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Data Models & Serialization (Gson)
# Preserves all API response models and local storage DTOs from obfuscation/stripping
-keep class in.vinit.storyghost.data.model.** { *; }
-keepclassmembers class in.vinit.storyghost.data.model.** { *; }
-keep class in.vinit.storyghost.core.model.** { *; }
-keepclassmembers class in.vinit.storyghost.core.model.** { *; }
-keep class in.vinit.storyghost.config.AppConfig { *; }
-keepclassmembers class in.vinit.storyghost.config.AppConfig { *; }

# Retrofit API Interfaces
-keep interface in.vinit.storyghost.data.remote.**.*API { *; }
-keepclassmembers interface in.vinit.storyghost.data.remote.**.*API { *; }

# Custom Network Exceptions
-keep class in.vinit.storyghost.core.common.network.ApiException { *; }

# Koin Dependency Injection
-keep class in.vinit.storyghost.**.*ViewModel { *; }
-keepclassmembers class in.vinit.storyghost.**.*ViewModel { <init>(...); }