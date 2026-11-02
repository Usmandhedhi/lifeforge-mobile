# LifeForge: Routine Tracker & Bedtime Shield 🛡️

**Built specifically for Android (Infinix Hot 30i)** to optimize your daily routine, conquer fatigue, achieve 7–8 hours of sleep, train 35–40 mins at the gym, and dedicate 2–3 hours to high-income learning for your family's future.

---

## 🎯 The 3 Daily Pillars

| Pillar | Target | Routine Time Slot | Solution to Hurdles |
| :--- | :--- | :--- | :--- |
| **1. Health & Fitness** | 35–40 min Gym Workout | **08:45 PM – 09:25 PM** | Transition straight from the van into gym clothes. Weight tracked from **90 kg ➔ 78 kg**. |
| **2. Self Learning** | 2–3 Hours | **08:00 AM – 08:45 AM (Van)**<br>**10:15 PM – 11:45 PM (Night)** | Uses the 45-min van commute for passive audio learning, followed by 1.5 hrs deep work at night. |
| **3. Sleep & Recovery** | 7.5 Hours (11:45 PM – 07:15 AM) | **11:45 PM – 07:15 AM** | **Automated Bedtime Shield (Offline Loopback VPN)** cuts all internet & reels access at 11:45 PM. |

---

## ⚡ How the Bedtime Shield Works (No Internet & Forced Sleep)

Late-night doom-scrolling is driven by cheap dopamine and instant connectivity. LifeForge breaks this neurological loop with 3 system layers:

1. **Local Blackhole VPN (`VpnService`)**:
   - At **11:45 PM**, Android starts a dummy loopback VPN.
   - All outgoing IPv4 traffic is captured and routed to a closed local interface (`10.120.0.1`).
   - Instagram, Facebook, TikTok, YouTube Shorts, and web browsers immediately display *"No Internet Connection"*.
   - **100% offline & private**: Zero external servers, no privacy leaks, zero data cost.
2. **Father's Guardian Overlay (`SYSTEM_ALERT_WINDOW`)**:
   - If you attempt to open distracting apps, a full-screen shield appears displaying your family pledge:
     > *“Sleep now, Father. Build our family's future tomorrow!”*
3. **60-Second Deliberate Friction**:
   - In case of an emergency, you can tap *Emergency 10-Min Bypass*, but you must wait out an intentional **60-second countdown**. This pause breaks the unconscious dopamine reflex.

---

## 📱 Infinix Hot 30i (XOS) Setup Guide

Infinix phones run **XOS**, which includes aggressive memory cleaners (*Phone Master* and *Power Marathon*). Follow these 3 quick steps on your phone so the shield is never terminated:

1. **Ignore Battery Optimization**:
   - Open LifeForge ➔ Navigate to the **Shield** tab.
   - Under *Infinix Hot 30i Permissions Checklist*, tap **Allow** next to *Unrestricted Battery*.
   - Select **LifeForge** ➔ choose **"Don't optimize" / "Unrestricted"**.
2. **Allow "Display Over Other Apps"**:
   - In the **Shield** tab, tap **Allow** next to *Display Over Other Apps*.
   - Toggle the switch to ON for LifeForge.
3. **Lock App in Recent Tasks (XOS)**:
   - Swipe up and hold to open Recent Apps.
   - Long press or tap the lock icon above the LifeForge card to lock it in RAM.

---

## 🚀 How to Build and Run

### Option A: Using Android Studio (Recommended)
1. Open **Android Studio**.
2. Select **File > Open...** and navigate to:
   ```
   C:\Users\usman.amin\.gemini\antigravity\scratch\lifeforge-mobile
   ```
3. Connect your **Infinix Hot 30i** via USB cable (with **USB Debugging** enabled in *Developer Options*).
4. Click the green **Run (▶)** button.

### Option B: Build APK via Command Line
Run the following in PowerShell / Terminal:
```bash
cd "C:\Users\usman.amin\.gemini\antigravity\scratch\lifeforge-mobile"
./gradlew assembleDebug
```
The installable APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Transfer this APK file directly to your Infinix Hot 30i via WhatsApp, Google Drive, or USB cable and tap **Install**!

---

## 🛠️ Tech Stack
- **Language**: Kotlin 1.9.24
- **UI Framework**: Jetpack Compose & Material 3
- **Local Storage**: Room Database (SQLite) + SharedPreferences
- **Network Shield**: Android `VpnService` (Local Loopback Interface)
- **Overlay Guardian**: Android `WindowManager` (`TYPE_APPLICATION_OVERLAY`)
- **Alarms**: Android `AlarmManager.setExactAndAllowWhileIdle()`
