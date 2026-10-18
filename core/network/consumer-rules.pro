# Preserve Retrofit API Interfaces and custom exceptions
-keep interface in.vinit.storyghost.data.remote.**.*API { *; }
-keep class in.vinit.storyghost.core.common.network.ApiException { *; }

# Retain service method parameters when optimizing
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# With R8 full mode, prevent replacing interface proxy instances with null
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Retain Continuation for Kotlin coroutines suspend functions in Retrofit
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Retain return type signatures for suspend functions
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# Retain generic signatures on Retrofit Response wrapper
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Suppress harmless warnings for optional / tooling annotations
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

