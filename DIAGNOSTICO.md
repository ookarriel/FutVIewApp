# Diagnóstico del proyecto FutView

> Fecha del diagnóstico: 24 de noviembre de 2026
> Este documento es la guía de trabajo del proyecto: describe el estado actual, lo que falta y el plan para terminarlo. Se debe ir actualizando a medida que se completan tareas.

---

## 1. Resumen ejecutivo

FutView es una app Android en **Kotlin + Jetpack Compose** para consultar información del fútbol chileno (equipos, estadios, partidos, resultados, favoritos) con un panel de administración.

**Estado actual: app funcional con datos reales.** La interfaz está construida en Jetpack Compose y conectada a Firebase Auth/Firestore, ESPN y MapLibre:

- ✅ **Fase 0 completada**: los datos hardcodeados (`FutViewData`) fueron eliminados; las pantallas leen datos reales o muestran estados vacíos/error.
- ✅ **Firebase funcional**: autenticación, sesión persistente, roles, catálogos, favoritos y reglas de seguridad.
- ✅ **Mapas gratuitos**: MapLibre + OpenFreeMap, sin API key ni tarjeta.
- ✅ **Datos de fútbol reales**: ESPN alimenta partidos, resultados y posiciones de la temporada 2026.

---

## 2. Stack y configuración actual

| Componente | Valor | Observación |
|---|---|---|
| Lenguaje | Kotlin 2.2.10 | |
| UI | Jetpack Compose (BOM 2025.12.00, Material 3) | Con `material-icons-extended` y `material3-adaptive-navigation-suite` |
| AGP | 9.2.1 | Kotlin integrado en AGP (no hay plugin `kotlin-android` separado) |
| minSdk / targetSdk / compileSdk | 33 / 36 / 37 | |
| applicationId | `com.example.futview` | ⚠️ Cambiar antes de publicar |
| Firebase | `google-services.json` (proyecto `futview-a23f1`, package correcto) + plugin `com.google.gms.google-services` 4.6.0 + BOM 35.2.0 | ✅ Auth, Firestore, favoritos, roles y catálogos conectados |
| Mapas | MapLibre 11.5 + OpenFreeMap | ✅ Gratuito, sin API key ni tarjeta |
| Navegación | Enum manual + `BackHandler` (`navigation/FutViewApp.kt`) | ⚠️ Funcional; evaluar migrar a Navigation Compose |
| Tests | Solo plantilla (`ExampleUnitTest`, `ExampleInstrumentedTest`) | ❌ Sin tests reales |

---

## 3. Lo que ya existe

### UI de usuario (conectada a datos reales)
- **Login / Registro / Recuperar contraseña / Verificar email / Nueva contraseña** (`LoginScreen.kt`, `AuthScreens.kt`): autenticación real con Firebase Auth, verificación de correo y recuperación.
- **Partidos** (`MatchesScreen`, `MatchDetailScreen`): fixture agrupado por fecha, detalle con botón a estadio.
- **Resultados** (`ResultsScreen`, `ResultDetailScreen`): marcadores finalizados y tabla oficial desde ESPN.
- **Equipos** (`TeamsScreen`, `TeamDetailScreen`): lista y detalle con próximo partido y estadio.
- **Estadios** (`StadiumsScreen`, `StadiumDetailScreen`): lista y detalle con dirección, aforo y mapa MapLibre.
- **Favoritos** (`FavoritesScreen`): equipos, partidos y estadios favoritos persistidos por usuario en Firestore.
- **Menú de usuario** (`UserMenuScreen.kt`): usuario autenticado real, switch persistente de modo oscuro, administración/estado para Admin/Editor y cierre de sesión. Sin botones de perfil, cuenta, configuración, notificaciones, ayuda o contacto.

### UI de administración
- Panel admin con 5 pestañas (`AdminHubScreen`, `admin/*.kt`): formularios reales de Equipos, Estadios (con campos de ubicación GPS), Partidos/Fixture, Resultados (Acta) y Usuarios + invitación de usuarios y confirmación de borrado. Todos persisten en Firestore.

