MozuAndroidInStoreAssistant
===========================
Mozu Android Instore Sales Assitant Version 1.0.1

This is the repository for the Mozu Android app.

Required Setup
--------------
<p> Add below mentioned parameters in 'gradle.properties' file  </p>

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
