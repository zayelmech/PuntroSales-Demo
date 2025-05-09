# PuntroSales-Demo
Offline Point of Sales for Android


This file must include
- A brief overview of the app, including its main features and functionality
- A list of the technologies and libraries used to build the app, such as Kotlin, Android SDK, and any third-party libraries
- A description of my role in the development of the app, including any specific responsibilities or contributions
- Screenshots or demo videos of the app in action to give people a sense of how it looks and works
- Any relevant information about the app's performance, such as its stability, scalability, or user feedback
- A link to the app's documentation or user manual, if available
- A call to action encouraging people to try out the app and contact me if they have any questions or feedback.



### Features

- Create, delete and update your products from inventory
- Create tickets


## Branching Strategy

### Main branches

- master

Naming Conventions
Generally, we can follow next structured pattern

<feature|bugfix>/<package|layer>/<description>

## Screenshots

* Normal phone (Portrait)

<p align="center">
  <img width="514" height="406" src="doc/img/DetailsProduct.png">
</p>

## UI Layer

### Libraries

- Hilt for dependency injection
- Compose 
- Material 3
- Jetpack libraries
- Coil for images

Need permissions

## Domain layer

It contains all use cases the app needs

### Products
- Get All products 
- Create a new product
- Edit product
- Delete product

### Sales
- Get all tickets
- Create a new ticket
- 

### Libraries

- KotlinX coroutines

## Data Source layer

## Libraries

- Room Database
- Dagger 2 for dependency injection

