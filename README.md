# LeagueHub

Aplicación Android en Jetpack Compose para consultar ligas, partidos, tablas, equipos y
perfiles, además de gestionar partidos desde el rol de organizador.

## Ejecutar el proyecto

1. Abrir la raíz del repositorio en Android Studio.
2. Sincronizar Gradle con JDK 25 y Android SDK 37.
3. Ejecutar la configuración `app` en un emulador o dispositivo Android 8.0 o posterior.

También se puede generar el APK con:

```powershell
.\gradlew.bat :app:assembleDebug
```

El APK de entrega está en `APK/LeagueHub-debug.apk`.

## Arquitectura

Las funcionalidades usan MVI: `Screen`, `Route`, `ViewModel`, `State` y `Event`. La navegación
se implementa con Navigation Compose y destinos type-safe `@Serializable`. Los repositorios
compartidos se exponen desde `LeagueHubApplication`.

La documentación del bloque Jugador está en `docs/INTEGRANTE-2.md` y la del bloque Match
Center/Organizador en `docs/integrante-3.md`.

## Servicios

- Google Maps: P09 abre la ubicación o las indicaciones mediante una URL segura y un Intent.
- Compartir de Android: P09 y P11 abren el selector del sistema con el resumen correspondiente.
- JSONPlaceholder: Retrofit consulta una publicación pública y muestra el anuncio remoto en Home,
  con manejo de error y reintento.
- Room: la sesión, el rol, la liga y el estado de onboarding se conservan en `leaguehub.db` y se
  restauran durante el splash antes de decidir el destino inicial.

## Librerías

- Jetpack Compose y Material 3 para la interfaz.
- Navigation Compose con Kotlin Serialization para navegación type-safe.
- AndroidX Lifecycle, ViewModel y StateFlow para MVI.
- Room para persistencia local y Retrofit con Gson para la llamada HTTP.
- JUnit, Compose UI Test y Espresso para pruebas.

## Verificación

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

La segunda línea requiere un emulador o dispositivo conectado.
