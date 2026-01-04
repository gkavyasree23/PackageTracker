# 📦 Package Tracker App

Package Tracker is an Android application developed as a college project to simulate real-world package tracking. The app allows users to track packages using a tracking number, view delivery history, save packages locally, receive delivery notifications, and manage their profile securely.

---

## ✨ Features

### 🔍 Package Tracking
- Track packages using a unique tracking number
- View detailed tracking history (Order Created, Shipped, In Transit, Arrived at Hub)
- ETA-based delivery status calculation
- Delivered badge for completed packages

### 💾 Saved Packages
- Save packages locally using Room Database
- Edit saved package name
- Delete saved packages with confirmation dialog
- Track saved packages instantly
- Filter packages by status:
    - Shipped
    - In Transit
    - Delivering Soon
    - Delivered
- Search saved packages by name or tracking number

### 📊 Analytics
- Total saved packages count
- In Transit packages count
- Delivered packages count
- Pending packages count
- ETA-based delivery analytics

### 🔔 Notifications
- Notification badge showing number of packages delivering today or tomorrow
- Notification screen listing upcoming deliveries

### 👤 User Profile
- View profile details (Name, Email, Date of Birth)
- Edit profile information
- Date picker for DOB
- Change password with old password verification
- Secure logout functionality


---

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM (simplified where required)
- **Database:**
    - Firebase Realtime Database (cloud data)
    - Room Database (local storage)
- **Async:** Kotlin Coroutines
- **Navigation:** Jetpack Navigation Component

---

## 🗂 Data Storage Strategy

- **Firebase Realtime Database**
    - User registration and profile data
    - Package tracking and history data

- **Room Database**
    - Saved packages
    - Offline access
    - Analytics calculation

Delivery status is automatically determined using ETA dates to ensure accurate results even without real-time backend updates.

---

## 🔐 Security Note

For academic simplicity, passwords are stored directly in Firebase Realtime Database.  
In real-world applications, Firebase Authentication and encrypted password storage should be used.

---

## 📱 Screens Included

- Home Screen
- Track Package Screen
- Saved Packages Screen
- Analytics Dashboard
- Notifications Screen
- Profile Screen
- About Us & Contact Us Screen

---

## 🚀 How to Run the Project

1. Clone the repository
2. Open the project in **Android Studio**
3. Connect Firebase to the project
4. Enable **Realtime Database**
5. Run the app on an emulator or physical device

---

