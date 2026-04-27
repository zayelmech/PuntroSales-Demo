# Puntro Sales Demo

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
