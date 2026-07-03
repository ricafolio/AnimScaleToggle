# Anim Scale Toggle — Change Animation Scale Without Developer Options (Android)

A small Android Studio project that changes your **window animation scale**,
**transition animation scale**, and **animator duration scale** with one tap
— without needing Developer Options turned on. Built specifically for the
case where a **banking app refuses to load unless Developer Options is
disabled**, but you still want fast/no animations the rest of the time.

> **This is an Android Studio source code project, not an APK.** There is no
> pre-built install file here — you open this folder in Android Studio,
> build it, and install it to your own device. See [Setup](#setup) below.

## The problem this solves

If you've searched things like:

- "How to change animation scale without developer options"
- "Banking app asks to disable developer options"
- "Animation scale resets when I turn off USB debugging"
- "SystemUI Tuner settings not persisting after disabling dev options"

...you've hit the same wall: Android ties `window_animation_scale`,
`transition_animation_scale`, and `animator_duration_scale` to the
Developer Options screen. Turning Developer Options **off** — which
banking apps increasingly require via `Settings.Global.DEVELOPMENT_SETTINGS_ENABLED`
checks or root/debug detection — silently resets all three animation
scales back to 1x. If you frequently re-enable Developer Options for USB
debugging (e.g. for `adb`, testing your own apps, etc.), you end up
having to manually reset your animation scale every single time.

## The fix

The three animation scale values aren't actually gated by Developer
Options at the permission level — they're controlled by the
`WRITE_SECURE_SETTINGS` permission, which you can grant to any app **once**
via `adb`. That grant is independent of the Developer Options toggle, so:

- Developer Options can be fully **off** (satisfying your banking app).
- Your custom animation scale (e.g. `0.5x`) stays exactly as you set it.
- You can still turn Developer Options back on temporarily for USB
  debugging — it won't touch the animation scale, and turning it back off
  won't reset it either.

This is the same underlying mechanism apps like **SystemUI Tuner** use, but
this project strips it down to just the animation-scale piece, with a
Quick Settings tile, home screen widget, and pinned shortcuts for true
one-tap access — no extra settings, no background service.

## What you get

- **Quick Settings tile** — swipe down, tap, done. Shows ON/OFF in the tile itself.
- **Home screen widget** — 1x1 button, changes color to show current state.
- **App shortcuts** — long-press the launcher icon for direct "Off" / "On" / "Toggle" actions.
- **In-app screen** — manual toggle, live status, and a field to set your preferred "off" speed (default `0.5x`, matching what most people find usable — anything much lower tends to feel choppy).
- Setting persists across reboots, app force-close, and even app uninstall (it's a system settings write, not app state).

## Setup

1. **Clone or download this repo**, open the project **folder** in Android
   Studio (not a file — the whole `AnimScaleToggle/` folder). Let Gradle
   sync; the wrapper downloads Gradle automatically on first open.
2. **Build and run** the app to your device (USB debugging must be on for
   this step, same as installing any dev app).
3. With the device still connected and debugging on, run this one-time
   command from a terminal:

   ```
   adb shell pm grant com.ricafolio.animscaletoggle android.permission.WRITE_SECURE_SETTINGS
   ```

   (The app also shows this exact command with a copy button.)
4. Turn Developer Options back off. The permission grant is unaffected.
5. Add the Quick Settings tile, pin the widget, or pin a shortcut — whichever you'll actually use daily.
6. Open the app once to set your preferred "off" scale (default `0.5x`).

No further ADB or Developer Options access is needed after step 3, unless
you reinstall the app.

## FAQ

**Does this need Developer Options on to work day-to-day?**
No. Only the one-time `adb` grant in step 3 requires it. After that, toggling animations never needs Developer Options at all.

**Will my banking app still see Developer Options as off?**
Yes — this doesn't touch the Developer Options toggle, only the three animation scale values.

**Does the app need to run in the background?**
No. It writes directly to `Settings.Global` and exits/idles; there's no persistent service.

**What scale should I use?**
`0.5x` is the default and a common sweet spot; some people prefer `0.4x`–`0.6x`. Below that many report the UI starts to feel choppy rather than faster. You can set any value from the in-app screen.

**Does the setting survive a reboot or force-closing the app?**
Yes — it's a one-time write to the system settings provider, not something held in the app's memory.

**Is this the same as SystemUI Tuner?**
Same underlying permission (`WRITE_SECURE_SETTINGS`) and approach, just scoped to only the animation-scale use case, with a tile/widget/shortcuts built in instead of a general settings-tweaking UI.

## Notes

- Min SDK 26 (Android 8.0+). Tested approach works across OEM skins including Samsung One UI.
- No internet permission, no analytics, no background service — three `Settings.Global` writes and nothing else.
- If a tile/shortcut/widget ever shows "Permission missing," the grant was revoked (typically from reinstalling the app) — rerun the `adb` command from step 3.
