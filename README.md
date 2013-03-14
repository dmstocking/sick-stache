SickStache
=============

SickStache is an open source android app for managing your SickBeard server. It is also available on Google Play.

[![Google Play Icon](http://www.android.com/images/brand/android_app_on_play_logo_large.png)](http://play.google.com/store/apps/details?id=org.sickstache)

[Free apk download here.](https://sourceforge.net/projects/sickbeard/files/?source=navbar)

Features:
* Android Ice Cream Sandwich (4.0) look and feel
* Very Fast
* View Shows, Seasons, and Episodes
* View Future Episodes
* View History, and Logs
* Set Episode Status
* Search for Episode
* Add Shows
* Edit Shows
* Delete Shows
* Mass Show Edit via Context Action Bar
* Mass Episode Edit via Context Action Bar
* HTTPS Support

Example Screenshot:

![Example Screenshot](https://github.com/Buttink/sick-stache/wiki/Screenshots/sickstache-shows.png)

## Contributing

First, fork the SickStache repository. Downloaded the android support library version 10. Set the environmental
variable ANDROID_HOME to the android sdk location. Then in IntelliJ, go to open a current project. Find the file
pom.xml. It will be located in the top most folder of your repo. It will be with ChangeLog and README.md. After opening
the project, open the Module settings. There should be two ~apklib projects. Add a maven dependency of
com.google.android:support-v4:10 to both. Compile and Run to test your setup. After making your changes, request a pull.

## Contributors

* David Stocking - Creator
* johnou