### Modelos de datos (`data/Models.kt`)
- `Team`, `Stadium`, `Match`, `StandingRow`, `SystemUser`, `FixtureDay`, `MatchStatus`.
- ⚠️ `Stadium.latitude` y `longitude` son `String`. Para Firestore + Maps conviene usar `GeoPoint` (o `Double`).
- ℹ️ El objeto demo `FutViewData` fue eliminado en la Fase 0; las pantallas leen datos reales desde Firestore/ESPN.

### Arquitectura (creada en la Fase 1)
- **`data/repository/`**: contratos e implementaciones Firestore para `TeamRepository`, `StadiumRepository`, `MatchRepository`, `UserRepository` y favoritos; `AuthRepository` + `FirebaseAuthRepository`; `EspnSyncRepository` para datos de fútbol. Todos conectados en `AppContainer`.
- **`data/model/FirestoreDtos.kt`**: DTOs de Firestore (`TeamDoc`, `StadiumDoc` con `GeoPoint`, `MatchDoc`, `StandingDoc`, `UserDoc`) con mapeadores a los modelos de dominio.
- **`data/AppContainer.kt`**: inyección manual y punto único de construcción de repositorios/servicios.
- **`ui/viewmodel/`**: `UiState` (Loading/Success/Error) + ViewModels de las 5 pestañas (Partidos, Resultados, Equipos, Estadios, Favoritos) y de los formularios admin (Equipos, Estadios, Usuarios, Partidos/Fixture, Acta), todos con reintento. **Fase 2**: `AuthStateViewModel` (sesión global) y ViewModels de Login, Registro, Recuperar, Nueva contraseña y Reenvío de enlace.
- **Estados de carga/error en UI**: `LoadingState` (carga) y `FirestoreErrorBanner` (aviso pasivo de problema con la base de datos, con reintento) integrados en todas las pantallas que leen datos; `ConnectionErrorScreen` se reserva para un corte total de conexión.
- **Sesión y rutas (Fase 2)**: splash mientras se resuelve la sesión; rutas de acceso (login/registro/recuperar) solo sin sesión; el resto de la app solo con sesión; acceso a Administración filtrado por rol.

### Configuración
- ✅ `app/google-services.json` presente y con el package correcto.
- ✅ Tema, colores y tipografías propios (`ui/theme/`).
- ✅ Componentes reutilizables (`ui/components/FutViewComponents.kt`) con paleta clara/oscura y cabeceras que muestran el usuario autenticado.

---

## 4. Lo que falta (brechas principales)

### A. Firebase Authentication — ✅ Implementado en Fase 2 (salvo reglas)
- [x] Dependencias: `firebase-auth` (fijada a 24.0.1: la 25 del BOM requiere Kotlin 2.4).
- [x] `FirebaseAuth` real en Login/SignUp/Recuperar/Logout.
- [x] Sesión persistente: arranca en HOME si hay sesión (`AuthStateListener` → `AuthStateViewModel`), splash de carga mientras se resuelve la sesión.
- [x] Manejo de errores de auth traducidos a español (`authErrorMessage`) y mostrados en la UI.
- [x] Enviar verificación de email (al registrarse y reenviable desde "Revisa tu correo") y recuperación de contraseña real.
- [x] Colección `users` con rol: se crea al registrarse (`role: "Usuario"`) y se lee vía `FirestoreUserRepository`.
- [ ] Reglas de seguridad basadas en roles en la consola de Firestore (Fase 3). Promoción a Admin: editar `users/{uid}.role = "Admin"` manualmente en la consola.

### B. Cloud Firestore — ✅ Implementado en Fase 3
- [x] Dependencias: `firebase-firestore` (agregada en Fase 1 para el mapeo con `GeoPoint`).
- [x] Definir el modelo de colecciones:
  - `teams` (nombre, corto, ciudad, `stadiumId` + `stadiumName` denormalizado)
  - `stadiums` (nombre, comuna, dirección, capacidad, `location: GeoPoint`, equipo local)
  - `matches` (dayLabel/shortDate/matchday, hora, estado, goles, ids de equipos, nombre de estadio)
  - `standings/2026` → tabla oficial de ESPN
  - `users` (uid, nombre, correo, rol)
  - `users/{uid}/favorites` (favoritos por usuario)
  - `invites` (invitaciones al panel, pendientes de alta real)
