MozuAndroidInStoreAssistant
===========================
This is the  code repository for the Mozu Android Instore Sales Assistant.(https://play.google.com/store/apps/details?id=com.mozu.mozuandroidinstoreassistant.app)

- This application makes use of Mozu-Android-Sdk(https://github.com/Mozu/mozu-java/tree/master/mozu-android-sdk)  to authenticate and access Mozu platform. 
- Details about various Mozu API’s can be obtained at http://developer.mozu.com/api


Required Setup
--------------
<p> Add below mentioned parameters in 'gradle.properties' file located at ~/.gradle.<br>
<b>Mozu appId</b> and <b>Mozu shared secret</b> can be obtained from https://developer.mozu.com/Console/app by either creating a new or referencing an existing Application.
</p>

      ~/.gradle/gradle.properties
      
       # Release build parameters
       releaseStoreLocation=<keystore location-require for release build>
       releaseStorePassword=<keystorepassword-required for release build>
       releaseKeyAlias=<keystore Alias-required for release build>
       releaseKeyPassword=<keystore Password - required for release build>
      
       #Mozu Auth Parameters
       mozuAuthAppId=<Required - Mozu App id>
       mozuAuthSharedSecret=<Required - Mozu shared secret>
       mozuServiceURL=https://home.mozu.com
      
       #Crashlytics setting
       enableCrashlytics=<true|false - enable crashlytics support for app>
       crashlyticsApiKey=<crashlytics api key - required if enableCrashlytics is true>
       crashlyticsApiSecret=<crashlytics api secret - required if enableCrashlytics is true>

Theming:
--------

      Icon : drawable/ic_launcher
      color: colors_mozu.xml
      Styles are defined in styles_mozu.xml  with Theme : “Theme.Mozu”
      
External dependencies:
-----------------------------

      Mozu android sdk:
            compile 'com.mozu:mozu-api-android:1.13.0’ 

      Crashlytics:
            compile 'com.crashlytics.android:crashlytics:1.+’ 

      Picasso: 
            compile 'com.squareup.picasso:picasso:2.3.4’

      Butterknife:
             'com.jakewharton:butterknife:5.1.2’ 

      Java RX:
             compile 'com.netflix.rxjava:rxjava-android:0.17.5'

      TabPage indicator(https://github.com/JakeWharton/Android-ViewPagerIndicator/blob/master/library/src/com/viewpagerindicat       or/TabPageIndicator.java).which is compiled as aar and included in project.
             compile(name:'viewpager', ext:'aar')

