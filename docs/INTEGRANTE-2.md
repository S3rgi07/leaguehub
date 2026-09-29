# LeagueHub · Entrega del integrante 2

Implementación de las siete pantallas de Jugador / Competición en Jetpack Compose, sobre `main` (`9fb8c67`) y en la rama `feature/player-ui`.

## Pantallas

| Vista | Screen y Route | Referencia Figma |
|---|---|---|
| P01 · Inicio | `feature/home/HomeScreen.kt`, `HomeRoute.kt` | [P01](https://www.figma.com/design/IAhnhFIXe7V2smEV0zrRt6?node-id=1-723) |
| P06 · Tabla | `feature/league/standings/StandingsScreen.kt`, `StandingsRoute.kt` | [P06](https://www.figma.com/design/IAhnhFIXe7V2smEV0zrRt6?node-id=1-1220) |
| P07 · Perfil | `feature/profile/ProfileScreen.kt`, `ProfileRoute.kt` | [P07](https://www.figma.com/design/IAhnhFIXe7V2smEV0zrRt6?node-id=1-1359) |
| P08 · Calendario | `feature/matches/calendar/CalendarScreen.kt`, `CalendarRoute.kt` | [P08](https://www.figma.com/design/IAhnhFIXe7V2smEV0zrRt6?node-id=1-1466) |
| P09 · Partido y mapa | `feature/matches/detail/MatchDetailScreen.kt`, `MatchDetailRoute.kt` | [P09](https://www.figma.com/design/IAhnhFIXe7V2smEV0zrRt6?node-id=1-1585) |
| P10 · Liga | `feature/league/detail/LeagueDetailScreen.kt`, `LeagueDetailRoute.kt` | [P10](https://www.figma.com/design/IAhnhFIXe7V2smEV0zrRt6?node-id=1-1694) |
| P11 · Equipo | `feature/team/detail/TeamDetailScreen.kt`, `TeamDetailRoute.kt` | [P11](https://www.figma.com/design/IAhnhFIXe7V2smEV0zrRt6?node-id=1-1806) |

Las rutas anteriores son relativas a `app/src/main/java/com/leaguehub/app/`.

## Cómo revisar

Abrir el proyecto en Android Studio, sincronizar Gradle y ejecutar `app`. `MainActivity` abre `PlayerApp`, un host de demostración del bloque del integrante 2. Las pantallas de Setup/Auth existentes siguen disponibles en sus packages. Al integrar la navegación global, llamar a estas Routes desde el grafo compartido y sustituir el host de demostración.

- La barra inferior abre Inicio, Tabla, Partidos y Perfil.
- En Inicio, tocar el nombre de la liga abre P10; el avatar abre P07; el próximo partido y los resultados abren P09.
- General/Local/Visitante cambian los datos de P06; tocar un equipo abre P11.
- P08 permite cambiar de mes, seleccionar un día, volver al mes completo y filtrar por el equipo del jugador.
- P09 abre la búsqueda o las indicaciones en mapas mediante un Intent; compartir abre el selector de Android.
- P10 permite consultar todos los equipos y leer la noticia de muestra.
- P11 permite seguir/dejar de seguir, expandir los 22 jugadores de la plantilla y abrir sus perfiles.

Cada Screen contiene un Preview de 390 × 844 dp bajo `LeagueHubTheme`. Inicio, Perfil, Calendario y Equipo incluyen estados vacíos; Tabla tiene un Preview local y Partido uno finalizado.

## Convenciones y datos

Las Screens reciben modelos, estado y callbacks por parámetros. Los fixtures se usan en las Routes y los Previews. No se accede a API, Room, ViewModel ni NavController desde una Screen.

Se reutilizan `LeagueHubCard` y `MatchScoreboard`. Se amplió la card con padding opcional, manteniendo su valor predeterminado. El marcador admite una cuenta regresiva opcional y conserva sus estados programado, en vivo y finalizado. Los elementos que usan varias features viven en `core/components`; los colores, tipografías y shapes estándar vienen del tema.

Se ampliaron los modelos existentes con propiedades opcionales o valores predeterminados, conservando sus constructores anteriores. Los conceptos nuevos son únicamente información de cancha, previa y noticia. Los colores de equipos permanecen en `FakeTeams`, como datos del equipo.

El calendario usa `LocalDate` y `YearMonth`, no interpreta los rótulos visibles de fecha. La fecha de la demo es el 9 de septiembre de 2026. `upcomingFalconsVsTitans` representa el estado programado de P01/P08/P09; el fixture original `falconsVsTitans` sigue en vivo para Match Center.

Los iconos y las ilustraciones de avatar, liga, noticia y mapa proceden de las capas de Figma, rasterizadas localmente a 3× en `res/drawable-nodpi`. No se incorporan capturas de pantalla de Figma ni URLs temporales en la app. Los equipos se representan con su color y abreviatura, según la guía compartida. Los iconos adoptan el color del tema para mantener contraste sobre fondo oscuro. Las barras del sistema son las nativas de Android.

## Alcance de la entrega

Los datos, valoraciones, noticias, previa y cuenta regresiva son de demostración. La ruta dibujada en P09 es ilustrativa: tiempo y distancia no se calculan en vivo. Los botones sí abren mapas externos. No se agregó backend, autenticación, permisos de ubicación, generación IA, edición de perfil ni las pantallas del integrante 1 o 3. Las opciones de perfil y alertas muestran estados informativos dentro del alcance de UI.

## Verificación

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

Se usa la configuración original del repositorio: JDK 25, SDK 37, Gradle 9.5.0 y Android Gradle Plugin 9.3.3. No se cambiaron las dependencias.

`PlayerCalendarTest` cubre alineación semanal, días únicos, febrero bisiesto, meses de seis semanas y filtrado por año/mes. `PlayerFlowTest` recorre las siete vistas, verifica filtros y calendario vacío, y guarda capturas en los resultados adicionales de instrumentación. Las capturas se compararon visualmente con Figma. Lint puede mostrar avisos previos sobre versiones y recursos del proyecto; no se actualizan dependencias ajenas a esta entrega.

Resultado de la ejecución final: `assembleDebug`, `testDebugUnitTest`, `lintDebug` y `connectedDebugAndroidTest` completados correctamente. Cinco pruebas unitarias y tres instrumentadas aprobadas. Lint: cero errores y catorce advertencias sobre configuración, versiones y recursos que ya existían. Revisión visual realizada a 390 × 844 dp en el emulador Pixel 10 Pro XL, Android 17.
