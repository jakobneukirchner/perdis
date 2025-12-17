# ProGuard rules for Perdis App

# Preserve Jsoup classes
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Preserve Kotlin
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Preserve Compose
-keep @androidx.compose.runtime.Stable class * { *; }
