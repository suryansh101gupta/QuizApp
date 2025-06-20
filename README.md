# QuizApp

QuizApp is a native Android application developed in Kotlin that allows users to take quizzes, track their scores, and manage their user profiles with modern cloud-backed features.

## 🏗️ Project Overview

This app offers a seamless quiz experience with user authentication, profile management, score history, and profile picture support using Firebase services. Glide is used for smooth image loading in the app.

---

## ✨ Features

- **User Authentication:** Secure login and registration using Firebase Authentication.
- **User Profiles:** Store, update, and display user profiles, including name, email, and profile picture.
- **Profile Picture Upload:** Upload and fetch profile pictures using Firebase Cloud Storage and display them with Glide by Bumptech.
- **Quiz System:** Take interactive quizzes, see your score, and view your quiz history.
- **Score History:** All quiz scores are saved to Firebase Firestore for persistent history.
- **Modern Android UI:** Clean, responsive layouts built with Android Studio and Kotlin.
- **Firebase Integration:** Authentication, Firestore, and Storage are managed through Gradle dependencies.

---

## 📲 Screenshots

<!-- Replace with your actual screenshots -->
![Login Screen](images/login.png)
![Quiz Screen](images/quiz.png)
![Profile Screen](images/profile.png)

---

## 🔧 Tech Stack

- **Language:** Kotlin
- **IDE:** Android Studio
- **Backend & Storage:** Firebase Authentication, Firebase Firestore, Firebase Cloud Storage
- **Image Loading:** Glide (by Bumptech)
- **Build Tool:** Gradle

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/suryansh101gupta/QuizApp.git
cd QuizApp
```

### 2. Open in Android Studio

- Open Android Studio.
- Choose **Open an existing project** and select the cloned folder.

### 3. Configure Firebase

- Add your `google-services.json` file in the `app/` directory for Firebase configuration.
- Ensure Firebase Authentication, Firestore, and Cloud Storage are enabled in your Firebase Console.

### 4. Gradle Dependencies

Make sure your `app/build.gradle` includes the following:

```gradle
// Firebase
implementation 'com.google.firebase:firebase-auth:XX.X.X'
implementation 'com.google.firebase:firebase-firestore:XX.X.X'
implementation 'com.google.firebase:firebase-storage:XX.X.X'

// Glide for image loading
implementation 'com.github.bumptech.glide:glide:4.12.0'
kapt 'com.github.bumptech.glide:compiler:4.12.0'
```
> Replace `XX.X.X` with the latest versions compatible with your project.

- Sync your Gradle project after adding/updating dependencies.

### 5. Run the App

- Connect your Android device or use an emulator.
- Click **Run** in Android Studio.

---

## 🧑‍💻 Usage

- **Register/Login:** Create a new account or login with existing credentials.
- **Set Profile:** Upload a profile picture and personalize your account.
- **Take Quizzes:** Select a quiz, answer questions, and submit to see your score.
- **View Score History:** Check your past quiz scores in your profile.

---

## 📝 Contributing

Contributions are welcome! Please fork the repo and submit a pull request for review.

---

## 🪪 License

This project is licensed under the [MIT License](LICENSE).

---

## 🙋 Author

Developed by [Suryansh Gupta](https://github.com/suryansh101gupta)

---

_Star ⭐ this repository if you found it useful!_
