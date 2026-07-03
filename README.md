# Anim Scale Toggle — Change Animation Scale Without Developer Options (Android)
![Static Badge](https://img.shields.io/badge/Built_with_Claude-665c4f?style=for-the-badge)
![Static Badge](https://img.shields.io/badge/—_vibe_coded_with_love_<3-ff0000?style=for-the-badge)

A small Android Studio project that lets you change your **window animation scale**, **transition animation scale**, and **animator duration scale** with one tap—without needing Developer Options turned on. Built specifically for the case where a **banking app refuses to load unless Developer Options is disabled**, but you still want fast or disabled animations the rest of the time.

> **This is an Android Studio source code project, not an APK.** There is no pre-built install file here. You will need to open this folder in Android Studio, build it, and install it to your own device. See [Setup](https://www.google.com/search?q=%23setup) below.

## Features

* **Home Screen Widget:** A responsive 1x1 button that changes color to indicate your current animation state.
* **App Shortcuts:** Long-press the launcher icon for direct "Off," "On," and "Toggle" actions.
* **Quick Settings Tile:** Swipe down and toggle animations instantly. The tile visually reflects the current ON/OFF state.
* **Customizable Speeds:** Set your preferred "off" speed via the in-app interface (Defaults to `0.5x`, the sweet spot for perceived speed without UI choppiness).
* **Persistent Settings:** Your custom animation scales survive device reboots, app force-closes, and even app uninstalls.
* **Zero Background Overhead:** Operates entirely through direct `Settings.Global` writes. There is no persistent background service, no internet permission, and no analytics.

## The Problem & The Fix

Android normally ties animation scales to the Developer Options menu. When you turn Developer Options off—which is increasingly required by banking apps enforcing strict security checks—Android silently resets all your animation scales back to 1.0x. If you frequently re-enable USB debugging for your own projects, you end up having to manually reset your animation scales every single time you toggle it.

**The Fix:** This app bypasses the Developer Options menu entirely by leveraging the `WRITE_SECURE_SETTINGS` permission. By granting this permission once via ADB, the app can directly modify the system's animation scales. You can keep Developer Options permanently **off** (keeping your banking apps happy), while your custom animation scale stays exactly as you set it.

## Setup

1. **Clone or download this repo.** Open the project folder (`AnimScaleToggle/`) in Android Studio. Let Gradle sync and build the project.
2. **Install the app** to your device (USB debugging must be temporarily enabled for this step).
3. **Set up ADB on your computer:**
* Download the [Android SDK Platform-Tools](https://www.google.com/search?q=https://developer.android.com/tools/releases/platform-tools) for your operating system.
* Extract the ZIP file to a dedicated folder on your machine (e.g., `C:\platform-tools`).
* Open your terminal (Command Prompt, PowerShell, or macOS/Linux Terminal) and navigate to that extracted folder.

4. **Grant the permission.** With your device still connected and USB debugging on, run this one-time command from your terminal:
```bash
adb shell pm grant com.ricafolio.animscaletoggle android.permission.WRITE_SECURE_SETTINGS
```

*(Note for PowerShell users: You may need to prefix the command with `.\`, like `.\adb shell...`)*
5. **Turn Developer Options back off.** The permission grant is unaffected, and your secure apps will now load normally.
6. **Configure your layout.** Add the Quick Settings tile, pin the widget, or pin a shortcut. Open the app once to set your preferred "off" scale.

No further ADB or Developer Options access is needed after Step 4, unless you uninstall and reinstall the app.

## FAQ

* **Does this need Developer Options on to work day-to-day?**
No. Only the one-time ADB grant in Step 4 requires it.
* **Will my banking app still see Developer Options as off?**
Yes. This app does not touch the Developer Options toggle, only the three underlying animation scale values.
* **What scale should I use?**
`0.5x` is the default and a common sweet spot. Anything lower than `0.4x` can make the UI feel choppy rather than faster.
* **Is this the same as SystemUI Tuner?**
It uses the exact same underlying permission and approach, but it is scoped exclusively to the animation-scale use case, meaning no extra settings or bulky UI.

## Notes

* **Requirements:** Min SDK 26 (Android 8.0+). Tested and working across OEM skins, including Samsung One UI.
* **Troubleshooting:** If a tile, shortcut, or widget ever shows "Permission missing," the secure settings grant was revoked (typically from reinstalling the app). Simply rerun the ADB command from Step 4.