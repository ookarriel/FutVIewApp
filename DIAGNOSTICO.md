# Diagnóstico del proyecto FutView

> Fecha del diagnóstico: 24 de noviembre de 2026
> Este documento es la guía de trabajo del proyecto: describe el estado actual, lo que falta y el plan para terminarlo. Se debe ir actualizando a medida que se completan tareas.

---

## 1. Resumen ejecutivo

FutView es una app Android en **Kotlin + Jetpack Compose** para consultar información del fútbol chileno (equipos, estadios, partidos, resultados, favoritos) con un panel de administración.

**Estado actual: prototipo visual limpio, esperando la capa de datos.** Toda la interfaz está construida (~7.800 líneas de UI Compose) siguiendo los mockups de `mockups/`, pero:

- ✅ **Fase 0 completada**: los datos hardcodeados (`FutViewData`) fueron eliminados; las pantallas compilan y muestran estados vacíos hasta conectar Firestore.
- **No hay Firebase funcional**: existe el `google-services.json` y el plugin de Gradle, pero falta agregar las dependencias y todo el código de Auth/Firestore.
- **No hay mapas**: el SDK de Google Maps no está integrado; el botón "Abrir dirección en mapas" del detalle de estadio tiene `onClick = {}` vacío.
- ✅ **Fase 1 completada**: arquitectura base lista — repositorios con contratos, ViewModels por pantalla y estados de carga/error. La fuente de datos es temporal (listas vacías) hasta conectar Firestore en la Fase 3.

---

## 2. Stack y configuración actual

| Componente | Valor | Observación |
|---|---|---|
| Lenguaje | Kotlin 2.2.10 | |
| UI | Jetpack Compose (BOM 2025.12.00, Material 3) | Con `material-icons-extended` y `material3-adaptive-navigation-suite` |
| AGP | 9.2.1 | Kotlin integrado en AGP (no hay plugin `kotlin-android` separado) |
| minSdk / targetSdk / compileSdk | 33 / 36 / 37 | |
| applicationId | `com.example.futview` | ⚠️ Cambiar antes de publicar |
| Firebase | `google-services.json` (proyecto `futview-a23f1`, package correcto) + plugin `com.google.gms.google-services` 4.6.0 + BOM 35.2.0 | ✅ Configuración base lista; Auth conectado (Fase 2); Firestore como dependencia y repos de usuarios; datos de catálogo pendientes (Fase 3) |
| Mapas | — | ❌ No integrado |
| Navegación | Enum manual + `BackHandler` (`navigation/FutViewApp.kt`) | ⚠️ Funciona para el prototipo; evaluar migrar a Navigation Compose |
| Tests | Solo plantilla (`ExampleUnitTest`, `ExampleInstrumentedTest`) | ❌ Sin tests reales |

---

## 3. Lo que ya existe

### UI de usuario (completa como mockup navegable)
- **Login / Registro / Recuperar contraseña / Verificar email / Nueva contraseña** (`LoginScreen.kt`, `AuthScreens.kt`): pantallas terminadas, sin datos precargados y navegación simulada (el login navega directo a HOME).
- **Partidos** (`MatchesScreen`, `MatchDetailScreen`): fixture agrupado por fecha, detalle con botón a estadio.
- **Resultados** (`ResultsScreen`, `ResultDetailScreen`): marcadores finalizados.
- **Equipos** (`TeamsScreen`, `TeamDetailScreen`): lista y detalle con próximo partido y estadio.
- **Estadios** (`StadiumsScreen`, `StadiumDetailScreen`): lista y detalle con dirección, aforo y botón de mapas (sin funcionalidad).
- **Favoritos** (`FavoritesScreen`): equipos, partidos y estadios favoritos. Guardados solo en memoria (`mutableStateListOf`), **se pierden al cerrar la app**.
- **Menú de usuario** (`UserMenuScreen`, `AccountScreens.kt`): perfil, ajustes, notificaciones, ayuda, contacto (pantallas estáticas).

### UI de administración
- Panel admin con 5 pestañas (`AdminHubScreen`, `admin/*.kt`): formularios de Equipos, Estadios (con campos de ubicación GPS), Partidos/Fixture, Resultados (Acta) y Usuarios + invitación de usuarios y confirmación de borrado. **Ningún formulario persiste datos.**

### Modelos de datos (`data/Models.kt`)
- `Team`, `Stadium`, `Match`, `StandingRow`, `SystemUser`, `FixtureDay`, `MatchStatus`.
- ⚠️ `Stadium.latitude` y `longitude` son `String`. Para Firestore + Maps conviene usar `GeoPoint` (o `Double`).
- ℹ️ El objeto demo `FutViewData` fue eliminado en la Fase 0; las pantallas leen de listas vacías con `// TODO Fase 3` marcando el punto de conexión al repositorio.

