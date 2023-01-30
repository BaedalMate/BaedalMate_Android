# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Common
-dontnote android.net.http.*
-dontnote org.apache.http.**

# KAKAO
-keep class com.kakao.sdk.**.model.* { *; }
-keep class * extends com.google.gson.TypeAdapter

# ==================================
# Sentry
# ==================================
-keepattributes SourceFile, LineNumberTable, Annotation
-dontwarn org.slf4j.**
-dontwarn javax.**
-keep class io.sentry.**.* { *; }

# GooglePlay Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.common.** { *; }
-dontwarn com.google.common.**

# Firebase
    # This rule will properly ProGuard all the model classes in
    # the package com.yourcompany.models.
    # Modify this rule to fit the structure of your app.
    -keep public class com.google.firebase.analytics.FirebaseAnalytics {
         public *;
     }
## Crashlytics
# In order to provide the most meaningful crash reports
-keepattributes SourceFile,LineNumberTable
# If you're using custom Eception
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**
## Crash report
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# KAKAO MAP
-keep class net.daum.mf.map.n.** { *; }
-keep class net.daum.mf.map.api.MapView { *; }
-keep class net.daum.android.map.location.MapViewLocationManager { *; }
-keep class net.daum.mf.map.api.MapPolyline { *; }
-keep class net.daum.mf.map.api.MapPoint { *; }

# DAUM/카카오 관련 모두 제외처리
-keep class net.daum.** {*;}
-keep class com.kakao.** { *; }
-keep class android.opengl.** {*;}
-keep class com.kakao.util.maps.helper.** {*;}
-keepattributes Signature
-keepclassmembers class * {
    public static *;
    public *;
}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Lottie
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** {*;}

# databinding
-keep class android.databinding.** { *; }
-dontwarn android.databinding.**

# Navigation
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable
-keep class * extends androidx.fragment.app.Fragment{}
-keep class androidx.lifecycle.** { *; }