- [x] **Permiso `android.permission.INTERNET` en el Manifest** (agregado en Fase 0).
- [x] Capa de repositorios completa: `FirestoreTeamRepository`, `FirestoreStadiumRepository`, `FirestoreMatchRepository` (con CRUD + tabla de posiciones calculada) y `FirestoreUserRepository`; conectados en `AppContainer` (los stubs se eliminaron).
- [x] ViewModels por pantalla con `StateFlow` (hecho en Fase 1) + `FirestoreErrorBanner` pasivo en errores.
- [x] GeoPoint en el DTO de estadios (`StadiumDoc.location`); el modelo de UI mantiene lat/long como texto para mostrar.
- [x] Reglas de seguridad: `firestore.rules` + `firebase.json` (lectura autenticada; escritura de catálogos solo Admin/Editor; rol solo Admin; favoritos solo dueño).
- [x] Índices compuestos: no se requieren con las consultas actuales (colecciones completas ordenadas en cliente).
- [x] Offline: persistencia de Firestore activada en `FutViewApplication` (caché de 64 MB).
- [ ] **Desplegar y probar las reglas** (requiere Firebase CLI): `firebase deploy --only firestore:rules`.

### C. Mapas y ubicación de estadios — ✅ Implementado (MapLibre + OpenFreeMap)
- [x] Dependencia MapLibre 11.5 para Compose.
- [x] Tiles vectoriales de OpenFreeMap: gratuito, ilimitado, sin API key ni tarjeta.
- [x] Mapa embebido en `StadiumDetailScreen` con marcador del estadio (coordenadas reales; si no tiene, se oculta).
- [x] Botón "Cómo llegar en mapas" con Intent `geo:`.
- [x] Sin permisos de ubicación (no se usa geolocalización).

### D. Arquitectura y calidad — ⚠️ Base implementada; faltan tests y release
- [x] Estructura de capas: `data/repository`, `data/model`, `data/AppContainer`, `ui/viewmodel`.
- [x] ViewModels con `viewModelScope`, estados UI (`UiState`) y aviso pasivo de error (`FirestoreErrorBanner`).
- [x] Inyección manual mediante `AppContainer`.
- [x] Favoritos persistidos por usuario en Firestore.
- [x] Panel admin conectado: crear/editar/eliminar documentos reales en Firestore.
- [x] Validación y feedback en formularios admin.
- [ ] Tests: unitarios para repos/VM y smoke tests de Compose.
- [ ] R8/minify y reglas ProGuard para release.

### E. Deudas y detalles detectados (menores pero concretos)
1. ✅ ~~`app/build.gradle.kts` línea 45: `8` suelto con espacios basura~~ — eliminado en Fase 0 (commit `f361685`).
2. ✅ ~~El manifest no declara `INTERNET`~~ — agregado en Fase 0 (commit `f361685`).
3. ✅ ~~`README.md` está casi vacío y con typo ("FutVIewApp")~~ — reescrito en Fase 5.
4. ⚠️ `applicationId` con namespace `com.example.*` — cambiar para publicación.
5. ⚠️ Navegación con enums y estado en `rememberSaveable`: no sobrevive a cambios de configuración complejos ni soporta deep links; migrar a Navigation Compose cuando se conecte la lógica.
6. ✅ ~~Datos de ejemplo con fechas de 2027 y solo 4 equipos~~ — dataset demo eliminado en Fase 0; al conectar Firestore se define el dataset real (primera división chilena completa).
7. ⚠️ `firebase-bom:35.2.0` está algo desactualizada; sincronizar versión al agregar dependencias.

---

## 5. Plan de trabajo propuesto (fases)

### Fase 0 — Limpieza (inmediato) ✅ COMPLETADA
- [x] Eliminar el `8` basura de `build.gradle.kts` y hacer commit.
- [x] Agregar permiso `INTERNET` al manifest.
- [x] Actualizar este documento con el avance.
- [x] Eliminar los datos hardcodeados: borrado `FutViewData` (modelos conservados en `data/Models.kt`), pantallas con listas vacías + `TODO Fase 3`, credenciales/favoritos/notificaciones demo eliminadas, textos demo de admin neutralizados. Compilación verificada (`:app:compileDebugKotlin` OK).

