# EECS4443-Demos

This repository contains a collection of Android application projects for the EECS4443 course. Each project is contained within its own folder. Students are encouraged to download the repository, open the individual project in Android Studio, and run it.

The Javadoc for each of the demos can be accessed here: [https://yorku-ease.github.io/EECS4443-Demos/](https://yorku-ease.github.io/EECS4443-Demos/).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

The projects in this repository require:

- Gradle 7.4.2
- Java 11 or higher
- Android SDK 33

Please ensure you have these prerequisites installed before attempting to run the projects.

> [!WARNING]  
> The latest versions of Android Studio, like Hedgehog, may prompt you to upgrade the AGP to 8.2.1 - you do not need to do this as 7.4.2 is still supported.
> If you get the prompt `Android Gradle plugin version 7.4.2 has an upgrade available.` you can simply ignore it. Note that upgrading will require using Java 17.

### Installing

1. Clone the repository: `git clone https://github.com/yorku-ease/EECS4443-Demos.git`
2. Open the desired project folder in Android Studio (e.g. Demo_Android).
3. Run the project by clicking the green `run` button or the `^R` shortcut.

## Project Structure

Each project is contained within its own folder. The folder name corresponds to the project name.

## Dependencies

Dependencies may vary between projects. Please refer to the specific project's `build.gradle` file for a detailed list of dependencies.

## Troubleshooting

If you encounter any issues while trying to run the projects, here are some common problems and their solutions:

<details>
<summary>Common Issues</summary>
<br>

### Android Studio not recognizing the JDK
Error might be `Could not resolve all files for configuration ':classpath'`. Follow these steps to set the JDK:

#### In Android Studio Hedgehog

1. Go to `Android Studio` > `Settings` > `Build, Execution, Deployment` > `Build Tools` > `Gradle`.
2. In the `Gradle Projects` section set the `Distribution` to `wrapper` and `Gradle JDK` to the path to your JDK installation.

#### In older versions

1. Go to `File` > `Project Structure` > `SDK Location`.
2. In the `JDK Location` field, set the path to your JDK installation. This will set it for the current project.

### Android Studio not recognizing the Android SDK
Ensure that you have the correct Android SDK version installed. You can check this in Android Studio by going to `File` > `Settings` > `Appearance & Behavior` > `System Settings` > `Android SDK`.

### Gradle sync failed
This can happen if there are issues with your Gradle setup. Try invalidating caches and restarting Android Studio (`File` > `Invalidate Caches / Restart`). Also, ensure that you're using a compatible version of Gradle.

### Project not building
Check the `build.gradle` file for any errors. Also, try cleaning the project (`Build` -> `Clean Project`) and rebuilding it (`Build` -> `Rebuild Project`).

</details>

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

We appreciate your interest in this repository. We hope you find these demos helpful for your studies.
