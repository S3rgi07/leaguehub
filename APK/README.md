# APK de entrega Firebase

Instalar `LeagueHub-debug.apk`: versión **1.2.1-firebase** (código 4), rama `featureFirebase`.
Conserva la interfaz y los flujos locales de `FeatureFinal` e incorpora Auth y sincronización
Firestore. `LeagueHub-debug (2).apk` corresponde a una entrega anterior.

Antes de actualizar, exportar un respaldo desde la versión local. La migración Room v4 → v5
conserva los datos; el APK antiguo 1.1 no abre una base v5. No borrar los datos para configurar Firebase.

Authentication por correo/contraseña y Firestore ya están configurados, con reglas publicadas.
Se verificaron siete escenarios con clientes SDK contra producción. Falta recorrer la app en
dos teléfonos y comprobar la llegada del correo de recuperación a un buzón real.

El APK incluye la configuración Android de **LeagueHub**, proyecto **`leaguehub-grupo-2026`**,
con `FIREBASE_CONFIGURED=true`. El JSON está ignorado en Git: en otra computadora, ejecutar
`npm ci --include=dev`, `npx firebase login` y `npm run config:android` desde `firebase/` con una
cuenta con acceso al proyecto. Sin el JSON se compila únicamente con funcionamiento local.

Origen: `app/build/outputs/apk/debug/app-debug.apk`, firma de depuración del equipo actual.
Para actualizar una instalación debe coincidir la firma; conservar la clave de desarrollo original
si se pretende volver a compilar desde otro equipo sin reinstalar.

SHA-256: `936bd89ea1167a6925ee8e7fbb89f325cfc2c2bae73fe240a946ebd4f09f8bcd`
