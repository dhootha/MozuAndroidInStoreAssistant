<h1>MozuAndroidInStoreAssistant </h1>

<p>This is the code repository for the Mozu Android In-Store Sales Assistant app that enables In-Store sales associates to directly interact with the business’ Mozu account, inventory, orders, and customer records. The Android app is open-source for extending and customizing for use by your sales staff, supporting company branding through styles and API access.</p>
<p>The downloadable Android app can be found via Google Play: https://play.google.com/store/apps/details?id=com.mozu.mozuandroidinstoreassistant.app </p>
<p>This Mozu app makes use of Mozu-Android-Sdk (https://github.com/Mozu/mozu-java/tree/master/mozu-android-sdk) to authenticate and access the Mozu platform directly. </p>
<p>For details on the Mozu APIs, please visit our Mozu Dev Center (http://developer.mozu.com/api).</p>

- This code repository is for Public consumption. The intent is to clone or download the code to customize and extend into an app for your specific business.
- The app can be provided back to your staff through Google Play. 
- All required code for the app is included in the GitHub repository (https://github.com/Mozu/MozuAndroidInStoreAssistant). Dependencies for compiling updated code are included in this document.


<h2>Setup and Configuration</h2>

<p>These steps are required when working within the app source code. The original source code downloaded from GitHub includes all required dependencies and code for the app. Once downloaded, you will need to edit a specific file with parameters obtained from Mozu. Additional third-party software or libraries are required for the app.</p>
<p>Requirements:</p>
- You must have a Mozu Dev Center developer account to upload your app to Mozu.
- You must add a set of parameters to the gradle.properties file located at ~/.gradle.
- You can get a Mozu appId and Mozu shared secret through the Dev Center Console (https://developer.mozu.com/Console/app) by either creating a new or referencing an existing Application.

<h3>Getting Mozu App Properties</h3>
<p>If you do not have a developer account in Mozu Dev Center, contact your Mozu administrators to create an account. When creating a new app or referencing an existing app in the console, you can obtain the Mozu Auth Parameters. </p>
- Mozu Auth Parameters – Required for the app.
- Release Build Parameters – Required for the release build and must be updated prior to release. For a debugging build, the release build parameters do not be updated.
<p>The build properties digitally sign the certificate for Android apps, required for installation. Android uses this certificate to identify you as the author. For details, see Android’s policies on digital signing (http://developer.android.com/tools/publishing/app-signing.html).</p>
<p>You will add this data to the gradle.properties for your app. </p>

<h3>Adding Properties to gradle.properties</h3>
<p>Add the following parameters to gradle.properties:</p>
      ~/.gradle/gradle.properties
      
       # Release build parameters
       releaseStoreLocation=<keystore location - Required for release build>
       releaseStorePassword=<keystorepassword - Required for release build>
       releaseKeyAlias=<keystore Alias - Required for release build>
       releaseKeyPassword=<keystore Password - Required for release build>
      
       #Mozu Auth Parameters
       mozuAuthAppId=<Required - Mozu App id>
       mozuAuthSharedSecret=<Required - Mozu shared secret>
       mozuServiceURL=https://home.mozu.com
      
       #Crashlytics setting
       enableCrashlytics=<true|false - Enable crashlytics support for app>
       crashlyticsApiKey=<crashlytics api key - Required if enableCrashlytics is true>
       crashlyticsApiSecret=<crashlytics api secret - Required if enableCrashlytics is true>

<h2>Theming</h2>

<p>You can modify the styling and theme for the app through the following files and file locations:</p>
- The app uses a specific Theme: “Theme.Mozu”.
- All styles are configured in: styles_mozu.xml. 
- The app Icon is located at: drawable/ic_launcher.
- All colors are configured in: colors_mozu.xml.
      
<h2>External dependencies</h2>

<p>The downloaded code for the Mozu Android In-Store Sales Assistant app includes a set of external dependency applications listed below. The list of applications is for informational purposes and does not require additional steps taken.</p>
<p>Mozu Android SDK:</p>
      compile 'com.mozu:mozu-api-android:1.13.0’ 

<p>Crashlytics:</p>
      compile 'com.crashlytics.android:crashlytics:1.+’ 

<p>Picasso: </p>
      compile 'com.squareup.picasso:picasso:2.3.4’

<p>Butterknife:</p>
      compile 'com.jakewharton:butterknife:5.1.2’ 

<p>Java RX:</p>
      compile 'com.netflix.rxjava:rxjava-android:0.17.5'

<p>TabPage indicator(https://github.com/JakeWharton/Android-ViewPagerIndicator/blob/master/library/src/com/viewpagerindicator/TabPageIndicator.java) compiled as an aar file and included in the project:</p>
      compile(name:'viewpager', ext:'aar')

<h2>Enrolling and Distributing the App</h2>
<p>When the app customization and extension is complete, you can enroll the app to Mozu. You can add the app directly through the Mozu Dev Center Console, registering the app for use in Mozu. </p>
<p>When complete, you can distribute the app through Google Play store. You must include the release parameters to generate a release build. Sales associates in your company can download, install, and use the app to interact with Mozu customer records, orders, and inventory. </p>
