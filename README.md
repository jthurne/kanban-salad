Kanban Data Tracker
===================

Kanban Data Tracker will make tracking data on a kanban board (and maybe other types of process visualization boards) easy.

The project is currently in the inception phase.  So far, we've built a prototype android application which can read and program NFC tags on a Kanban board.

Building the Android Client
===========================
To build the Android Client with Maven you need first need to:

1. Install Oracle JDK 1.6+ as required for Android development
2. Install Android SDK (r17) preferably with all platforms, see http://developer.android.com/sdk/index.html
3. Install Maven 3.0.3+, see http://maven.apache.org/download.html
4. Set environment variable ANDROID\_HOME to the path of your installed Android SDK and add $ANDROID\_HOME/tools as well as $ANDROID\_HOME/platform-tools to your $PATH. (or on Windows %ANDROID\_HOME%\tools and %ANDROID\_HOME%\platform-tools).
   MacOS users: Note that for the path to work on the commandline and in IDE's started by launchd you have to set it in /etc/launchd.conf and NOT in .bashrc or something else

Then you can:

    mvn clean package
    mvn android:deploy
    mvn android:run

License
=======
Copyright 2013 Jim Hurne and Joseph Kramer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



