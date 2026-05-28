# Bali Travel Health

**A comprehensive travel health companion for visitors to Bali.**

Bali Travel Health helps travellers stay safe before, during, and after their trip with health risk assessments, emergency guides, healthcare facility discovery, nurse booking, vaccine records, and personalised health advice.

---

## Features

| Category | Feature |
|---|---|
| **Pre-Travel** | Health risk assessment, vaccine record, travel schedule, personalised preparation advice |
| **During Travel** | Healthcare facility finder, nearby facility information, emergency guides, Basic Life Support guidance |
| **Post-Travel** | Post-travel health screening and follow-up advice |
| **Nursing Care** | Browse nurses in Bali, book appointments, view nursing records |
| **Offline Support** | Local Room database cache for facilities, nurses, guides, and life support content |
| **Account & Profile** | Google Sign-In, traveller profile, health profile, local preference storage |
| **Localisation** | English, Indonesian, Arabic, Chinese, Dutch, French, German, Hindi, Italian, Japanese, Korean, Russian, and Spanish |

---

## Screenshots

![Bali Travel Health App](https://balihealth.sgp1.cdn.digitaloceanspaces.com/Bali%20Travel%20Health%20App.png)

---

## Requirements

| Requirement | Value |
|---|---|
| Android | Android 14 / API 34 or later |
| Device | Android phone or tablet with Google Play services |
| Android Studio | Latest stable Android Studio recommended |
| Android SDK | Compile SDK 35 |
| Kotlin | 2.1.0 |
| Gradle | Wrapper included, Gradle 9.4.1 |

---

## Installation - APK Outside Google Play Store

Bali Travel Health is not available on the Google Play Store yet. You can install it manually by sideloading the APK from GitHub Releases.

> **Important:** Only install APK files from this repository's official [Releases](../../releases/latest) page.

### Step 1 - Download the APK

1. Open the [latest release](../../releases/latest).
2. Download the newest `BaliTravelHealth.apk` file.
3. Keep the APK on your Android device, or transfer it from your computer using USB, Google Drive, WhatsApp, or another trusted method.

### Step 2 - Allow installation from unknown sources

Android blocks apps installed outside the Play Store by default. Enable permission only for the app you use to open the APK.

1. Open the downloaded APK from **Files**, **Chrome**, **Drive**, or your file manager.
2. If Android shows a security prompt, tap **Settings**.
3. Enable **Allow from this source**.
4. Go back to the APK installer screen.

You can also find the setting manually:

**Settings -> Apps -> Special app access -> Install unknown apps -> choose your browser/file manager -> Allow from this source**

### Step 3 - Install

1. Tap the `BaliTravelHealth.apk` file.
2. Tap **Install**.
3. Wait until installation finishes.
4. Tap **Open**, or launch **Bali Travel Health** from your app drawer.

### Step 4 - Sign in

Open **Bali Travel Health** and sign in with Google to access your profile, assessments, vaccine records, nursing appointments, and synced health content.

### Updating the app

1. Download the newer APK from [Releases](../../releases/latest).
2. Open the APK and tap **Update**.
3. Install it over the existing app.

Your local data should remain on the device when the APK is signed with the same release certificate. Uninstalling the app may remove local app data.

### Optional - Install with ADB

If you have Android Platform Tools installed, you can install the APK from a computer:

```bash
adb install -r BaliTravelHealth.apk
```

---

## Build from Source

```bash
# Clone the repository
git clone https://github.com/your-username/BaliTravelHealth.git
cd BaliTravelHealth

# Build a debug APK
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Build a release APK

```bash
./gradlew assembleRelease
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleRelease
```

For public distribution, configure Android app signing first and publish a signed release APK in GitHub Releases. Do not distribute debug builds as official releases.

---

## Project Structure

```text
app/
  src/main/java/com/visitbali/balitravelhealth/
    data/          Data models, DTOs, Room database, repositories, API client
    ui/            Jetpack Compose screens, components, navigation, theme
    viewmodel/     Screen state and business logic
  src/main/res/    Strings, images, icons, animations, themes
gradle/            Gradle wrapper and version catalog
```

---

## Tech Stack & Libraries

| Library / Framework | Purpose |
|---|---|
| **Kotlin** | Main programming language |
| **Jetpack Compose** | Declarative Android UI |
| **Material 3** | UI components and design system |
| **Navigation Compose** | In-app navigation |
| **Room** | Local database and offline cache |
| **DataStore Preferences** | Local user/session preferences |
| **Retrofit, OkHttp, Gson** | Backend API communication |
| **Google Sign-In** | Google authentication |
| **Google Play Services Location** | Location-based Bali travel context |
| **Google Maps / Maps Compose** | Healthcare facility map experience |
| **Coil** | Image and GIF loading |
| **Lottie Compose** | App animations |

---

## Privacy

- No advertising SDK is included.
- No third-party analytics SDK is included.
- Authentication/session data is stored locally with Android DataStore.
- Facility, guide, nurse, and life support content can be cached locally with Room.
- Location permission is used for Bali travel context and healthcare facility features.
- Network requests are sent to the Bali Travel Health backend: `https://backend.balihealth.me/`.

---

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m "Add my feature"`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

---

## Contact

For questions, feedback, or issues, please open a [GitHub Issue](../../issues).

---

<p align="center">Made for travellers visiting Bali</p>