### Fase 1 — Arquitectura base ✅ COMPLETADA
- [x] Crear estructura `data/repository`, `ui/viewmodel` (+ `data/model`, `data/AppContainer`).
- [x] Definir modelos Firestore (DTOs `TeamDoc`, `StadiumDoc` con `GeoPoint`, `MatchDoc`, `StandingDoc`, `UserDoc` con mapeadores a dominio).
- [x] ViewModels de pantallas principales leyendo desde un repositorio (stubs vacíos temporales; la forma final ya está conectada).
- [x] Estados de carga/error en UI (`UiState` + `LoadingState` en todas las pantallas con datos).
- [x] Aviso pasivo de error de Firebase: `FirestoreErrorBanner` integrado en las 10 pantallas con datos — informa que hay un problema con la base de datos y permite reintentar sin bloquear la pantalla. `ConnectionErrorScreen` queda como pantalla completa para el caso de corte total.

### Fase 2 — Firebase Auth ✅ COMPLETADA
- [x] Dependencia `firebase-auth` + login real email/contraseña (`FirebaseAuthRepository`, errores traducidos a español, botón con estado de carga).
- [x] Registro real + verificación de email + recuperación de contraseña por correo (`CheckEmailScreen` reenvía el enlace).
- [x] Persistencia de sesión (`AuthStateListener`; arranque en HOME con sesión activa y splash mientras resuelve) y logout real (menú y configuración).
- [x] Colección `users` con rol: documento creado al registrarse; `observeUserRole` en vivo; panel admin solo para Admin/Editor (item oculto en el menú + pantalla "Acceso restringido" en la ruta). Datos reales de nombre/email en menú y perfil.

### Fase 3 — Firestore ✅ COMPLETADA
- [x] Cargar equipos, estadios, partidos y resultados reales desde Firestore (repos `Firestore*` con listeners en vivo; tabla de posiciones calculada desde los resultados; nombres de estadio/equipo denormalizados).
- [x] Formularios admin escribiendo/actualizando/borrando: Clubes (crear/editar/eliminar tocando una fila), Estadios (ídem, con GeoPoint), Partidos (programar con jornada + fecha parseada a español, y eliminar desde el fixture seleccionado), Acta (actualizar marcador y estado del partido), Usuarios (cambiar rol / quitar del panel) e Invitaciones (colección `invites`; la alta real del usuario requiere Cloud Function o consola).
- [x] Favoritos persistidos por usuario en `users/{uid}/favorites` (`UserFavoritesViewModel` + `FavoritesRepository`); con sesión activa se cargan y guardan automáticamente, y desaparecen al cerrar sesión.
- [x] Reglas de seguridad escritas (`firestore.rules` + `firebase.json`); desplegar con `firebase deploy --only firestore:rules` y probar en consola (acción manual del usuario).

### Fase 4 — Mapas
✅ COMPLETADA — 100% gratuito con MapLibre + OpenFreeMap
- [x] Mapa con **MapLibre 11.5 + tiles vectoriales de OpenFreeMap** (gratuito, ilimitado, sin API key ni tarjeta) — reemplaza a Google Maps (exige facturación) y a osmdroid (la política del tile server público de OSM no permite apps nativas en producción).
- [x] Mapa embebido con marcador en `StadiumDetailScreen` (coordenadas del estadio; si no tiene, se oculta) + inicialización de MapLibre en `FutViewApplication`.
- [x] Botón "Cómo llegar en mapas" con Intent `geo:` (sin costo, abre la app de mapas del teléfono).
- [x] Sin permisos de ubicación (no se usa geolocalización).

### Fase 4-B — Datos de fútbol ✅ COMPLETADA con ESPN (gratis, sin clave)
- **Fuente oficial en uso: ESPN** (`site.api.espn.com`, liga `chi.1`) — sin registro, sin API key y sin cuota.
- [x] `data/remote/Espn.kt` (Retrofit + DTOs): `scoreboard?dates=2026&limit=500` devuelve los 240 partidos de la temporada con fecha/hora real, estado y marcador; `standings?season=2026` entrega la tabla oficial (PJ, puntos, DG).
- [x] `EspnSyncRepository`: escribe `matches` con id estable `espn-<eventId>`, reutiliza los equipos/estadios del seed emparejando nombres normalizados (sin duplicar clubes), infiere la jornada (8 partidos por fecha) y escribe `standings/2026`.
- [x] Sincronización automática cada 6 horas (4 veces al día) al abrir la app con sesión + botón manual **"Sincronizar con ESPN"** en Admin → Equipos.
- [x] Limpieza automática de documentos heredados de API-Football (`af-*`) y de los 5 resultados del seed inicial, para no duplicar marcadores.
- [x] ~~`AdminStatusScreen` (dashboard "Estado del sistema")~~ — eliminado en la limpieza de UI, junto con el respaldo manual de Wikipedia (`WikipediaSyncViewModel` + `WikipediaStandingsRepository`).
- **API-Football eliminada** (código y clave): su plan gratuito solo sirve temporadas 2021–2023, por lo que no podía alimentar la temporada 2026.
- [x] La tabla de posiciones se lee de `standings/2026` (ESPN); el respaldo manual de Wikipedia fue eliminado.

