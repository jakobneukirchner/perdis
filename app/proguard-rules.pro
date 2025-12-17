-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
