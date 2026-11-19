# Contexto para continuar Firebase

## Estado y alcance

- Rama activa: `featureFirebase`, creada desde `FeatureFinal`. No fusionar en `main` hasta validar
  el proyecto real en dos teléfonos. Se conserva el diseño/MVI y toda la captura local.
- Proyecto activo del grupo: **LeagueHub**, ID `leaguehub-grupo-2026`, número `308063763054`.
  Android `com.leaguehub.app`, app ID `1:308063763054:android:1bc016e549d4b2d860847f`.
  `app/google-services.json` fue descargado oficialmente con Firebase CLI. Está ignorado en Git;
  en otra máquina se obtiene con `npm run config:android` desde `firebase/`, tras iniciar sesión
  con una cuenta con acceso al proyecto. No pedir una clave privada de servicio ni pegar claves
  de administrador en Android o en el repositorio.
- Authentication correo/contraseña habilitado; Firestore `(default)` Standard creado en
  `us-central1`, con nivel gratuito confirmado al crear la base. Reglas e índices publicados.
  `firebase/.firebaserc` apunta al proyecto del grupo y `firebase.json` incluye el proveedor
  `auth.providers.emailPassword`. `npm run deploy:backend` reproduce su despliegue.
- Se verificó el servicio real con siete comprobaciones SDK: registro/login desde dos clientes,
  lectura/escritura bidireccional, rechazo de revisión antigua, protección entre cuentas,
  cambio de contraseña y solicitud de recuperación. Los documentos y cuentas de prueba se
  retiraron. Aún falta validar Android en dos teléfonos y la recepción en un buzón real.
- Dependencias Android fijadas: Google Services 4.4.4 y Firebase BoM 34.7.0. Se eligió la BoM
  compatible con Kotlin 2.2.10 de este proyecto. La 35.0.1 descargada durante el desarrollo usa
  metadatos Kotlin 2.4 en Auth y no compila con este Kotlin. No ocultar ese error con flags para
  saltarse validaciones; actualizar Kotlin/KSP/Compose requeriría una tarea separada.

## Arquitectura y rutas de código

- `data/cloud/FirebaseGateway`: SDK de Auth y Firestore; los `Task` se esperan como operaciones
  suspendidas. Los emuladores solo se seleccionan en pruebas, nunca en la aplicación normal.
- `data/repository/AuthRepository`: proveedor cloud opcional. Los tests locales siguen usando el
  constructor de dos argumentos. El proveedor de la aplicación real se configura en
  `LeagueHubApplication`. Las cuentas ya existentes se vinculan sin reemplazar sus IDs ni ligas.
- `data/local/SyncEntities`, `SyncSchema`, `LeagueHubDatabase.MIGRATION_4_5`: tablas de vínculos,
  revisiones/cola, control de importación y copias de conflicto. Triggers de ligas, equipos,
  jugadores, partidos, eventos y `user_data` con clave `match-sheet:` marcan cambios locales.
  `SYNC_CALLBACK` instala/backfillea los triggers al abrir la base; incluirlo en bases de pruebas
  que deban sincronizar. No usar fallbackToDestructiveMigration.
- `CloudLeagueStore`: serializa un agregado por liga con DTO deportivos; valida como los respaldos.
  No exporta emails, claves, hashes/sales, sesiones ni fotos. Convierte los vínculos de jugadores a
  UID y los adapta al ID local al descargar. Aplica todo de forma transaccional, preservando las
  fotos locales si su partido sigue existiendo, y evita sobrescribir IDs de otra liga.
- `CloudSyncEngine`: escucha el servidor y descarta snapshots de caché como autoridad para reemplazar
  Room. Sube cambios pendientes con comparación de revisión y confirmación del servidor. Si hay
  cambios locales y una revisión remota nueva, guarda un conflicto. Un mutex serializa recepción,
  confirmaciones y resoluciones. Cambios hechos mientras se envía otra revisión quedan pendientes.
  Reintenta conexiones; no cambia el estado de la liga a sincronizado ante un fallo de envío.
- `feature/local/cloud`: pantalla MVI de diagnóstico, vinculación y resolución. Acceso desde Inicio
  o Perfil → Configuración. La contraseña del formulario es transitoria y se limpia tras operar.
- `feature/auth/recovery`: solicita a Firebase el enlace de cambio de contraseña, con respuesta
  genérica para no confirmar públicamente si una dirección tiene cuenta.

## Esquema remoto y seguridad