### Fase 5 — Pulido y release 🔄 EN CURSO
- [x] Dataset completo del fútbol chileno (Liga de Primera 2026) desde ESPN: 16 clubes, 240 partidos y tabla oficial.
- [x] Limpieza de UI: eliminados `DemoBanner` y textos de mockup/demostración; cabeceras muestran el usuario real; menú reducido a modo oscuro, administración/estado y cierre de sesión.
- [x] Modo oscuro persistente con `SharedPreferences`, paleta dinámica y controles compatibles.
- [x] Pantalla de resultados ordenada del más reciente al más antiguo (fecha + hora de kickoff).
- [x] Filtros de comuna/ciudad funcionales en Estadios y Equipos (opciones derivadas de los datos).
- [x] Notas de sincronización, chips de estadísticas y dashboard "Estado del sistema" eliminados; código heredado de Wikipedia fuera.
- [x] Tests básicos y README decente (Fase 5).
- [ ] Icono de app, `applicationId` final (requiere registrar el package en Firebase) y keystore de release.
- [ ] Build release con minify y verificación.

---

## 6. Registro de avance

| Fecha | Avance |
|---|---|
| 24-nov-2026 | Diagnóstico inicial. Prototipo UI completo; Firebase configurado a nivel de proyecto pero sin código; mapas no integrados. |
| 24-nov-2026 | **Fase 0 completada**: limpieza de `build.gradle.kts` + permiso `INTERNET` (commit `f361685`); eliminados los datos hardcodeados (`FutViewData` → `data/Models.kt`), credenciales, favoritos y notificaciones demo; pantallas quedan con estados vacíos y `TODO Fase 3` marcando la conexión al repositorio. Compilación OK. |
| 24-nov-2026 | **Fase 1 completada**: capa de datos (`data/repository` con contratos + stubs, `data/model` con DTOs Firestore/GeoPoint, `data/AppContainer` DI manual), dependencias `firebase-firestore` + `lifecycle-viewmodel-compose` + `lifecycle-runtime-compose`, `UiState` (Loading/Success/Error) y 10 ViewModels (5 pestañas + 5 admin) con reintento; estados de carga e integrado el aviso pasivo de error de Firebase (`FirestoreErrorBanner`) en todas las pantallas con datos. Compilación OK. |
| 24-nov-2026 | **Fase 2 completada**: Firebase Auth real (login, registro con verificación de email, recuperación y cambio de contraseña), sesión persistente con splash de arranque, logout real, errores de auth traducidos a español; colección `users` con rol creada al registrarse y leída en vivo; panel admin restringido a Admin/Editor; nombre/email reales en menú y perfil. Nota: `firebase-auth` fijada a 24.0.1 (la 25 del BOM exige Kotlin 2.4). Compilación OK. |
| 24-nov-2026 | **Fase 3 completada**: repositorios Firestore reales para catálogos con CRUD (clubes, estadios con GeoPoint, partidos/fixture, acta con standings calculadas), favoritos persistidos por usuario (`users/{uid}/favorites`) con `UserFavoritesViewModel`, persistencia offline activada (`FutViewApplication`, 64 MB), reglas de seguridad escritas (`firestore.rules` + `firebase.json` — despliegue manual pendiente) y stubs eliminados. Compilación OK. |
| 24-nov-2026 | **Hotfix crash de arranque**: `FutViewApplication` mezclaba `isPersistenceEnabled = true` (setter deprecado) con `setLocalCacheSettings(...)` — Firestore moderno lanza `IllegalStateException`. Corregido dejando solo `persistentCacheSettings` (la persistencia ya viene activada por defecto). Verificado en dispositivo: la app arranca y queda viva, sin crashes en logcat. |
| 24-nov-2026 | **Hotfix PERMISSION_DENIED**: los listeners de navegación se suscribían sin sesión y sin manejo de errores → la app moría al entrar al login. Ahora no se suscriben listeners sin sesión y las denegaciones se absorben (`catch`); los banners por pantalla informan. Verificado en dispositivo: app viva en login sin crashes. |
| 24-nov-2026 | **Mapa MapLibre + OpenFreeMap**: se reemplazó osmdroid porque la política del tile server público de OSM no permite apps nativas en producción. Tiles vectoriales gratis, ilimitados y sin clave. Verificado en dispositivo. |
| 24-nov-2026 | **ESPN reemplaza a API-Football**: `EspnSyncRepository` descarga los 240 partidos de la temporada 2026 (fecha/hora real, estado y marcador) y la tabla oficial, reutiliza equipos/estadios del seed y elimina los registros heredados (`af-*` y los 5 resultados del seed). Sync automática cada 6 h + botón admin. API-Football y su clave fueron eliminadas. Verificado en dispositivo: fixture y resultados reales visibles, 0 crashes. |
| 24-nov-2026 | **Limpieza de UI + modo oscuro**: eliminados `DemoBanner`, textos de mockup/demostración y botones sin uso (perfil, cuenta, configuración, notificaciones, ayuda, contacto). Cabeceras y admin usan el usuario autenticado real; menú reducido a switch persistente de modo oscuro, administración/estado y logout. Paleta clara/oscura y controles dinámicos. Build debug OK, instalada en dispositivo y sin crashes. |
| 24-nov-2026 | **Resultados más recientes primero**: la pestaña Resultados ordena por fecha de kickoff (parseada de `dayLabel`) y hora, descendente; favoritos y acta conservan su orden original. Build debug OK. |
| 24-nov-2026 | **"ÚLTIMO" y "PRÓXIMO PARTIDO" con datos reales**: `lastResult`/`nextMatch` de equipos y estadios se calculan desde `matches` (`data/ActivityResult.kt`); tarjetas de Equipos, Estadios, Favoritos y detalles muestran resúmenes ("Ganó 2-1 a X · 21 nov", "vs Y · 28 nov · 18:00") o "Por confirmar" si no hay datos. Build debug OK, instalada en dispositivo sin crashes. |
| 24-nov-2026 | **Resultados: orden y paginación**: corregido el parseo de `dayLabel` (era case-sensitive y toda fecha caía a `MIN`, ordenando como si fuera agosto); la lista ahora ordena de verdad por fecha+hora descendente. Paginado de 5 en 5 con botón "Ver más" y encabezado de fecha por grupo; la tabla de posiciones queda a pocos scrolls del inicio. Build debug OK, instalada en dispositivo sin crashes. |
| 24-nov-2026 | **Limpieza de notas y dashboards**: eliminados `InfoNote`/`InfoPill` y todas sus apariciones (notas de sincronización, "actualiza desde el panel", chips de estadísticas), el dashboard "Estado del sistema" (`AdminStatusScreen` + ruta + ítem de menú) y el código heredado del respaldo de Wikipedia (`WikipediaSyncViewModel`, `WikipediaStandingsRepository`, botón en Acta). Textos mockup del Acta ("Simulación de la jornada 1", "Vista previa") corregidos. Build debug OK. |
| 24-nov-2026 | **Filtro de comuna/ciudad funcional**: Estadios y Equipos ahora llenan el dropdown con las comunas/ciudades reales de los datos (antes tenía una única opción fija y nunca filtraba); si la ciudad guardada desaparece, vuelve a "Todas". Eliminado el rótulo duplicado "Sedes de la competición". Build debug OK, instalada en dispositivo sin crashes. **Fase 5 iniciada.** |
| 24-nov-2026 | **Fase 5: tests y README**: 11 tests unitarios (`ActivityResultTest`: parseo de fechas case-insensitive —regresión del bug de agosto—, resúmenes último/próximo; `StandingsTest`: puntos/DG y orden). `README.md` reescrito. Test task `:app:testDebugUnitTest` OK. |
