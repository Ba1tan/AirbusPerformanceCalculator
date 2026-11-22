<p align="center">
  <img src="app/src/main/res/drawable/loadingicon.jpeg" width="140" alt="Airbus Performance Calculator Logo" style="border-radius: 20px;" />
</p>

<h1 align="center">Airbus Takeoff Performance Calculator</h1>

<p align="center">
  <strong>Aviation EFB Takeoff Performance & METAR/TAF Weather Utility for Airbus Aircraft</strong>
</p>

<p align="center">
  <b>🇬🇧 English</b> &nbsp;|&nbsp; <a href="README_RU.md">🇷🇺 Русский</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform: Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Min_SDK-24-blue?style=for-the-badge" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target_SDK-34-blue?style=for-the-badge" alt="Target SDK" />
  <img src="https://img.shields.io/badge/Design-Material_3_Dark-00E5FF?style=for-the-badge" alt="Material 3" />
</p>

---

## ✈️ Overview

The **Airbus Takeoff Performance Calculator** is an Android application designed for flight simmers and aviation enthusiasts (MSFS, X-Plane, Prepar3D) to compute essential takeoff parameters for Airbus aircraft.

Based on aerodynamic lookup tables, weight & balance, and environmental conditions, the app computes:

* **$V_1$** — Decision Speed
* **$V_R$** — Rotation Speed
* **$V_2$** — Takeoff Safety Speed
* **THS (Trimmable Horizontal Stabilizer)** — Pitch trim setting (e.g. `UP 1.2` or `DN 0.5`)
* **FLEX Temp** — Reduced-thrust takeoff power setting

---

## 📥 Download App

Pre-built, ready-to-install APK binaries are available on the **[Releases](https://github.com/nursultandev/AirbusPerformanceCalculator/releases)** page of this repository.

1. Go to the **Releases** tab.
2. Download the latest `.apk` file to your Android device.
3. Install and run the app.

---

## ✨ Key Features

### 🛩️ Multi-Aircraft Fleet Support
Certified aerodynamic models and weight envelopes (Empty Weight & MTOW) for:
* **Airbus A319-132** (IAE V2527-A5 engines)
* **Airbus A320-214** (CFM56-5B4 engines)
* **Airbus A320-251N NEO** (CFM LEAP-1A26 engines)
* **Airbus A321-211** (CFM56-5B3 engines)
* **Airbus A330-243** (Rolls-Royce Trent 772B-60 engines)

### 📈 High-Precision Continuous Interpolation Engine
* **Linear Interpolation:** Eliminates discrete speed jumps across weight brackets for smooth, realistic speeds at any gross weight.
* **Environmental & Systems Corrections:**
  * Runway elevation & pressure altitude ($QNH$)
  * Engine Anti-Ice status (`ON` / `OFF`)
  * Air Conditioning & Packs status (`ON` / `OFF`)
  * Runway Surface Conditions (`DRY`, `WET`, `LOW` friction)
* **Mathematical THS Trim Curve:** Resolves Trimmable Horizontal Stabilizer pitch settings as a function of aircraft $\% \text{ MAC}$ center of gravity.

### 🌐 Global METAR & TAF Weather Station
* **Real-Time Data:** Queries live METAR and TAF reports for any ICAO airport worldwide.
* **Intelligent Weather Decoder:**
  * Decodes wind direction, speed, gusts, and variable headings (e.g. `150° @ 3 MPS` or `240° @ 15 KT`).
  * Full support for both **$KT$ (Knots)** and **$MPS$ (Meters per Second)** (with automatic knot equivalent conversions), plus resilience to teleprinter formats and typos (e.g. `MO02/\nMO2` $\rightarrow -2^\circ\text{C}$).
* **One-Tap "Apply to Calculator":** Automatically transfers decoded airport temperature and QNH directly into the Takeoff Performance Calculator form.

### 🎛️ Airbus Cockpit Glass UI (AMOLED Dark)
* **Pure Black (#000000) Background:** Ideal for cockpit tablets and night flight simulation.
* **Airbus Glass Cockpit Color Palette:**
  * <span style="color:#00E5FF;">**Cyan**</span> for active parameters and primary accents.
  * <span style="color:#00E676;">**Green**</span> for confirmed takeoff speeds.
  * <span style="color:#EA80FC;">**Magenta**</span> for pitch trim (THS) guidance.
  * <span style="color:#FFB300;">**Amber**</span> for FLEX temperature.
* **Convenient Controls:** Dropdown selectors for Flaps (`1+F`, `2`, `3`) and Runway condition, with clear unit suffixes (`kg`, `% MAC`, `ft`, `°C`, `hPa`).

---

## 📋 How to Use

1. **Select Aircraft:** Choose your Airbus model from the dropdown (e.g. `A320-214` or `A330-243`).
2. **Fetch Weather (Optional):**
   * Switch to the **METAR / TAF** tab.
   * Enter the departure airport ICAO code (e.g. `UACC`, `EGLL`, `KJFK`) and tap **FETCH**.
   * Tap **Apply Weather to Calculator** to pre-fill temperature and QNH.
3. **Configure Flight Data:**
   * Select Flaps configuration (`1+F`, `2`, or `3`).
   * Enter Gross Weight ($kg$) and Center of Gravity ($CG$ in $\% \text{ MAC}$).
   * Verify Elevation ($ft$), Temperature ($^\circ\text{C}$), QNH ($hPa$), and runway condition.
4. **Calculate:** Tap **CALCULATE TAKEOFF DATA** and use the calculated $V_1$, $V_R$, $V_2$, THS Trim, and FLEX Temp for entry into your aircraft's MCDU / FMGS.

---

## 👨‍💻 Author

* **Nursultan B.** — Developer & Maintainer

---

## ⚠️ Disclaimer
*This application is developed strictly for flight simulation and entertainment purposes (Microsoft Flight Simulator, X-Plane, Prepar3D, etc.). It is **NOT** certified or approved for real-world flight operations.*
