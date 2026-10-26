# LeagueHub · Entrega del integrante 2

Rama: `feature/player-mvi`. Base: `main` en `e698c36`.

## Alcance implementado

Se migró todo el bloque Jugador a MVI y navegación type-safe con Navigation Compose.

| Vista | Feature |
|---|---|
| P01 · Inicio | `feature/home` |
| P06 · Tabla de posiciones | `feature/league/standings` |
| P07 · Perfil de jugador | `feature/profile` |
| P08 · Calendario | `feature/matches/calendar` |
| P09 · Detalle de partido | `feature/matches/detail` |
| P10 · Detalle de liga | `feature/league/detail` |
| P11 · Detalle de equipo | `feature/team/detail` |
| P12 · Notificaciones | `feature/notifications` |
| P13 · Configuración | `feature/settings` |
| P14 · Explorar | `feature/explore` |

Cada feature conserva su `Screen` sin lógica de negocio y ahora cuenta con `Route`,
`ViewModel`, `State` y `Event`. Los Routes observan `StateFlow` con
`collectAsStateWithLifecycle()` y envían las acciones al ViewModel.

## Navegación

`PlayerNavigation.kt` registra las diez vistas con destinos `@Serializable`. Los detalles
recuperan sus identificadores con `toRoute<T>()`; no se usan rutas de texto. La barra inferior
navega a Inicio, Tabla, Calendario y Perfil con restauración de estado y `launchSingleTop`.

El flujo incluye:

- Inicio → partido, liga y notificaciones.
- Tabla → equipo o liga.
- Calendario → partido.
- Liga → equipo, noticia y notificaciones.
- Equipo → jugador, partido, compartir y seguir/dejar de seguir.
- Perfil → equipo y configuración.
- Configuración → perfil, notificaciones, explorar, organizador y cerrar sesión.
- Explorar → liga, equipo o jugador según el resultado seleccionado.

`PlayerApp` ya no mantiene un back stack manual. Es un `NavHost` independiente para pruebas
y reutiliza el mismo grafo que `LeagueHubNavHost`.

## Estado compartido

`FollowedTeamsRepository` es la fuente de verdad de equipos seguidos y vive en
`LeagueHubApplication`, igual que `UserSessionRepository`. De esta forma P11 y P13 observan
la misma información durante la sesión y el estado ya no depende de `rememberSaveable`.

Los ViewModels administran filtros de tabla y calendario, fecha seleccionada, pestaña y noticia
de liga, expansión de plantilla, categorías y lectura de notificaciones, preferencias, búsqueda
y categorías de Explorar.

## Verificación

Comandos ejecutados:

```powershell
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug :app:lintDebug
```

Resultado: compilación y APK correctos, 10 pruebas unitarias aprobadas y lint sin errores
(18 advertencias no bloqueantes). `PlayerMviTest` cubre filtros, selección, notificaciones,
búsqueda y el repositorio compartido. `PlayerFlowTest` conserva el recorrido instrumentado
de las pantallas; no se ejecutó en esta verificación porque no había emulador o dispositivo
conectado.

APK generado: `APK/LeagueHub-debug.apk`.

## Límites coordinados con el equipo

Room y la llamada HTTP son requisitos globales indicados en la guía, pero no pertenecen al
listado de funcionalidades asignadas específicamente al integrante 2. Esta rama no añade una
segunda implementación de datos para evitar conflictos con la integración que el equipo debe
coordinar. Los datos del bloque Jugador continúan usando los fixtures compartidos.
