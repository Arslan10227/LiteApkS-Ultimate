# ProGuard rules for LiteapksClone
# Keep DTO and model classes
-keepclassmembers class com.arsla.liteapksclone.data.** { *; }
-keepclassmembers class com.arsla.liteapksclone.api.** { *; }

# Keep FileProvider path
-keep class androidx.core.content.FileProvider { *; }
