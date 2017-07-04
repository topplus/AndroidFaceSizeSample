# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ssbai\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**

-keep class topplus.com.commonutils.util.*{
     native <methods>;
     public *;
     protected *;
}
-keep class topplus.com.facemeasure.*{
    *;
}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,Annotation,EnclosingMethod,MethodParameters

-keep class **.R$* {
*;
}

-keep interface threethird.it.sephiroth.android.library.exif2.ExifInterface$*{
    *;
}

-keep class threethird.it.sephiroth.android.library.exif2.ExifTag{
    public *;
}


-keep interface threethird.it.sephiroth.android.library.exif2.ExifInterface$*{
    *;
}