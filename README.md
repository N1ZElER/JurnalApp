# 📚 Jurnals – TopAcademy Student App

**Jurnals** is an Android application that connects to the **TopAcademy Journal API** and allows students to view their schedule, exams, and news directly from their mobile device.

The app uses **Retrofit** to communicate with the official API and requires authentication with a student account.

---

# ✨ Features

* 🔐 **Authorization**

  * Login using your TopAcademy account
  * Access token stored locally

* 📅 **Schedule**

  * View today's lessons
  * Shows:

    * Subject
    * Teacher
    * Lesson number
    * Time

* 📚 **Exams**

  * Displays upcoming exams
  * Shows exam date and subject

* 📰 **News**

  * Shows latest academy news

* 🔄 **Swipe Refresh**

  * Pull down to reload data

* 📱 **Modern Android UI**

  * Navigation Drawer
  * Material Design Cards
  * RecyclerView lists

---

# 🛠 Tech Stack

* **Java**
* **Android SDK**
* **Retrofit2**
* **Gson**
* **RecyclerView**
* **Material Components**
* **SwipeRefreshLayout**

---

# 🔑 Authentication

The app authenticates using the API endpoint:

```
POST /api/v2/auth/login
```

Required fields:

```
application_key
username
password
id_city
```

After login, the API returns an **access token** used for further requests.

---

# 🌐 API Endpoints Used

| Feature   | Endpoint                                  |
| --------- | ----------------------------------------- |
| Login     | `/api/v2/auth/login`                      |
| User Info | `/api/v2/settings/user-info`              |
| Schedule  | `/api/v2/schedule/operations/get-by-date` |
| Exams     | `/api/v2/dashboard/info/future-exams`     |
| News      | `/api/v2/news/operations/latest-news`     |

---

# 📱 Screens

The app contains several screens:

* **Schedule Screen**
* **Exams Screen**
* **News Screen**
* **Authorization Screen**
* **Settings Screen**

Navigation between them is done via **Navigation Drawer**.

---

# 🚀 How to Run

1. Clone the repository

```
https://github.com/N1ZElER/JurnalApp.git
```

2. Open the project in **Android Studio**

3. Build the project

```
Build → Make Project
```

4. Run on device or emulator.

---

# ⚠️ Requirements

* Android **8.0+**
* Internet connection
* Valid **TopAcademy account**

---

# 📌 Notes

This project is **not an official TopAcademy application**.
It is a personal project created to interact with the public API.

---

# 👨‍💻 Author

Developed by **N1ZELER**
