# Prework: Computer Setup & Installation

### Operating Systems

This course supports Mac, Ubuntu and Windows users. All users must use the standard setup for their system, as described in the Code 201 & 301 prework. In particuluar, if you use Windows, make sure you have the Windows Subsystem for Linux installed & set up as described in [this guide](https://github.com/michaeltreat/Windows-Subsystem-For-Linux-Setup-Guide){:target="_blank"}.

### Command Line Setup

**For WSL users**, please ensure that you've set up the WSL as described in the above guide. Also, please run `sudo dpkg-reconfigure tzdata` to pick your current timezone in Ubuntu.

### Editors and IDEs

We'll use a variety of text editors and IDEs to build our projects throughout this course. We use **Visual Studio Code** for light text editing, **IntelliJ IDEA** for pure-Java programs and **Android Studio** when we're building Android applications. Additionally, you'll need to make sure **Java** itself is installed, as well as **Gradle**, a build system for Java applications.

Download and install the following on your base OS:

* [Visual Studio Code](https://code.visualstudio.com/){:target="_blank"}
* [IntelliJ IDEA (Community Edition)](https://www.jetbrains.com/idea/){:target="_blank"}
* [Android Studio](https://developer.android.com/studio/){:target="_blank"}
* [Java](https://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html#javasejdk){:target="_blank"}, version 8 if you get a choice.
  * WSL users: Also run these commands to install Java on Ubuntu.

   ```bash
   sudo apt-get update
   sudo apt-get install default-jdk
   ```

  * If you're using Linux (not WSL), some people have found [this guide](https://www.javahelps.com/2015/03/install-oracle-jdk-in-ubuntu.html){:target="_blank"} to be helpful.
* [Gradle](https://gradle.org/install/){:target="_blank"} should be automatically installed by Android Studio, but that version won't necessarily be on your PATH.
  * WSL users: install Gradle following the [manual installation instructions](https://gradle.org/install/#manually){:target="_blank"} for Windows. This will AUTOMATICALLY set Gradle up for use in your Ubuntu system as well.
  * Mac users: run `brew install gradle`.
  * Linux users: use the [manual installation instructions](https://gradle.org/install/#manually){:target="_blank"}.

## Submission

For this assignment, turn in a screenshot of your terminal, where you've run the following commands in order. (You can copy-paste this entire chunk into your terminal.) If any of the output is different from the comment at the end of the line, then something has gone wrong in your installation, and you should reach out to your instructor for help.

Also turn in a screenshot in which you've opened IntelliJ.

```bash
mkdir setup-verification
cd setup-verification
gradle init --type java-library # should run some stuff and eventually give a success message
ls src/main/java # should say setup/ or Library.java depending on Gradle version
./gradlew test # should say that all tests pass
javac -version # should say a version number
```
