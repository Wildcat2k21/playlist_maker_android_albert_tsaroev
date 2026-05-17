# PlaylistMaker

Android-приложение для поиска музыки через iTunes API, сохранения треков в избранное и управления плейлистами.

> 📋 **Примечание**: Проект находится в процессе рефакторинга. Многие компоненты имеют суффикс `App` для согласования имён в рамках учебного задания.

## 🎯 Функциональность

- 🔍 Поиск треков через iTunes API с историей запросов
- ▶️ Воспроизведение 30-секундного превью трека
- ❤️ Добавление и удаление треков из избранного
- 📁 Создание плейлистов с названием, описанием и обложкой
- ➕➖ Добавление и удаление треков из плейлистов
- 🌓 Тёмная и светлая тема с сохранением выбора

## 🛠 Технологический стек

| Компонент | Версия | Назначение |
|-----------|--------|------------|
| **Kotlin** | 2.0.21 | Язык разработки |
| **Jetpack Compose** | BOM 2024.09.00 | Declarative UI |
| **Android SDK** | minSdk 29 · targetSdk 36 · compileSdk 36 | Платформа |
| **Android Gradle Plugin** | 8.13.2 | Сборка проекта |
| **Navigation Compose** | 2.9.6 | Навигация между экранами |
| **Room** | 2.7.2 | Локальное хранение (треки, плейлисты, связи) |
| **DataStore Preferences** | 1.1.7 | История поиска и настройки темы |
| **Retrofit + Gson** | 3.0.0 | HTTP-клиент для iTunes API |
| **Coil** | 2.7.0 | Загрузка и кэширование изображений |
| **Coroutines / Flow** | — | Асинхронность и реактивные потоки данных |

## 📋 Требования

- **Android Studio** Meerkat 2025.1.1 или новее
- **JDK** 11
- Устройство или эмулятор с **Android 10+** (API 29)

## 🚀 Сборка и запуск

1. Клонируйте репозиторий:

```bash
   git clone https://github.com/Wildcat2k21/playlist_maker_android_albert_tsaroev.git
```

2. Откройте проект в Android Studio:  
   **File → Open** → выберите корневую папку проекта.

3. Дождитесь завершения синхронизации Gradle (индикатор внизу окна).

4. Подключите устройство или запустите эмулятор (API 29+).

5. Запустите приложение:  
   **Run → Run 'app'** или нажмите `Shift + F10`.

## 🗂 Структура проекта

```
app/src/main/java/com/practicum/playlistmaker/
│
├── creator/
│   └── DependencyProvider.kt          # Ручной DI: фабрики репозиториев
│
├── data/
│   ├── db/
│   │   ├── dao/
│   │   │   ├── PlaylistAppDao.kt
│   │   │   ├── PlaylistAppTrackDao.kt
│   │   │   └── TrackAppDao.kt
│   │   ├── entity/
│   │   │   ├── PlaylistAppEntity.kt
│   │   │   ├── PlaylistAppTrackCrossRef.kt
│   │   │   └── TrackAppEntity.kt
│   │   ├── mapper/
│   │   │   └── DbMappers.kt           # Конвертеры Entity ↔ Domain
│   │   ├── model/
│   │   │   └── PlaylistWithTracks.kt  # Room @Relation модель
│   │   └── PlaylistAppDatabase.kt     # Room Database с миграциями
│   │
│   ├── dto/
│   │   ├── BaseAppResponse.kt
│   │   ├── TrackAppDto.kt
│   │   ├── TrackDtoMapper.kt
│   │   ├── TrackSearchAppRequest.kt
│   │   ├── TracksSearchAppResponse.kt
│   │   └── WordAppDto.kt
│   │
│   ├── network/
│   │   ├── ITunesApiAppFactory.kt     # Retrofit builder
│   │   ├── ITunesApiAppService.kt     # API endpoints
│   │   ├── NetworkAppClient.kt        # Абстракция сетевого слоя
│   │   ├── PlaylistApp.kt             # Domain-модель плейлиста
│   │   ├── PlaylistsAppRepositoryImpl.kt
│   │   ├── RetrofitNetworkAppClient.kt
│   │   ├── SearchHistoryAppRepositoryImpl.kt
│   │   ├── TrackApp.kt                # Domain-модель трека
│   │   ├── TrackAppRepositoryImpl.kt
│   │   └── WordApp.kt
│   │
│   ├── preferences/
│   │   └── SearchHistoryAppPreferences.kt  # DataStore wrapper
│   │
│   ├── storage/
│   │   ├── PlaylistCoverAppStorage.kt # Работа с файлами обложек
│   │   └── StorageProvider.kt         # Фабрика хранилищ
│   │
│   └── utils/
│       └── ArtworkUrlFormatter.kt     # Форматирование URL обложек
│
├── domain/
│   ├── api/
│   │   ├── PlaylistsAppRepository.kt
│   │   ├── SearchHistoryAppRepository.kt
│   │   ├── TracksAppRepository.kt
│   │   └── TrackSearchAppInteractor.kt
│   │
│   └── impl/
│       └── TrackSearchInteractorImpl.kt
│
└── ui/
    ├── activity/
    │   └── PlaylistAppMainActivity.kt # Точка входа приложения
    │
    ├── favorites/
    │   └── FavoritesScreen.kt         # Экран избранного
    │
    ├── main/
    │   └── MenuScreen.kt              # Главное меню
    │
    ├── navigation/
    │   ├── PlayerNavigationArgs.kt    # Передача трека между экранами
    │   └── PlaylistAppHost.kt         # NavHost с маршрутами
    │
    ├── player/
    │   ├── PlayerAppScreen.kt         # UI плеера
    │   └── PlayerAppViewModel.kt      # Логика воспроизведения
    │
    ├── playlist/
    │   ├── CreatePlaylistAppScreen.kt # Создание плейлиста
    │   ├── PlaylistAppScreen.kt       # Детали плейлиста
    │   ├── PlaylistsAppScreen.kt      # Список плейлистов
    │   ├── PlaylistAppViewModel.kt    # VM для одного плейлиста
    │   └── PlaylistsAppViewModel.kt   # VM для списка плейлистов
    │
    ├── search/
    │   ├── SearchAppScreen.kt         # Экран поиска
    │   ├── SearchAppState.kt          # sealed class состояний
    │   └── SearchAppViewModel.kt      # Логика поиска и истории
    │
    ├── settings/
    │   └── SettingsScreen.kt          # Настройки темы
    │
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   ├── ThemeViewModel.kt
    │   └── Type.kt
    │
    └── utils/
        ├── ButtonSample.kt
        ├── CorrectIcon.kt
        └── TopAppButtonBar.kt
```

## 🔄 Архитектурные особенности

- **Clean Architecture**: разделение на слои `data` / `domain` / `ui`
- **Repository Pattern**: абстракция источников данных
- **Unidirectional Data Flow**: состояние экрана управляется через `ViewModel` + `StateFlow`
- **Ручной DI**: `DependencyProvider` для простоты учебного проекта (без Hilt/Dagger)
- **Room с миграциями**: поддержка эволюции схемы БД

## 📝 Лицензия MIT

Проект создан в учебных целях в рамках программы Яндекс.Практикум