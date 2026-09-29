# Integrante 3 · Match Center y Organizer

Rama: `feature-cristian`.

## Alcance de esta entrega

Implementación visual en Jetpack Compose, con `Screen` sin estado interno, datos por parámetros,
callbacks, `Route` que proporciona datos fake y previews bajo `LeagueHubTheme`.
Las vistas reutilizan el tema, las tarjetas, los botones y el marcador compartidos.

| Vista obligatoria según la repartición | Archivo dentro de `app/src/main/java/com/leaguehub/app/feature` |
|---|---|
| P02 · Resumen | `matches/matchcenter/MatchCenterScreen.kt` |
| P03 · Alineaciones | `matches/matchcenter/MatchCenterScreen.kt` |
| P04 · Estadísticas | `matches/matchcenter/MatchCenterScreen.kt` |
| O01 · Panel del organizador | `organizer/dashboard/OrganizerDashboardScreen.kt` |
| O02 · Gestión de partido | `organizer/matchmanagement/MatchManagementScreen.kt` |
| O03 · Escanear acta | `organizer/scan/ScanReportScreen.kt` |

P02–P04 son contenidos de una misma pantalla seleccionados con `MatchCenterTab`.
No se incluye galería P05 ni los pasos O04 en adelante: no forman parte de la repartición
del integrante 3. El feedback de la entrega #3 no fue proporcionado; esta selección se basa
en las seis vistas asignadas, y no pretende sustituir la priorización global del equipo.

Fuera de esta fase: navegación, cámara real, reconocimiento OCR, actualizaciones en vivo,
persistencia y backend. El acta es una ilustración hecha con Compose; los controles exponen
callbacks sin implementar esas funciones. No se agregan permisos de cámara.

## Ver las pantallas

1. Abrir un archivo `Screen.kt` de la tabla en Android Studio.
2. Seleccionar **Split** o **Design**, y actualizar Compose Preview.
3. Elegir el preview por su código P02, P03, P04, O01, O02 u O03.

También hay previews de alineación visitante, panel sin pendientes, plantillas completas,
error de gestión y escaneo en búsqueda, procesamiento y error.
`MainActivity` conserva la pantalla de ejemplo original: esta entrega se revisa mediante
previews, sin incorporar navegación ni cambiar la entrada compartida del proyecto.

## Datos

- `FakePlayers.kt`: plantillas compartidas; se conserva el objeto `sofia` existente.
- `FakeMatchCenter.kt`: eventos, posiciones de cancha, estadísticas y momentum.
- `FakeOrganizer.kt`: resumen de jornada, actas pendientes y partido programado.
- Se reutilizan `TeamUiModel`, `PlayerUiModel` y `MatchUiModel` sin cambiar sus contratos.
- Los modelos adicionales representan únicamente conceptos que faltaban (eventos,
  formación, métricas, plantillas y actas pendientes).

## Verificación

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.leaguehub.app.MemberThreeScreensTest'
```

Las pruebas instrumentadas requieren un emulador o dispositivo conectado. Comprueban las
seis vistas, acceso por desplazamiento a sus acciones, bloqueo de procesamiento cuando no
hay documento y callbacks de procesamiento/reintento. Guardan capturas en el directorio
privado `files/member-three-previews` de la app durante la ejecución.

En el entorno Windows usado para verificar esta entrega, Java falló al crear sockets en
su carpeta temporal predeterminada. Se resolvió solo para el proceso de compilación con:

```powershell
$env:JAVA_TOOL_OPTIONS='-Djdk.net.unixdomain.tmpdir=C:\Windows\Temp'
```

No es un cambio requerido del proyecto ni se ha agregado a la configuración de Gradle.
