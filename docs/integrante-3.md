# Integrante 3 · Match Center y Organizador

Rama: `feature/organizer-match-mvi`.

## Alcance completado

- P02–P05 comparten `MatchCenterViewModel`, `MatchCenterState` y `MatchCenterEvent`.
- O01–O07 cuentan con `Screen`, `Route`, `ViewModel`, `State` y `Event`.
- Todos los destinos se registran con Navigation Compose type-safe.
- El flujo de acta conecta Escanear → OCR → Validar → Resultado publicado.
- El control en vivo se abre desde el panel o desde la gestión del partido cuando las
  plantillas están completas.
- `MatchWorkflowRepository` comparte marcador, eventos, OCR y publicación entre las pantallas.
- El resultado publicado permite abrir Match Center o volver al panel.

## Flujo navegable

```text
Panel
├── Gestión de partido ──> Control en vivo ──> Resultado publicado
└── Escanear acta ──> Revisar OCR ──> Validar eventos ──> Resultado publicado
                                                    └──> Match Center
```

Match Center también se abre desde el detalle de partido del jugador. Sus cuatro pestañas
(resumen, alineaciones, estadísticas y galería) conservan la selección en el ViewModel.

## Verificación

```powershell
$env:JAVA_TOOL_OPTIONS='-Djdk.net.unixdomain.tmpdir=C:\Windows\Temp'
$env:ANDROID_HOME='C:\Users\PC RYZEN 5\AppData\Local\Android\Sdk'
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
.\gradlew.bat connectedDebugAndroidTest
```

La última tarea requiere un emulador o teléfono conectado por ADB.