`users/{firebaseUid}` es privado: canonicalId=UID, nombre, modo, liga seleccionada, onboarding y
updatedAt. `leagues/{leagueId}` contiene ownerUid, ownerLocalId=UID, revision, payload, deleted y
updatedAt. El payload JSON lleva los registros deportivos relacionados. Solo usuarios registrados
pueden leer las ligas; solo el dueño escribe. No hay correos en un directorio público.

No borrar documentos de liga físicamente para una eliminación normal: debe escribirse la marca
`deleted` con revisión nueva. Firestore Rules impide el borrado físico y saltos/repeticiones de revisión.
Las reglas validan identidad, campos, tamaño y versión; la app valida el esquema/referencias del payload.
No son una validación de servidor de cada estadística deportiva dentro de JSON: para controles de
negocio entre clientes no confiables haría falta un backend o un modelo de documentos más granular.

Las copias de conflictos se conservan en `sync_recovery`. Cada una puede exportarse desde la
pantalla de sincronización como respaldo compatible, e importarse como una copia independiente
con los IDs remapeados. No subirlas a una cuenta distinta ni borrarlas como parte de un reintento.

## Continuación pendiente (prioridad)

1. En una computadora nueva, ejecutar `npm ci --include=dev`, `npx firebase login` y
   `npm run config:android` dentro de `firebase/`. El proyecto es `leaguehub-grupo-2026`.
   Auth, Firestore y reglas ya están configurados; `npm run deploy:backend` permite actualizarlos.
2. Exportar un respaldo de los datos antes de actualizar. Instalar el APK de la rama en el
   teléfono A manteniendo los datos existentes, conectar la cuenta
   desde la pantalla de sincronización y esperar al estado al día.
3. Entrar con la misma cuenta en B. Verificar liga, perfil/jugador vinculado, alineaciones, resultado
   y estadística. Probar también una cuenta distinta: puede leer deportes pero no editar ligas ajenas.
4. Desconectar B, editar en ambos, reconectar y comprobar conflicto. Exportar la copia y verificar
   que los cambios de la versión descartada pueden importarse en una liga independiente.
5. Verificar recepción del correo real de recuperación y completar el cambio desde su enlace.
   La petición al servicio de producción ya pasó; no se comprobó el buzón. Después actualizar
   este estado y considerar el merge de esta rama.

## Comandos de pruebas

Probado en este equipo con JDK 25, SDK 37, Node 22 y Pixel 8/API 37. No ejecutar dos Gradle builds
simultáneamente en Windows: los JAR de compilación pueden quedar bloqueados.

Terminal 1, desde `leaguehub/firebase`:

```powershell
npm ci --include=dev
npm run emulators
```

Terminal 2, pruebas de reglas (manteniendo esos emuladores abiertos):

```powershell
cd firebase
node --test rules.test.mjs
cd ..
```

Para Android conectar el emulador/teléfono y preparar los túneles al equipo:

```powershell
adb reverse tcp:9099 tcp:9099
adb reverse tcp:8080 tcp:8080
.\gradlew.bat testDebugUnitTest connectedDebugAndroidTest assembleDebug lintDebug "-Pandroid.testInstrumentationRunnerArguments.firebaseEmulator=true"
```

La prueba `FirebaseSynchronizationTest` usa dos FirebaseApp/SDK y dos Room independientes,
con Auth/Firestore emulados, en el proyecto ficticio `demo-leaguehub`. Comprueba migración de cuenta,
perfil vinculado en la segunda instalación, escritura/lectura bidireccional, cola que sobrevive a
reabrir la base sin red, conflicto sin sobrescritura, copia recuperable, reconexión y tombstone.
Sin el argumento `firebaseEmulator=true` se omite expresamente esa prueba; no inferir que la nube
fue probada si aparece como omitida.

`CloudLocalDurabilityTest` verifica migración desde el esquema v4 real exportado, triggers,
rollback de errores, aplicación atómica y conservación/exclusión de URI de fotos.
`CloudScreenTest` verifica que resolver un conflicto requiera una confirmación explícita.
`LeagueHubTestRunner` sustituye Application para desactivar la integración REAL durante los tests
regulares. No quitarlo para resolver una prueba: evitar crear usuarios o datos de prueba en producción.
El permiso de HTTP de debug se limita a localhost/127.0.0.1/10.0.2.2 para estos emuladores.

## Límites conocidos y trabajo futuro

- Antes de instalar la rama Firebase en el teléfono de entrega, exportar un respaldo y conservar
  el APK 1.1. El upgrade v4→v5 se prueba; un APK 1.1 antiguo no abre una base v5. No forzar downgrade
  ni borrar bases/cuentas para resolver un problema de conexión. El respaldo deportivo no contiene
  contraseñas ni fotos. Para un rollback completo de la instalación se necesita un plan de copia
  de la base anterior, además de preservar las imágenes, no solo cambiar de rama en Git.

