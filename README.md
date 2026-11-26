# FutView

App Android (Kotlin + Jetpack Compose) para seguir el fútbol chileno: fixture, resultados, tabla de posiciones, equipos, estadios con mapa y favoritos, con panel de administración.

## Funcionalidades

- **Sesión real** con Firebase Auth: registro con verificación de correo, login, recuperación de contraseña y cierre de sesión.
- **Datos reales de la Liga de Primera 2026** sincronizados desde ESPN (`site.api.espn.com`, liga `chi.1`): 16 clubes, 240 partidos con fecha/hora/estado/marcador y tabla oficial. Actualización automática cada 6 horas + botón manual en Admin → Equipos.
- **Cloud Firestore** para catálogos, favoritos por usuario (`users/{uid}/favorites`) y roles (`users/{uid}.role`), con reglas de seguridad (`firestore.rules`).
- **Mapas sin costo**: MapLibre + tiles vectoriales de OpenFreeMap (sin API key, permitido para apps en producción) y botón "Cómo llegar" vía Intent `geo:`.
- **Modo oscuro** persistente con switch en el menú de usuario.
- **Panel de administración** (roles Admin/Editor): clubes, estadios (con GeoPoint), fixture, acta de resultados y usuarios.
- Favoritos de equipos, partidos y estadios; resultados ordenados del más reciente; filtros por comuna/ciudad y búsqueda.

## Stack

| Componente | Valor |
|---|---|
| Lenguaje | Kotlin 2.2 |
| UI | Jetpack Compose (Material 3) |
| Backend | Firebase Auth + Cloud Firestore (BOM 35.2.0; `firebase-auth` fijada a 24.0.1) |
| Mapas | MapLibre 11.5 + OpenFreeMap |
| Datos de fútbol | ESPN (gratis, sin clave) |
| minSdk / targetSdk / compileSdk | 33 / 36 / 37 |

## Cómo compilar

```bash
./gradlew :app:assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Para correr los tests:

```bash
./gradlew :app:testDebugUnitTest
```

Requiere JDK 17+ (en este proyecto se usa el JBR de Android Studio: `JAVA_HOME=/opt/android-studio/jbr`).

## APK exportable (release con minify)

```bash
./gradlew :app:assembleRelease
```

Genera `app/build/outputs/apk/release/app-release.apk` (~47 MB, ofuscada con R8 y recursos reducidos), firmada con la clave debug para poder instalarla y compartirla directamente. Para Play Store habría que crear un keystore propio y, si se cambia el `applicationId`, registrar el package nuevo en Firebase.

Si publicas la release, conserva `app/build/outputs/mapping/release/mapping.txt`: es la tabla para des-ofuscar stack traces de R8.

## Configuración de Firebase

1. Proyecto: `futview-a23f1` (el `google-services.json` ya está en `app/`).
2. Reglas de seguridad en `firestore.rules`; desplegar con `firebase deploy --only firestore:rules`.
3. Lectura solo autenticada; escritura de catálogos solo Admin/Editor; favoritos solo del dueño.
4. Promoción a admin: editar manualmente `users/{uid}.role = "Admin"` en la consola de Firestore.