### Arquitectura (creada en la Fase 1)
- **`data/repository/`**: contratos `TeamRepository`, `StadiumRepository`, `MatchRepository`, `UserRepository` (exponen `Flow`). Implementaciones vacías temporales (`Empty*Repository`) para catálogos hasta la Fase 3. **Fase 2**: `AuthRepository` + `FirebaseAuthRepository` y `FirestoreUserRepository` (colección `users` con rol) conectados en `AppContainer`.
- **`data/model/FirestoreDtos.kt`**: DTOs de Firestore (`TeamDoc`, `StadiumDoc` con `GeoPoint`, `MatchDoc`, `StandingDoc`, `UserDoc`) con mapeadores a los modelos de dominio.
- **`data/AppContainer.kt`**: inyección manual; punto único para cambiar los stubs por implementaciones de Firestore.
- **`ui/viewmodel/`**: `UiState` (Loading/Success/Error) + ViewModels de las 5 pestañas (Partidos, Resultados, Equipos, Estadios, Favoritos) y de los formularios admin (Equipos, Estadios, Usuarios, Partidos/Fixture, Acta), todos con reintento. **Fase 2**: `AuthStateViewModel` (sesión global) y ViewModels de Login, Registro, Recuperar, Nueva contraseña y Reenvío de enlace.
- **Estados de carga/error en UI**: `LoadingState` (carga) y `FirestoreErrorBanner` (aviso pasivo de problema con la base de datos, con reintento) integrados en todas las pantallas que leen datos; `ConnectionErrorScreen` se reserva para un corte total de conexión.
- **Sesión y rutas (Fase 2)**: splash mientras se resuelve la sesión; rutas de acceso (login/registro/recuperar) solo sin sesión; el resto de la app solo con sesión; acceso a Administración filtrado por rol.

### Configuración
- ✅ `app/google-services.json` presente y con el package correcto.
- ✅ Tema, colores y tipografías propios (`ui/theme/`).
- ✅ Componentes reutilizables (`ui/components/FutViewComponents.kt`, 1.600 líneas).

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
  - `standings` → **calculada en cliente** a partir de `matches` finalizados (no se almacena)
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

### C. Mapas y ubicación de estadios — ❌ No implementado
- [ ] Dependencia `play-services-maps` + `maps-compose` (librería oficial de Compose).
- [ ] Obtener **API key de Maps SDK for Android** y agregarla al manifest (`meta-data com.google.android.geo.API_KEY`) — idealmente vía `local.properties` + BuildConfig, no hardcodeada.
- [ ] Mapa embebido en `StadiumDetailScreen` con marcador del estadio (usar `GeoPoint`).
- [ ] (Opcional) Mapa general con todos los estadios en `StadiumsScreen`.
- [ ] Mínimo viable rápido: botón "Abrir dirección en mapas" con Intent de `geo:` (URI con lat/lng ya presentes en los datos).
- [ ] Permisos de ubicación solo si se agrega "distancia hasta el estadio" o estadio más cercano.

### D. Arquitectura y calidad — ❌ No implementado
- [x] Estructura de capas: `data/repository` (contratos + stubs temporales), `data/model` (DTOs Firestore), `ui/viewmodel` (ViewModels + UiState). Creada en Fase 1.
- [x] ViewModels con `viewModelScope` y estados UI (`UiState`: Loading/Success/Error) + aviso pasivo de error (`FirestoreErrorBanner`). Fase 1.
- [ ] Inyección manual (o Hilt si el proyecto crece).
- [ ] Persistir favoritos del usuario en Firestore (hoy en memoria).
- [ ] Panel admin conectado: crear/editar/eliminar documentos reales en Firestore (hoy solo escriben en estados locales si es que eso).
- [ ] Validación y feedback real en formularios admin.
- [ ] Tests: unitarios para repos/VM, y al menos smoke tests de Compose.
- [ ] R8/minify y reglas ProGuard para release.

### E. Deudas y detalles detectados (menores pero concretos)
1. ✅ ~~`app/build.gradle.kts` línea 45: `8` suelto con espacios basura~~ — eliminado en Fase 0 (commit `f361685`).
2. ✅ ~~El manifest no declara `INTERNET`~~ — agregado en Fase 0 (commit `f361685`).
3. ⚠️ `README.md` está casi vacío y con typo ("FutVIewApp").
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
- [ ] API key + dependencias Maps Compose.
- [ ] Mapa con marcador en detalle de estadio.
- [ ] Intent `geo:` como fallback / botón "cómo llegar".
- [ ] (Opcional) Mapa de todos los estadios.

### Fase 5 — Pulido y release
- [ ] Dataset completo del fútbol chileno (Liga de Primera).
- [ ] Tests básicos, README decente, icono de app, `applicationId` final.
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
