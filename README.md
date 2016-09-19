# sample android app for BLE level-gauge

# Document : https://goo.gl/FjH8SB

This is about "Level Gauge"'s sample source.

# Build
dependencies {
    compile "com.android.support:support-v4:24.2.0"
    compile "com.android.support:support-v13:24.2.0"
    compile "com.android.support:cardview-v7:24.2.0"
    compile "com.android.support:appcompat-v7:24.2.0"
}
...
android {
    compileSdkVersion 24
    buildToolsVersion "24.0.2"
    
    defaultConfig {
        minSdkVersion 18
        targetSdkVersion 24
    }
    ...
}
