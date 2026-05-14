# ══════════════════════════════════════════════════════════════════
# BloomCycle ProGuard / R8 Rules
# ══════════════════════════════════════════════════════════════════

# ── Debugging ─────────────────────────────────────────────────────
# Keep source file names and line numbers for crash stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Room ──────────────────────────────────────────────────────────
# Room entities, DAOs, and database classes are accessed via
# reflection and generated code — keep them intact.
-keep class com.bloomcycle.app.data.local.entity.** { *; }
-keep class com.bloomcycle.app.data.local.dao.** { *; }
-keep class com.bloomcycle.app.data.local.BloomCycleDatabase { *; }
-keep class com.bloomcycle.app.data.local.converter.Converters { *; }

# ── Domain enums ──────────────────────────────────────────────────
# These enums are stored as strings in Room via TypeConverters.
# R8 must not rename or strip their valueOf()/values() methods.
-keepclassmembers enum com.bloomcycle.app.domain.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ── Hilt ──────────────────────────────────────────────────────────
# Hilt's code generation handles most keep rules automatically.
# These entries cover edge cases with assisted injection and workers.
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ── WorkManager + HiltWorker ──────────────────────────────────────
-keep class com.bloomcycle.app.data.notification.ReminderWorker { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# ── Kotlin serialization & coroutines ─────────────────────────────
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keep class kotlin.Metadata { *; }
-dontwarn kotlinx.coroutines.**

# ── Compose ───────────────────────────────────────────────────────
# Compose compiler handles obfuscation correctly, but keep
# stability annotations for runtime introspection.
-keepattributes RuntimeVisibleAnnotations

# ── DataStore ─────────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# ── Biometric ─────────────────────────────────────────────────────
-keep class androidx.biometric.** { *; }

# ── General Android ───────────────────────────────────────────────
# Prevent stripping of Parcelable creators used by the framework.
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Keep R classes (needed for string resource references at runtime).
-keepclassmembers class **.R$* {
    public static <fields>;
}
