
#-------------------------------------------基本不用动区域----------------------------------------
#忽略警告 不忽略可能打包不成功
-ignorewarnings
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
#字典
-obfuscationdictionary musdic.txt
-classobfuscationdictionary musdic.txt
-packageobfuscationdictionary musdic.txt
-dontskipnonpubliclibraryclassmembers
#-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-ignorewarnings
# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#androidx包使用混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

#----------------------------------------------------------------------------
#---------------------------------webview (项目中没有使用到webView忽略即可)-----------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------------------------------------

#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------
#bean的包路径（如下：）
-keep class com.extraordinary.weaves.tendency.bean.** { *; }
-keep class com.extraordinary.interstings.studios.beans.** { *; }

#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------
#一些依赖库的混淆（如：gson）
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
##---------------End: proguard configuration for Gson  ----------

# Gson reflects on local push config models and generic Map fields.
-keep class com.extraordinary.weaves.tendency.notification.localpush.LocalPushConfig$* { *; }
#-------------------------------------------------------------------------
#okhttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.**{*;}

-keep class okio.**{*;}

-keep class android.net.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.**{*;}

-keep class com.qiniu.android.**{*;}

-keep class android.support.annotation.**{*;}

-keep class com.squareup.wire.**{*;}

-keep class com.ant.liao.**{*;}

-keep class com.nostra13.universalimageloader.**{*;}




# Called by introspection
-keep class
    com.google.android.gms.**,
    com.google.ads.**
    extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}


# This keeps the class name as well as the creator field, because the
# "safe parcelable" can require them during unmarshalling.
-keepnames class
    com.google.android.gms.**,
    com.google.ads.**
    implements android.os.Parcelable {
    public static final ** CREATOR;
}


# eventbus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# If using AsyncExecutord, keep required constructor of default event used.
# Adjust the class name if a custom failure event type is used.
-keepclassmembers class org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Accessed via reflection, avoid renaming or removal
-keep class org.greenrobot.eventbus.android.AndroidComponentsImpl


# 保持注解不被混淆
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}
# 保持异常不被混淆
-keepattributes Exceptions

-keep class dora.cache.** {
    *;
}
-dontwarn dora.cache.**

-keep class dora.db.builder.Condition {
    public *;
}
-keep class dora.db.builder.WhereBuilder {
    public *;
}
-keep class dora.db.builder.QueryBuilder {
    public *;
}
-keep class dora.db.constraint.AssignType {
    public *;
}
-keep class dora.db.converter.** {
    *;
}
-keep interface dora.db.dao.Dao {
    public *;
}
-keep class dora.db.dao.OrmDao {
    public *;
}
-keep class dora.db.dao.DaoFactory {
    public *;
}
-keep interface dora.db.table.OrmTable {
    public *;
}
-keep class dora.db.table.PrimaryKeyEntry {
    *;
}
-keep class dora.db.table.PrimaryKeyId {
    *;
}
-keep class dora.db.table.TableManager {
    *;
}
-keep class dora.db.Orm {
    public *;
}
-keep class dora.db.OrmConfig {
    public *;
}
-keep class dora.db.OrmConfig$Builder {
    public *;
}
-keep class dora.db.Transaction {
    public *;
}
-dontwarn dora.db.**

-keep class dora.http.log.FormatLogInterceptor {
    public *;
}
-keep class dora.http.retrofit.** {
    *;
}
-keep class dora.http.DoraCallback {
    *;
}
-keep class dora.http.DoraListCallback {
    *;
}
-keep class dora.http.DoraHttp {
    public *;
}
-dontwarn dora.http.**


# appsflyer

-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }
-keep public class com.android.installreferrer.** { *; }



-keep class com.bytedance.sdk.** { *; }