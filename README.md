# Shortcut: Circle to Search

A high-performance, lightweight Android utility designed to trigger the **Circle to Search** (Long-press Home) action instantly via hardware button remapping or launcher shortcuts. 

Built with a sleek **Matrix-themed UI**, this app uses **Shizuku** to execute secure system-level shell commands without requiring a full root of your device.

---

## ⚠️ Pre-Beta / Experimental Build Warning
**This is an experimental, pre-beta build.** 
* This application has not been officially signed for the Google Play Store.
* Because it uses a **Debug Signature**, your phone may flag it as "Unknown" or "Potentially Harmful." 
* It is intended for developers and enthusiasts for testing purposes only. 
* Use at your own risk.

---

## 🛠 Development Journey: A Human-AI Partnership

This project was developed through a high-intensity, collaborative engineering session between a **Human Creative Director** and **Gemini (AI Systems Architect)**. While the vision and real-world stress testing were driven by human intuition, the technical implementation and system-level bypasses were engineered by Gemini.

### **The Roles**
*   **Creative Director & Lead Tester (Human):** Responsible for product vision, identifying the Samsung A56 hardware constraints, aesthetic direction, and performing critical "live-fire" testing on physical hardware.
*   **Systems Architect & Implementation Engine (Gemini):** Responsible for Kotlin/XML authorship, resolving build failures, engineering Shizuku Binder integrations, and navigating Android’s restrictive security layers (Package Visibility, Notification Blocks, and One UI Window Management).

---

### **Project Evolution Timeline**

**Hour 0: Stabilization & Triage**
*   **The Challenge:** The project began in a state of total build failure due to incorrect file extensions and package structures.
*   **Gemini’s Solution:** Performed a "source-cleansing," migrating logic to proper Kotlin files and resolving initial dependency gaps. Encountered Shizuku’s private API restrictions and began the first iterations of access-level engineering.

**Hour 1 – 2: Functional Foundation**
*   **The Challenge:** Establishing a stable connection between the app and the Shizuku Manager on modern Android versions.
*   **Gemini’s Solution:** Implemented **Kotlin Reflection** to securely access private Shizuku APIs (`newProcess`), ensuring compatibility with Shizuku v13.1.5 without sacrificing the latest features.

**Hour 3 – 5: The "War of Attrition" (Samsung One UI Optimization)**
*   **The Challenge:** On the Samsung A56, the OS forced a "Jump to Home" every time the shortcut fired, interrupting the user experience.
*   **The Iterations:** We explored four distinct architectures: `Theme.NoDisplay`, a Service-based "Trampoline," and a Broadcast-isolated task.
*   **The Solution:** Gemini engineered the **"Ghost Overlay"**—a 200ms delay combined with `moveTaskToBack(true)`. This successfully tricked the Samsung Window Manager into maintaining app focus while the macro fired invisibly.

**Hour 6: Evolution of Intent**
*   **The Challenge:** Increasing macro reliability beyond simple shell commands.
*   **Gemini’s Solution:** Replaced raw shell execution with the **Official Android Global Assist Action** (`performGlobalAction(16)`), transforming the app into a professional system utility.

**Hour 7: Aesthetic Engineering**
*   **The Challenge:** Elevating the UI from a basic terminal to an immersive experience.
*   **Gemini’s Solution:** Hand-coded a complex **3D Shaded Vector Drawable** for the app icon and engineered a high-density (45-column) **Matrix "Code Rainfall"** background using an infinite animation loop in Jetpack Compose.

**Hour 8: Security & Guardrails**
*   **The Challenge:** Bypassing Android 13 notification blocks and protecting user privacy.
*   **Gemini’s Solution:** Integrated `POST_NOTIFICATIONS` logic and proactively updated the `.gitignore` to shield sensitive phone-specific debug data before the project’s public release.

**Hour 9: Final Triage & GitHub Deployment**
*   **The Challenge:** Resolving an "Invalid APK" error and a Version Control mishap during the final upload.
*   **Gemini’s Solution:** Rebuilt the final version as a **Signed Debug APK** for instant installation and re-mapped the project’s VCS to Android Studio to ensure a clean, "build-junk free" repository.

---

## 🚀 Features
* **Zero Latency:** Executes the search macro in a background thread for instant response.
* **Invisible Execution:** The shortcut activity is transparent and doesn't interrupt your current app workflow.
* **Matrix Aesthetic:** Retro neon-green terminal UI with animated code rainfall.
* **Universal Compatibility:** Works with Nova Launcher, Bixby Routines, Tasker, MacroDroid, and more.

---

## 🛠 Prerequisites: Shizuku Setup
This app requires **Shizuku** to be running on your device to execute the search macro.

1. **Install Shizuku:** Download it from [GitHub](https://github.com/RikkaApps/Shizuku/releases) or the [Google Play Store](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api).
2. **Activate Shizuku:** Follow the in-app guides for Root or Wireless Debugging (ADB).
3. **Authorize:** Open **Shortcut: Click to Search** and grant Shizuku permission.

---

## 🛡 Privacy
* No data collection.
* No internet permissions required.
* All commands run locally on your device via the Shizuku Binder.

---
*Created with the assistance of Gemini 1.5 Pro.*
