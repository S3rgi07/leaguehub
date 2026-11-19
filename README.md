# LeagueHub

Aplicación Android local en Jetpack Compose para administrar ligas, equipos, jugadores,
partidos, resultados y estadísticas. El flujo principal usa datos de Room/SQLite; no requiere
servidor, conexión a Internet ni una cuenta externa.

## Ejecutar

1. Abrir esta carpeta (`leaguehub`) en Android Studio.
2. Usar JDK 25 y Android SDK 37. Ajustar `local.properties` a la ruta del SDK de cada máquina.
3. Ejecutar `app` en un dispositivo Android 8.0/API 26 o posterior.

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

La última tarea requiere emulador o teléfono conectado. El APK de entrega es
`APK/LeagueHub-debug.apk`; el APK que genera Gradle está en
`app/build/outputs/apk/debug/app-debug.apk`.

## Recorrido funcional local

1. Crear una cuenta con nombre, correo y contraseña. Elegir Organizador o Jugador.
2. Como organizador: Ligas → Crear liga → nombre y temporada.
3. Abrir la liga; Equipos → crear al menos dos equipos.
4. Jugadores → crear la plantilla, indicando equipo, nombre, posición y dorsal (1–99).
   El dorsal no se repite en el mismo equipo.
5. Para vincular un jugador con un perfil, registrar primero su cuenta en este teléfono e
   ingresar su correo en el formulario del jugador. Cada cuenta se vincula a un solo jugador.
   Dejar el correo vacío permite crear jugadores sin cuenta o quitar una vinculación.
6. Partidos → crear partido con equipos distintos de la liga, fecha, hora, cancha y jornada.
7. Ver eventos → agregar o editar goles, asistencias, tarjetas y participaciones. Una
   participación permite guardar minutos jugados y valoración opcional de 0 a 10.
8. Registrar resultado → ingresar ambos marcadores. Se puede corregir el resultado o volver
   el partido a Programado. El marcador no puede ser menor que los goles ya registrados.
9. Tabla refleja los resultados: victoria 3 puntos, empate 1. Desempates por diferencia de
   goles, goles a favor y nombre. Perfil y Jugadores calculan estadísticas desde los eventos
   de partidos con resultado. Corregir, reabrir o eliminar partidos recalcula los totales.
10. Perfil permite editar el nombre de la cuenta, cambiar contraseña verificando la actual,
    cambiar apariencia y modo, y cerrar sesión. Las contraseñas no se muestran ni se guardan
    en texto plano. Para volver a entrar se usa el correo y la contraseña registrados.

Las ligas del dispositivo se pueden consultar desde cualquier cuenta. Solo su creador, en modo
Organizador, puede modificarlas. Cambiar a organizador no permite editar ligas ajenas.

La eliminación solicita confirmación. Para borrar ligas o equipos se deben quitar primero
sus dependencias. Un jugador con eventos no se puede eliminar ni trasladar a otro equipo.
Eliminar un partido elimina también sus eventos y actualiza las estadísticas.

## Respaldos locales

En Perfil, en modo Organizador, están **Exportar respaldo** e **Importar respaldo**.
El selector de archivos de Android permite elegir el destino o el archivo sin permisos generales
de almacenamiento.

- Exportar guarda en JSON todas las ligas creadas por la cuenta activa y sus equipos, jugadores,
  partidos y eventos. La lectura se hace en una transacción para obtener una copia consistente.
- Importar valida formato, versión, campos obligatorios, identificadores, relaciones, fechas,
  dorsales, marcadores, eventos y valoraciones. El límite es 8 MB y 50 000 registros.
- Antes de guardar aparece una vista previa con las cantidades y las ligas incluidas. Cancelar
  no cambia la base. Un respaldo vacío se puede consultar, pero no tiene copias que importar.
- Confirmar crea ligas con el sufijo `(copia)`, nuevos identificadores y el propietario actual.
  No sobrescribe ni fusiona registros existentes. Importar nuevamente crea otras copias.
- No se exportan cuentas, correos de acceso, contraseñas, sesiones, preferencias ni vínculos de
  jugadores con cuentas. Los jugadores importados se pueden vincular después desde Jugadores.
