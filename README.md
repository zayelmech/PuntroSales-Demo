# Puntro Sales

An Android point-of-sale app for managing products, inventory, sales, customers, and purchasing needs. Core sales and inventory workflows use local storage; publishing a web catalog requires an internet connection and authentication.

[Google Play](https://play.google.com/store/apps/details?id=com.imecatro.demosales) · [Testing community](https://groups.google.com/g/puntro-sales-demo) · [Project reports](https://apps.imecatro.com/puntrosales/)

## Feature status

Checked items are implemented. Remaining work is listed under [Pending features](#pending-features).

### Getting started — Onboarding

The app introduces a five-step, optional checklist: configure the business, save five products, save an order with products, consult the Fulfillment plan, and publish/share the catalog. **Start** opens the checklist; **Not now** keeps the app available without completing or hiding the remaining steps. Steps can be opened in any order.

- The introduction is claimed once using `ONBOARDING_VERSION` (currently `1`), independent of the app version. Missing preferences cover both a new installation and the first upgrade from an older release. Rotation keeps the same presentation; later launches and ordinary app updates do not present it again.
- `onboarding_prefs` stores the presented version, individual milestones, current product count, completion, and acknowledgement separately. Completed milestones survive product deletion, order cancellation, and catalog removal.
- Existing saved products and pending/completed orders with products are recognized through the existing use cases. Business details count after an explicit successful save; changing theme/language or using default values alone does not count. For older versions without a confirmation marker, a saved, non-default business name is the available evidence. Ambiguous default profiles need one confirmation; logo and description are optional.
- The Fulfillment step completes only after a successful, usable load, including an empty or fully covered plan. Loading errors or missing inventory/unit information do not count. New users are encouraged to save their first order as pending.
- The catalog milestone is recorded after confirmed public-route publication, or recognition of an already published catalog. Opening preview/management or cancelling a share chooser does not count. Existing link/QR sharing remains available; sharing never implies receipt. Catalog publishing still requires authentication/network and is unavailable in the existing Huawei distribution.
- A compact themed inventory card shows progress and the next step. Its checklist scrolls independently and supports English/Spanish and large text. Finishing all five steps removes the card and shows a brief confirmation.

**Composition and dependency injection:** all onboarding types live in `:app` under `onboarding`. Hilt binds `OnboardingComponent` to `MaterialOnboardingComponent` and injects it into `MainActivity`. The app passes its composable through the products navigation graph, adaptive pane, and inventory state container into the optional `banner` slot. Inventory renders the slot in its `Scaffold.bottomBar`, outside the product list and empty-state scroll containers; Scaffold reserves its height and places floating actions above it. An empty slot consumes no space. No UI module depends on `:app` or knows onboarding rules. App-owned decorators of existing repository contracts record successful product/order/catalog operations; business confirmation and successful plan loads are connected in app code. ViewModels contain no UI contexts, composables, or navigation controllers.

**Validation:** onboarding unit tests cover presentation/versioning, persisted milestones, legacy defaults, existing data, real operation success/failure, and plan results. Android tests cover SharedPreferences upgrades/restarts, introduction actions, arbitrary checklist order, navigation destinations, fixed banner scrolling/removal, empty slots, and large fonts in dark theme. Run:

```sh
./gradlew :app:testGoogleDebugUnitTest :app:connectedGoogleDebugAndroidTest
./gradlew :app:assembleGoogleDebug :app:assembleHuaweiDebug
./gradlew :app:lintGoogleDebug
```

Validated on 2026-09-30: Google/Huawei debug builds, 45 app unit tests (17 onboarding tests), 17 sales unit tests, 11 Android tests (10 onboarding tests) on API 35, and Google debug lint. Manual emulator checks covered the actual Hilt entry point, initial dismissal, rotation, restart, and landscape at 200% text size. Remote publication is covered through repository test doubles; no real catalog was published during validation.

### Fulfillment plan — Plan de surtido

Turn pending orders into a purchasing list. Open **Sales → the highlighted clipboard button in the top bar** to see the plan for all pending orders, independently of the sales list filters.

- [x] Consolidate product demand across pending orders.
- [x] Show available inventory and the missing quantity for each product.
- [x] Keep demand totals separate by unit, including fractional quantities.
- [x] Switch between all requested products and products to buy.
- [x] Expand a product to see related orders, customer names, and quantities; open an order from the plan.
- [x] Share a text shopping list containing only missing quantities, without customer information.
- [x] Update the plan when orders or inventory change, with manual refresh and retry.
- [x] Show empty and fully covered states, and block sharing when inventory or unit information is missing.

**Inventory calculation:** pending orders already deduct stock when saved. The plan adds their demand back to the recorded balance to calculate availability, then determines the shortage without counting that demand twice. Draft, cancelled, and completed orders are excluded. Viewing or sharing the plan does not change inventory.

### Products and categories

- [x] Create, view, edit, and delete products.
- [x] Set product images, names, prices, units, initial stock, descriptions, categories, and barcodes.
- [x] Search products by name.
- [x] Filter by category, including uncategorized products.
- [x] Sort by name, price, stock, or creation order in either direction.
- [x] Create, rename, and delete categories.
- [x] Select products and export their inventory as CSV.

### Inventory

- [x] Add or remove stock by quantity.
- [x] View current stock and movement history for each product.
- [x] Deduct stock when a sale is saved as pending or completed; drafts do not deduct stock.
- [x] Restore deducted stock when a sale is cancelled.
- [x] Allow negative balances to represent shortages.
- [x] Export a product's stock history as CSV.

### Sales and reports

- [x] Create sales, save drafts, and resume checkout.
- [x] Save pending sales or mark them as completed.
- [x] Assign customers, add notes, and apply discounts or extra charges.
- [x] Find products by name or barcode while building a sale.
- [x] Duplicate existing sales and cancel tickets.
- [x] List sales, search by ticket ID, and filter by status.
- [x] View ticket details and share the full ticket as an image.
- [x] Select tickets and export sales reports or consolidated product quantities as CSV.
- [x] View today's completed-sales total.
- [x] View daily, weekly, and monthly completed-sales charts with total, average, and peak amounts.

### Customers

- [x] Create, list, search, edit, and delete customers.
- [x] Import all or selected phone contacts and update matching customer records.
- [x] View customer purchase history.
- [x] Record completed purchases and reflect cancellations in customer history.

### Web catalog

- [x] Sign in with Google for catalog publishing.
- [x] Select products and configure store information for a catalog.
- [x] Publish and preview a web catalog.
- [x] Open, copy, and share the public catalog link.
- [x] Display and share a catalog QR code.
- [x] View publication status and product, category, and image counts.
- [x] Unpublish a catalog.

### Store profile and preferences

- [x] Update the store logo, name, description, location, and WhatsApp number.
- [x] Choose the store currency.
- [x] Switch the app language between English and Spanish.
- [x] Switch between light and dark themes.
- [x] Persist profile information and preferences locally.

### Pending features

- [ ] Filter the sales list by date or a custom date range. Status filtering and chart period selection are already implemented.
- [ ] Export a complete backup of sales, products, and customers as a ZIP file.
- [ ] Connect catalog visit counts to real analytics; the current count uses placeholder data.

## Screenshots

Sales list, product details, and checkout on a phone in portrait orientation:

<p align="center">
  <img src="doc/img/sales_list.jpg" alt="Sales list with ticket status and totals" width="320"/>
  <img src="doc/img/product_details.jpg" alt="Product details and inventory" width="320"/>
  <img src="doc/img/checkout.jpg" alt="Sale checkout" width="320"/>
</p>

### Creating a product

<p align="center">
  <img src="doc/img/product.gif" alt="Creating a product in Puntro Sales" width="320"/>
</p>

## Architecture

The project is organized by layer and feature. Dependencies point toward the domain: **UI → Domain ← Data**. The app module connects the layers and owns navigation and dependency injection.

```mermaid
flowchart TB
    app[":app"] --> ui["UI feature modules"]
    app --> data["Data feature modules"]
    ui --> domain["Domain feature modules"]
    data --> domain
    ui --> theme[":demosales-ui:theme"]
    domain --> core[":demosales-domain:core"]
    theme --> core
```

| Module | Responsibility |
| --- | --- |
| `:app` | App entry point, navigation, dependency wiring, profile settings, and Firebase integrations. |
| `:demosales-ui:products` | Product screens, categories, inventory, and catalog publishing. |
| `:demosales-ui:sales` | Sales, checkout, reports, and the fulfillment plan. |
| `:demosales-ui:clients` | Customer management, contact import, and purchase history. |
| `:demosales-ui:theme` | Shared theme, UI components, and presentation utilities. |
| `:demosales-domain:products`, `:demosales-domain:sales`, `:demosales-domain:clients` | Domain models, repository contracts, and use cases. |
| `:demosales-domain:core` | Shared domain contracts, profile models, and coroutine utilities. |
| `:demosales-data:products`, `:demosales-data:sales`, `:demosales-data:clients` | Repository implementations and persistence. |

The UI uses **Jetpack Compose and Material 3**, dependency injection uses **Dagger/Hilt**, and local database persistence uses **Room**. Domain modules use Kotlin/JVM and coroutines. The fulfillment plan reuses existing domain use cases, with its presentation and aggregation in `:demosales-ui:sales`.

## Project reports

[Browse code quality, API documentation, and test coverage reports](https://apps.imecatro.com/puntrosales/).
