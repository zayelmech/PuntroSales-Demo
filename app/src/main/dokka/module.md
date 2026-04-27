# Module app

This project follows a **Clean Architecture** approach combined with **MVVM** and is highly **Modularized** to ensure scalability, testability, and separation of concerns.

## Architecture

The project is divided into three main layers:

1.  **Data Layer**: Responsible for data persistence and retrieval (Room, Network).
2.  **Domain Layer**: Contains the core business logic, models, and Use Cases. This layer is pure Kotlin/Java and has no dependencies on the Android framework.
3.  **UI Layer**: Handles the display of information and user interaction using **Jetpack Compose** and **ViewModels**.

## Modularization

The project is modularized by feature and by layer:

-   **`:app`**: The main entry point of the application.
-   **`:demosales-ui:*`**: Modules containing UI components for specific features (Products, Sales, Clients).
-   **`:demosales-domain:*`**: Pure Kotlin modules containing the business logic for each feature.
-   **`:demosales-data:*`**: Modules responsible for data management (Room entities, DAOs, Repositories).

This structure allows for independent development of features and faster build times.

## App Module Folder Structure

The `:app` module acts as the orchestrator of the application, connecting all feature modules and providing global configurations.

- **`com.imecatro.demosales.di`**: Contains Hilt modules for dependency injection, providing database instances, repositories, and use cases to the rest of the application.
- **`com.imecatro.demosales.ui`**: Houses top-level UI components, such as the `AdaptiveScaffold`, which handles the main navigation layout (rail, drawer, or bottom bar) based on screen size.
- **`com.imecatro.demosales.datasource`**: Includes the main Room database definition (`AppRoomDatabase`) that aggregates all DAOs from the data modules.
- **`com.imecatro.demosales.navigation`**: Contains the navigation graphs and destination definitions for each feature (Products, Sales, Clients), facilitating a decoupled navigation system.
- **`MainActivity.kt`**: The single activity of the application, responsible for setting up the UI and navigation.
- **`PuntroSalesDemoApp.kt`**: The `Application` class, annotated with `@HiltAndroidApp` to initialize Hilt.
