# SpeechRecog-GC
Speech Recognition using Google Cloud Speech API
Build Environment:
Android Studio 3.1.3
Android Platform SDK 26
Gradle Build 4.4
Android SDK build Tools 27.0.3
Java JDK 1.8

Dependencies:

dependencies {
        //Google Cloud Speech 
    implementation 'com.google.api-client:google-api-client-android:1.22.0'
    implementation 'com.google.apis:google-api-services-speech:v1beta1-rev336-1.22.0'
    implementation 'com.google.apis:google-api-services-language:v1beta2-rev6-1.22.0'
    implementation 'com.google.code.findbugs:jsr305:2.0.1'
    
    implementation project(':commons-io-2.6')//IO File Library
    implementation 'info.hoang8f:fbutton:1.0.5'// Button design

    implementation 'com.android.support:design:26.1.0'//UI design
    implementation 'com.jaredrummler:material-spinner:1.2.5'//Spinner for language selection
}


Compile options:

compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8 //JDK 1.8
    targetCompatibility JavaVersion.VERSION_1_8 //JDK 1.8
}

Gradle Configuration:

compileSdkVersion 26
defaultConfig {
    applicationId "com.example.sk.exp"
    minSdkVersion 23
    targetSdkVersion 26
    versionCode 1
    versionName "1.0"
    testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"

Material spinner Library:

Info: https://github.com/jaredrummler/MaterialSpinner
Import: 
app:build:gradle -> dependencies 

implementation 'com.jaredrummler:material-spinner:1.2.5'

Importing Commons IO Library:

Download Link: https://commons.apache.org/proper/commons-io/
Import:
Extract the binary -> locate commons-io-2.6.jar
Add dependency in app:build:gradle :
implementation project(':commons-io-2.6') 

Code:

Declare the API KEY to a String in MainActivity.java
private final String CLOUD_API_KEY = "API KEY HERE";

The AudRec.java class generates audio.wav for the recording and replaces this file if new audio is recorded, this behaviour can be changed in getFilename() in AudRec.java.

The File Format used is wav with PCM encoding.

Other formats can be used with encoding specifications as mentioned in the link below:
https://cloud.google.com/speech-to-text/docs/encoding