- Payload deportivo máximo por liga: 750 KB para no rozar el límite de documento de Firestore.
  Ante exceso se muestra error y se conserva Room. Evolución: subcolecciones o temporadas.
- Galería local: falta Firebase Storage para compartir archivos; no subir URI ni imágenes base64
  a documentos Firestore como atajo.
- Apariencia, seguidos y lectura de avisos siguen siendo preferencias del dispositivo. No se ha
  diseñado una reconciliación entre preferencias editadas simultáneamente en teléfonos distintos.
- El buscador de cuentas por correo para vincular jugadores sigue siendo local. No hay un endpoint
  administrativo de búsqueda de usuarios desde Android. Diseñar acceso privado antes de ampliarlo.
- OCR, IA/predicciones y push fuera de alcance. No introducir pantallas que simulen esos servicios.
- No existe promesa de cero errores ni validación física de producción. Mantener los respaldos y
  reportar por separado lo probado con emuladores y lo comprobado contra Firebase real.

## Fuentes de referencia

- https://firebase.google.com/docs/android/setup
- https://firebase.google.com/docs/auth/android/password-auth
- https://firebase.google.com/docs/firestore/manage-data/transactions
- https://firebase.google.com/docs/firestore/manage-data/enable-offline
- https://firebase.google.com/docs/firestore/security/rules-conditions
- https://firebase.google.com/docs/emulator-suite/connect_and_prototype

## Nota de sesión para la siguiente IA

El cierre de Firebase se ejecuta mediante `UserSessionRepository.onLoggedOut`, conectado desde
`LeagueHubApplication`. No cerrar Firebase automáticamente cuando el motor observe una sesión
local vacía: durante un login, Auth completa su acceso antes de que Room guarde la sesión y eso
provocaría un cierre prematuro. La prueba de dos clientes inicia el motor B antes del login para
cubrir ese orden real de arranque.

## Validación histórica de la rama antes de conectar el proyecto del grupo

El 19 de noviembre de 2026 finalizaron correctamente 25 pruebas JVM, 34 instrumentadas Android
(sin omitidas) y 5 de reglas Firestore. `assembleDebug` y `lintDebug` correctos: 0 errores y
34 advertencias. El APK de aquella revisión se compiló con `FIREBASE_CONFIGURED=true`, versión
1.2-firebase, código 3. El APK actual y su hash están descritos en `APK/README.md`. También se verificó por separado que compila
sin el JSON, con Firebase desactivado. La captura `docs/capturas/firebase/sincronizacion.png`
corresponde a un estado de conflicto de prueba renderizado, no a producción.

Las pruebas Node de reglas limpian `demo-leaguehub`: ejecutarlas antes o después de las pruebas
Android, nunca a la vez contra el mismo proyecto emulado. No afectan `leaguehub-grupo-2026`.

Para continuar desde otra máquina: clonar y cambiar a `featureFirebase`, restaurar el JSON en
`app/`, instalar SDK/JDK y configurar `local.properties` localmente; después seguir la lista de
Console y probar dos teléfonos. Nunca solicitar credenciales privadas de administrador para Android.

## Verificación del proyecto del grupo · 19 de noviembre de 2026

- Proyecto final `leaguehub-grupo-2026`, nombre visible LeagueHub. El proyecto provisional
  descartado se marcó para eliminación; no tenía Firebase ni datos deportivos.
- `firebase deploy --only auth,firestore:rules,firestore:indexes` completado contra el proyecto real.
- 7 comprobaciones con SDK contra producción: permisos sin sesión, registro/login, intercambio
  bidireccional, revisiones, protección entre cuentas, contraseña y petición de recuperación.
  Se limpiaron solo los documentos y cuentas temporales creados por la comprobación.
- 25 pruebas JVM aprobadas, `assembleDebug`, `lintDebug` y `compileDebugAndroidTestKotlin`
  correctos. Lint: 0 errores y 33 advertencias. No se ejecutó instrumentación en esta computadora
  porque no había emulador/teléfono conectado.
- 5 pruebas de reglas en el emulador aprobadas antes del despliegue.
- APK 1.2.1-firebase, código 4, `FIREBASE_CONFIGURED=true`. Hash y archivo en `APK/`.
- Pendiente: flujo completo Android con dos teléfonos, conflicto/reconexión y lectura del correo
  real. Mantener separados estos pendientes de lo ya verificado con los SDK y emuladores.
