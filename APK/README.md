# APK de entrega Firebase

Instalar `LeagueHub-debug.apk`: versión **1.2-firebase** (código 3), rama `featureFirebase`.
Conserva la interfaz y los flujos locales de `FeatureFinal` e incorpora Auth y sincronización
Firestore. `LeagueHub-debug (2).apk` corresponde a una entrega anterior.

Antes de actualizar, exportar un respaldo desde la versión local. La migración Room v4 → v5
conserva los datos; el APK antiguo 1.1 no abre una base v5. No borrar los datos para configurar Firebase.

Para activar sincronización faltan los pasos de Firebase Console detallados al inicio del
[README principal](../README.md) y en [el contexto para continuar](../docs/FIREBASE_CONTINUAR.md).
Los servicios de producción y la prueba física en dos teléfonos siguen pendientes.

El APK incluye la configuración Android suministrada para `leaguehub-ffd96`. Ese JSON está
ignorado en Git: en otra máquina hay que descargarlo del mismo proyecto y colocarlo en `app/`
antes de compilar. Sin él se compila únicamente con funcionamiento local.

Origen: `app/build/outputs/apk/debug/app-debug.apk`, firma de depuración del equipo actual.
Para actualizar una instalación debe coincidir la firma; conservar la clave de desarrollo original
si se pretende volver a compilar desde otro equipo sin reinstalar.

SHA-256: `79459fb7e5858664412b1fc208dd736839360c53ffb3dda77140e78d59600c85`
