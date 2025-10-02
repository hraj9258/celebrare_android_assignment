# Celebrare Assignment

An Android app built with Kotlin and Jetpack Compose. It demonstrates a simple authentication flow backed by Firebase Authentication and a lightweight text editor experience that persists data to Cloud Firestore. The project follows a modular package-by-feature structure and uses Koin for dependency injection.

## Features
- Email/Password authentication (Firebase Auth)
- Text editor
  - Create and edit text items
  - Reorder pages/items
  - Basic alignment actions (left/center/right)
- Cloud persistence using Firebase Cloud Firestore
- Jetpack Compose UI with Navigation Compose
- Dependency injection with Koin

## Tech stack
- Kotlin, Coroutines/Flow
- Jetpack Compose, Material 3
- Navigation Compose
- Firebase Authentication, Cloud Firestore
- Koin (DI)
- Gradle Kotlin DSL

## Prerequisites
- Android Studio (Latest Stable)
- An Android device or emulator
- A Firebase project if you want to run with your own backend
  - Enable Email/Password in Authentication
  - Create a Firestore database (in test or production mode as needed)
  - Download the `google-services.json` and place it at `app/google-services.json`

## Getting started
1. Clone the repo
   ```bash
   git clone https://github.com/hraj9258/CelebrareAssignment.git
   cd CelebrareAssignment
   ```
2. Open the project in Android Studio.
3. Sync Gradle and let the IDE download dependencies.
4. Connect a device or start an emulator.
5. Run the app from Android Studio (Run > Run 'app').

## Usage
- Launch the app.
- Sign up or sign in with email and password.
- Navigate to the Text Editor.
- Add/edit text items and reorder pages as needed; changes persist to Firestore.

## Project structure (high level)
- `app/` — Main application module
  - `src/main/java/com/hraj9258/celebrareassignment/`
    - `CelebrareApp.kt` — App class and DI start
    - `MainActivity.kt` — Entry activity
    - `di/AppModule.kt` — Koin module (Firebase/Koin/ViewModels)
    - `navigation/` — Navigation routes and host (`NavigationRoute.kt`, `MainNavHost.kt`)
    - `auth/` — Authentication feature
      - `data/FirebaseAuthRepository.kt`
      - `domain/AuthRepository.kt`
      - `presentation/` — `AuthViewModel`, `SignInScreen`, `SignUpScreen`, state/actions
    - `texteditor/` — Text editor feature
      - `data/` — DTOs and `FirebaseTextEditorRepository`
      - `domain/` — Contracts and models (`TextEditorRepository`, `TextItem`)
      - `presentation/` — Compose screens (`TextEditorScreen`, `PageReorderScreen`, `PageState`, `TextEditorViewModel`)
    - `core/ui/theme/` — Compose theme files
  - `src/main/res/` — Resources (drawables, strings, themes)
- `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml` — Build setup

## License
This repository is provided for assignment/demo purposes. If you intend to use it beyond that, please contact the author.

## Author
- hraj9258
- Updated: 2025-10-02
