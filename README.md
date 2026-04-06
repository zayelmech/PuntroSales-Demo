# PuntroSales-Demo
Offline Point of Sales for Android

We’re excited to invite you to test our new app Puntro Sales! 🚀

- You can download the app directly from the Google Play Store:
👉 https://play.google.com/store/apps/details?id=com.imecatro.demosales

- Or,join our closed testing group here:
👉 https://groups.google.com/g/puntro-sales-demo

- Once you’ve joined the group, you’ll see the link to download the app directly from the Google Play Store.

## Screenshots

* Normal phone (Portrait)

<p align="center">
  <img src="doc/img/sales_list.jpg" alt="screenshot 1" width="320"/>
  <img src="doc/img/product_details.jpg" alt="screenshot 2" width="320"/>
  <img src="doc/img/checkout.jpg" alt="screenshot 3" width="320"/>
</p>


### Features

- CRUD for products
  - Products has an inventory
- CRUD for Sales
  - Sales are automatically synchronized with clients and inventory 
- CRUD for clients
  - The idea, is to control clients sales history


- [x] Create a new product
  - Image from device gallery or camera
  - Name
  - Price
  - Category
  - Initial stock
  - Unit (pcs, mL, kg, lb)

- [x] Edit product details
- [x] Delete a product and its stock history
- [x] List all products
- [x] Filter and sort products --> Still in progress
- [x] Default product sorting

- [x] Product search bar
- [x] Download product inventory as CSV

#### Stock Control
- [x] Adjust stock (in/out) by quantity
- [x] Total remaining stock
- [x] Stock syncs with sales; when a sale is completed, items are automatically deducted
- [x] Stock may go negative for pending orders to forecast restocking needs
- [ ] Download stock by product as CSV

### Sales
- [x] List all tickets
- [x] Create a new ticket — stock is automatically deducted
- [x] Cancel a ticket — stock will be re-added
- [x] View ticket details and share a screenshot with overscroll
- [x] Filter sales by date range, status, etc. --> Still in progress
- [x] Download sales as CSV
- [ ] Sales charts and income tracking — *Future*

### Clients
- [x] List all clients
- [x] Add client
- [ ] Sync phone contacts
- [x] Edit client
- [x] Delete client
- [ ] Client sales history

### Home & Settings 
- [ ] Add/Update Store Logo and name, address, phone number
- [ ] Change Currency
- [ ] Change Language
- [ ] Export data (sales, products, clients)) as .zip


This project follows a modular architecture organized first by layer and then by feature.
Dependencies flow inward toward the domain layer:
ui → domain ← data

Dependency injection is managed with Dagger/Hilt.
The UI layer is built with Jetpack Compose and Material 3.
The domain layer depends only on Kotlin Coroutines. Almost free from third party sdk's.
The data layer is currently implemented with Room.