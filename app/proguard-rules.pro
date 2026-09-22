# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Room database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep models and entities for Room and state
-keep class com.example.model.** { *; }
-keep class com.example.data.** { *; }

# App Widgets (system calls these via intent filter reflection)
-keep class com.example.widget.** extends android.appwidget.AppWidgetProvider { *; }

# Kotlin Coroutines internal dispatchers
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Broadcast Receivers (AlarmManager and Boot receivers called by Android OS)
-keep class com.example.reminder.** extends android.content.BroadcastReceiver { *; }