- Tabla y estadísticas se recalculan desde los resultados y eventos restaurados. Todo el guardado
  usa una única transacción: si falla cualquier registro, no se conserva ninguna parte de esa importación.

El formato es `leaguehub-backup`, versión `1`, con fecha ISO y arreglos `leagues`, `clubs`,
`players`, `fixtures` y `events`. El archivo es UTF-8 y contiene nombres y datos deportivos
legibles, sin cifrado. Para trasladar una liga a otro teléfono, crear primero una cuenta allí,
elegir modo Organizador e importar el archivo.

## Persistencia y arquitectura

Room v4 almacena la base privada `leaguehub.db` en la ruta de
`context.getDatabasePath("leaguehub.db")`, habitualmente
`/data/user/0/com.leaguehub.app/databases/leaguehub.db`.

| Tabla | Datos |
|---|---|
| `user_accounts` | Cuentas, hash PBKDF2 y sal aleatoria, preferencias de rol/liga |
| `user_session` | Sesión activa |
| `user_data` | Preferencias locales por cuenta; también conserva snapshots históricos |
| `leagues` | Liga, temporada y propietario |
| `clubs` | Equipos relacionados con su liga |
| `players` | Plantilla y vínculo opcional con una cuenta |
| `fixtures` | Partidos y sus resultados |
| `match_events` | Eventos, participaciones, minutos y valoraciones |

Las migraciones 1→2→3→4 conservan cuentas y datos estructurados existentes. Las antiguas
sesiones de demostración que no tenían cuenta se cierran al arrancar y se solicita crear
una cuenta local. Los catálogos falsos anteriores nunca fueron ligas guardadas y no se
convierten en registros reales.

`feature/local` contiene el ViewModel, estado, formularios y pantalla principal.
`CompetitionRepository` valida propiedad y relaciones dentro de transacciones. Las listas
observan Room mediante Flow; la tabla y los totales se derivan de los registros actuales.
La navegación real entra por `LocalHubDestination`. Los antiguos screens y fixtures se
conservan como referencias de diseño y para pruebas aisladas, fuera del recorrido principal.
Los documentos `docs/INTEGRANTE-2.md` y `docs/integrante-3.md` describen entregas históricas.

## Alcance de esta versión

Todo el recorrido anterior funciona en el mismo teléfono. GitHub guarda el código, no la base
de datos del dispositivo. Las cuentas, ligas y cambios no se sincronizan entre teléfonos.
La recuperación por correo y los accesos Google/Apple requieren servicios externos y no forman
parte de esta versión local. La pantalla de recuperación lo explica sin simular un envío.
Cámara/OCR, predicciones, avisos programados y publicación en Internet quedan fuera del flujo
local; no se presentan como acciones disponibles. Compartir un partido abre el selector de Android. Abrir ubicación delega en una aplicación de mapas instalada (su disponibilidad sin conexión depende de esa aplicación). Desde Equipos se puede seguir/dejar de seguir equipos; la selección se conserva por cuenta y aparece en Perfil.

## Continuar el desarrollo

La rama de trabajo es `FeatureFinal`. Recuperar con `git fetch origin`, `git switch FeatureFinal`
y `git pull --ff-only`. Se hacen commits cortos en español y se suben al terminar bloques validados.
Conservar los cambios personales de Android Studio (`.idea`) que estén fuera de esos commits.

La cobertura incluye cálculos y correcciones de tabla, estadísticas, claves foráneas, propiedad,
reapertura de la base, migraciones, cuentas, vinculación y el recorrido visual de formularios y
registro → contraseña → salir → entrar → recrear actividad. No equivale a una garantía sobre
cada modelo de teléfono: las pruebas instrumentadas se ejecutan en el emulador disponible.

## Verificación de la entrega local

Compilación y lint completados sin errores. Pasaron 20 pruebas unitarias y 22 pruebas
instrumentadas en el emulador disponible, incluyendo registro/login, cambio de contraseña,
recreación de actividad, captura por formularios, edición/eliminación, protección de dependencias,
modo de lectura, reapertura de datos y migraciones. El APK de `APK/LeagueHub-debug.apk` se
actualizó desde la compilación comprobada y se verificó su coincidencia por SHA-256.
