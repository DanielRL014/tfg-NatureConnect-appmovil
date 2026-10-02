# NatureConnect

NatureConnect is an Android application for nature lovers and bird watchers. It lets users share
photos of the birds they spot, discover other people's sightings on an interactive map, and use an
AI model to identify the birds present in a picture. Guests can browse the community without an
account, while registered users get the full social experience (likes, notifications and a personal
profile).

---

## Table of contents

- [Features](#features)
- [Architecture](#architecture)
- [Project structure](#project-structure)
- [Tech stack](#tech-stack)
- [Navigation routes](#navigation-routes)
- [Permissions](#permissions)
- [Requirements](#requirements)
- [Getting started](#getting-started)
- [Testing](#testing)
- [Documentation conventions](#documentation-conventions)
- [Known limitations](#known-limitations)

---

## Features

### Authentication
- Sign in and sign up with username, password and email.
- The session is persisted in `SharedPreferences` (`NatureConnectPrefs`), so the app opens straight
  on the home screen when the user is already logged in.

### Home feed
- Scrollable list of publications (photo + location).
- Free-text search and filtering by bird family through a dialog.
- Empty and error states handled with dedicated messages.

### Publication detail
- Full-size photo, the birds and tags attached to it, location and like counter.
- Signed-in users can like/unlike a publication; guests are redirected to the login screen.
- Two bottom navigation variants depending on the session (signed-in or guest).

### New publication flow (step by step)
1. Pick a photo from the gallery.
2. Choose the location: current GPS position or a point dropped on a map.
3. Select the birds shown in the photo (searchable and filterable by family).
4. Select the tags and publish — the photo, the publication, the birds and the tags are sent to the
   backend in one flow.

### Bird detection with AI
- Upload a photo to the backend's `/api/deteccion/clasificar` endpoint.
- The response contains one detection per bird: bounding box, detection confidence, predicted class
  and classification confidence.
- Results are shown as cards with the cropped region of the original image (EXIF rotation is
  honoured) and the confidence percentage.
- There is a guest variant of both the upload and the results screen.

### Map
- Google Map with a marker for every publication.
- Tapping a marker opens the publication detail.
- Search bar and family filter dialog, same as the feed.
- Guest variant available.

### Notifications
- Lists the likes received by the user's publications, grouped per publication.
- Stores the last checked date so only new likes are requested on later launches.
- Requires API 26+ (`java.time.LocalDate`).

### Profile
- Shows the signed-in user's name and their own publications.
- Search bar to jump to the feed with a query.

### Guest mode
- Most screens (feed, map, AI detection, publication detail) have a guest version that requires no
  account, using a reduced bottom navigation bar.

---

## Architecture

The app follows an **MVVM** (Model–View–ViewModel) architecture with a repository layer:

```
UI (Jetpack Compose screens)
        │  observes StateFlow
        ▼
ViewModel (androidx.lifecycle, viewModelScope)
        │  calls suspend functions
        ▼
Repository (data/repository)
        │  delegates to the Retrofit service
        ▼
natureconectAPI (network) ──► REST backend
```

- **Unidirectional data flow.** View models expose immutable `StateFlow` properties; private
  `MutableStateFlow` backing fields are updated only inside the view model.
- **Coroutines everywhere.** Every network call is a `suspend` function executed in
  `viewModelScope`, with `Dispatchers.IO` for blocking work such as copying the image to the cache.
- **Manual dependency injection.** Each view model has a `ViewModelProvider.Factory` that builds the
  repository on top of the `RetrofitClient.api` singleton.
- **Single-activity.** `MainActivity` sets the Compose content and hands the `NavHostController`
  to `NavGraph`, which declares every route and decides the start destination based on the stored
  session.

---

## Project structure

```
app/src/main/java/es/unex/natureconnect/
├── MainActivity.kt              # Entry point: theme + navigation graph
├── data/
│   ├── models/                  # Data classes mapped from the API (Publicacion, Ave, Etiqueta, …)
│   └── repository/              # IARepository, PublicacionesRespository, UsuaraiosRepository
├── network/
│   ├── natureconectAPI.kt       # Retrofit contract for every REST endpoint
│   └── RetrofitClient.kt        # Singleton Retrofit/OkHttp builder
├── ui/
│   ├── *.kt                     # One file per Compose screen
│   ├── navigation/NavGraph.kt   # All routes and start destination
│   └── theme/                   # Color, Theme and Type (Material 3)
└── viewModel/                   # View models and their factories
```

---

## Tech stack

| Area | Library |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose (Material & Material 3), Coil for image loading |
| Architecture | MVVM, AndroidX Lifecycle / ViewModel Compose |
| Navigation | Navigation Compose |
| Networking | Retrofit 2.11 + Gson converter, OkHttp logging interceptor |
| Maps & location | Google Maps Compose 4.3, Play Services Location, Accompanist Permissions |
| Async | Kotlin coroutines 1.9, `kotlinx-coroutines-play-services` |
| Backend analytics | Firebase Analytics & Crashlytics (BOM 33.7.0) |
| Tests | JUnit 4, Espresso, Compose UI test |

Build configuration: `compileSdk 35`, `minSdk 24`, `targetSdk 34`, JVM target 1.8, Compose
compiler extension `1.5.1`.

---

## Navigation routes

Defined in `ui/navigation/NavGraph.kt`:

| Route | Screen | Notes |
| --- | --- | --- |
| `login` | `LoginScreen` | Start destination when there is no session |
| `register` | `RegisterScreen` | |
| `home` | `PrincipalScreen` | Start destination when logged in |
| `home/{texto}` | `PrincipalScreen` | Feed with a search query |
| `homeInvitado`, `homeInvitado/{texto}` | `PrincipalInvitadoScreen` | Guest feed |
| `map`, `mapInvitado` | `MapScreen` / `MapInvitadoScreen` | |
| `detalle/{idPublicacion}` | `PublicacionScreen` | Publication detail |
| `NuevaPublicacion` | `NuevaPublicacionScreen` | Step 1: photo |
| `NuebaPublicacionUbicacion` | `SeleccionarUbicacionScreen` | Step 2: location |
| `SeleccionarAves` | `SeleccionarAvesScreen` | Step 3: birds |
| `SeleccionarEtiqueta` | `SeleccionarEtiquetasScreen` | Step 4: tags and publish |
| `IAMandar`, `IAMandarInvitado` | `IAmandarScreen` / `IAmandarInvitadoScreen` | Upload photo for AI |
| `ia_resultados`, `ia_resultadosInvitados` | `DeteccionResponseScreen` / `DeteccionResponseInvitadoScreen` | Detections |
| `Perfil` | `PerfilScreen` | |
| `notificaciones` | `NotificacionesScreen` | Requires API 26+ |

---

## Permissions

Declared in `app/src/main/AndroidManifest.xml`:

| Permission | Why |
| --- | --- |
| `INTERNET` | Talk to the REST backend, load photos and use Google Maps |
| `ACCESS_COARSE_LOCATION` / `ACCESS_FINE_LOCATION` | Current location for a publication and centring the map (requested at runtime) |
| `READ_EXTERNAL_STORAGE` | Pick photos from the gallery |

The Maps API key is provided through the `com.google.android.geo.API_KEY` meta-data tag, and
`network_security_config.xml` allows cleartext HTTP so the app can reach a development backend.

---

## Requirements

- Android Studio (Koala or newer recommended).
- JDK 17 to run Gradle (the app itself targets Java 8).
- An Android device or emulator with API 24+ (API 26+ for the notifications screen).
- A reachable instance of the NatureConnect backend REST API.
- `google-services.json` for Firebase (already present in `app/` for this project).

---

## Getting started

1. **Clone the repository.**

   ```bash
   git clone <repository-url>
   cd NatureConnect
   ```

2. **Point the app at your backend.** The base URL is hardcoded in
   `network/RetrofitClient.kt`:

   ```kotlin
   private const val BASE_URL = "http://192.168.1.44:8080/"
   ```

   Change it to the address of your machine (use `10.0.2.2` when running on the Android emulator
   so it reaches `localhost` on the host).

3. **Check the Google Maps key** in `app/src/main/AndroidManifest.xml`
   (`com.google.android.geo.API_KEY`).

4. **Build and run** from Android Studio, or from the command line:

   ```bash
   ./gradlew :app:assembleDebug
   ./gradlew :app:installDebug
   ```

---

## Testing

```bash
# Local unit tests (host JVM)
./gradlew :app:testDebugUnitTest

# Instrumented tests (device/emulator required)
./gradlew :app:connectedDebugAndroidTest
```

The project ships with the two Android Studio template tests (`ExampleUnitTest` and
`ExampleInstrumentedTest`).

---

## Documentation conventions

Every public declaration in the codebase is documented with **KDoc in English, following the
Google Kotlin style guide**:

```kotlin
/**
 * A photo publication shared in the app feed.
 *
 * @property idPublicacion Unique identifier of the publication.
 * @property idUsuario Author of the publication.
 */
data class Publicacion(/* ... */)
```

- One summary sentence ending with a period, then a blank line before any further paragraph.
- `@param`, `@return` and `@property` tags describe each argument and value.
- Cross-references use `[Symbol]` links.

---

## Known limitations

- The backend base URL and the photo URL prefix are hardcoded, so a different server requires a
  code change.
- The Google Maps API key is committed in the manifest.
- Some identifiers keep their original (misspelled) names, e.g. `UsuaraiosRepository`,
  `PublicacionesRespository`, `es.unex.natureconnect.vi`.
- Unit test coverage is limited to the template tests.
