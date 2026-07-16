# ♻️ WasteRecognitionApp

An **Android client** for the Waste Recognition **Federated Learning** system, built with **Kotlin** and **Jetpack Compose**. The app performs on-device waste image classification and on-device (local) training using **PyTorch Mobile (Lite Interpreter)**, then synchronizes the locally-trained classifier weights with a central [WasteRecognitionServer](https://github.com/MChandraR/WasteRecognitionServer) to be merged via **Federated Averaging (FedAvg)**.

This app is the client-side counterpart of the **[WasteRecognitionServer](https://github.com/MChandraR/WasteRecognitionServer)** backend.

## 📌 About The Project

Instead of sending raw photos to a central server, this app trains a lightweight classifier **directly on the device**:

1. A frozen **EfficientNet-B0** backbone (shipped as a `.ptl` TorchScript Lite asset) extracts image features on-device.
2. A small classifier head (weights + bias) is trained locally on the user's own captured/annotated images using a hand-written SGD training loop.
3. Only the trained classifier weights (not the raw photos) are uploaded to the server.
4. The server merges weights from all participating devices using FedAvg and redistributes the new global classifier parameters back to every client.

Waste classes recognized by the model:
- Glass
- Paper
- Cardboard
- Plastic
- Metal
- Trash (other)

## ✨ Key Features

- **On-Device Inference** — classify a photo of waste directly on the device using the PyTorch Mobile backbone + classifier head.
- **On-Device Training** — capture/import images, annotate them with a label, and train the classifier head locally without uploading raw images.
- **Federated Learning Sync** — download the shared backbone & latest global classifier parameters, then upload locally-trained weights back to the server.
- **Data Preprocessing** — built-in dataset preparation/annotation flow (see `data_preprocessing`, `anotate`, and `importimage` features).
- **Non-IID Data Simulation** — includes a Dirichlet-distribution sampler (`DirichletSampler.kt`) for simulating realistic, non-uniform (non-IID) client data splits.
- **Training History** — view past training sessions and their details (loss curves via Vico charts).
- **Authentication** — login flow backed by the server's JWT-based auth API.
- **Modern UI** — fully built with Jetpack Compose, Material 3, and a modular MVVM-style feature architecture.

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| On-Device ML | PyTorch Android / PyTorch Android TorchVision (Lite Interpreter, `.ptl` models) |
| Networking | Retrofit2, OkHttp3, Gson |
| Charts | Vico (Compose / Multiplatform / Views) |
| Math / Sampling | Apache Commons Math3 (Dirichlet sampling) |
| Images / GIFs | Coil, Coil-GIF |
| Build System | Gradle (Kotlin DSL), Android Gradle Plugin |

## 📂 Project Structure

```
WasteRecognitionApp/
├── app/
│   ├── build.gradle.kts               # App module Gradle config & dependencies
│   ├── libs/                          # Bundled PyTorch Android AAR libraries
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/
│       │   │   ├── backbone.ptl       # Frozen EfficientNet-B0 backbone (TorchScript Lite)
│       │   │   ├── param.json         # Local classifier weights/bias
│       │   │   └── attribute.txt / dataset/   # Bundled sample dataset & labels
│       │   ├── java/com/wasterec/app/
│       │   │   ├── MainActivity.kt
│       │   │   ├── manager/           # ModelManager, EfficientNetB0 (inference & on-device training), FileManager, DatasetManager
│       │   │   ├── services/          # ApiService, GlobalModelService, UserService, TrainingService, DatasetService
│       │   │   ├── repositories/      # GlobalModelRepository, UserRepository, TrainingRepository, DatasetUploadRepository
│       │   │   ├── model/             # Domain & API request/response models
│       │   │   ├── utils/             # AuthInterceptor, DirichletSampler, ImageUtil, DateUtil, IOUtils, etc.
│       │   │   ├── helper/            # Custom error/exception types
│       │   │   ├── shared/components/ # Reusable Compose UI components
│       │   │   └── feature/           # Feature modules (see below)
│       │   └── res/                   # Drawables, mipmaps, strings, themes
│       ├── test/                      # Unit tests
│       └── androidTest/               # Instrumented tests
├── build.gradle.kts                   # Root project Gradle config
├── settings.gradle.kts
└── gradle/libs.versions.toml          # Version catalog
```

### Feature Modules (`app/src/main/java/com/wasterec/app/feature/`)

| Feature | Purpose |
|---|---|
| `splash` | App splash screen |
| `login` | User authentication |
| `home` | Home dashboard |
| `main` / `navigation` | Bottom navigation & app-wide navigation graph |
| `modelload` | Downloading/loading the backbone & classifier parameters |
| `importimage` | Importing/capturing images for annotation |
| `anotate` | Labeling captured/imported images before training |
| `data_preprocessing` | Preparing the local dataset for training |
| `training` | Running on-device training & submitting results to the server |
| `training_history` / `training_history_detail` | Viewing past training sessions and their metrics |
| `neural_search` | On-device model inference / classification screen |

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest stable version recommended)
- JDK 11
- Android SDK — `minSdk 24`, `targetSdk 36`, `compileSdk 36`
- A running instance of [WasteRecognitionServer](https://github.com/MChandraR/WasteRecognitionServer) (local or remote)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/MChandraR/WasteRecognitionApp.git
   cd WasteRecognitionApp
   ```

2. **Open in Android Studio** and let Gradle sync finish (this downloads dependencies including the bundled PyTorch Android AARs referenced in `app/libs/`).

3. **Point the app to your backend server**

   The API base URL is currently defined in `ApiService.kt`:
   ```kotlin
   open class ApiService(
       val baseUrl : String = "http://192.168.1.4:8000/api/",
       ...
   )
   ```
   Update this value to match the address of your running `WasteRecognitionServer` instance (e.g. your machine's local IP if testing on a physical device, or `10.0.2.2` if using the Android emulator against a server running on `localhost`).

4. **Run the app** on an emulator or physical device (minimum Android 7.0 / API 24).

> ℹ️ The manifest declares `android:usesCleartextTraffic="true"`, which allows plain HTTP traffic — convenient for local development, but you should switch to HTTPS and remove this flag before any production release.

## 🔗 Server Integration

This app talks to the following areas of the backend API (see the [WasteRecognitionServer README](https://github.com/MChandraR/WasteRecognitionServer) for full endpoint details):
- Authentication (`/api/login`, `/api/auth/register`)
- Model distribution (`/api/model/download`, `/api/model/classifier/weight`)
- Weight submission for FedAvg (`/api/model/weight`)
- Training session status/progress (`/api/training/*`)
- Dataset upload (`/api/dataset`)

## 🧠 How On-Device Training Works

1. The frozen backbone (`backbone.ptl`) extracts a 1280-dimensional feature vector for each labeled image.
2. A simple linear classifier (`weights` + `bias`) is trained on these cached features using mini-batch gradient descent with a softmax cross-entropy loss, implemented manually in `EfficientNetB0.kt`.
3. After training, the updated weights can be pushed into the loaded PyTorch module via the model's custom `update_last_layer` method, and/or submitted to the server for aggregation.

## 🧪 Testing

- Unit tests: `app/src/test/java/com/wasterec/app/`
- Instrumented tests: `app/src/androidTest/java/com/wasterec/app/`

Run them from Android Studio or via Gradle:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 📄 License

No official license has been specified for this project yet. Please contact the repository owner for further usage information.

## 👤 Author

**MChandraR** — [github.com/MChandraR](https://github.com/MChandraR)

## 🔗 Related Repository

- Backend server: [WasteRecognitionServer](https://github.com/MChandraR/WasteRecognitionServer)
