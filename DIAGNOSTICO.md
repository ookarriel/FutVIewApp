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
- **No hay capa de datos ni arquitectura**: sin ViewModels, sin repositorios, sin Flow/Coroutines, sin inyección de dependencias.

---

## 2. Stack y configuración actual

| Componente | Valor | Observación |
|---|---|---|
| Lenguaje | Kotlin 2.2.10 | |
| UI | Jetpack Compose (BOM 2025.12.00, Material 3) | Con `material-icons-extended` y `material3-adaptive-navigation-suite` |
| AGP | 9.2.1 | Kotlin integrado en AGP (no hay plugin `kotlin-android` separado) |
| minSdk / targetSdk / compileSdk | 33 / 36 / 37 | |
| applicationId | `com.example.futview` | ⚠️ Cambiar antes de publicar |
| Firebase | `google-services.json` (proyecto `futview-a23f1`, package correcto) + plugin `com.google.gms.google-services` 4.6.0 + BOM 35.2.0 | ✅ Configuración base lista, ❌ sin dependencias de Auth/Firestore |
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

### Configuración
- ✅ `app/google-services.json` presente y con el package correcto.
- ✅ Tema, colores y tipografías propios (`ui/theme/`).
- ✅ Componentes reutilizables (`ui/components/FutViewComponents.kt`, 1.600 líneas).

---

## 4. Lo que falta (brechas principales)

### A. Firebase Authentication — ❌ No implementado
- [ ] Dependencias: `firebase-auth` (KTX) en `app/build.gradle.kts`.
- [ ] `FirebaseAuth` real en Login/SignUp/Recuperar/Logout (hoy todo es simulado).
- [ ] Sesión persistente: arrancar en HOME si hay sesión (`AuthStateListener` / flujo `currentUser`).
- [ ] Manejo de errores de auth (email/contraseña inválidos, usuario no existe, etc.) mostrados en UI.
- [ ] Enviar verificación de email y correo de recuperación de verdad.
- [ ] Reglas de seguridad basadas en roles (admin/editor/usuario). Roles: modelo Firestore (colección `users` con `role`) o custom claims.

### B. Cloud Firestore — ❌ No implementado
- [ ] Dependencias: `firebase-firestore` (KTX).
- [ ] Definir el modelo de colecciones:
  - `teams` (equipo: nombre, corto, ciudad, estadio ref, escudo/logo)
  - `stadiums` (nombre, comuna, dirección, capacidad, `location: GeoPoint`, equipo local)
  - `matches` (fecha/hora `Timestamp`, jornada, local, visita, estado, goles, estadio ref)
  - `standings` (calculada o derivada de resultados)
  - `users` (uid, nombre, rol)
  - `favorites` por usuario (subcolección o array)
- [x] **Permiso `android.permission.INTERNET` en el Manifest** (agregado en Fase 0).
- [ ] Capa de repositorios (`TeamRepository`, `MatchRepository`, `StadiumRepository`, `UserRepository`) que reemplace `FutViewData`.
- [ ] ViewModels por pantalla con `StateFlow` (estado de carga / error / datos) + pantalla `ConnectionErrorScreen` cableada a errores reales.
- [ ] Reemplazar `String` lat/long por `GeoPoint` en `Stadium`.
- [ ] Reglas de seguridad de Firestore (lectura pública, escritura solo admin/editor).
- [ ] Índices compuestos si los queries lo requieren.
- [ ] Offline: habilitar persistencia de Firestore si se quiere soporte sin conexión.

### C. Mapas y ubicación de estadios — ❌ No implementado
- [ ] Dependencia `play-services-maps` + `maps-compose` (librería oficial de Compose).
- [ ] Obtener **API key de Maps SDK for Android** y agregarla al manifest (`meta-data com.google.android.geo.API_KEY`) — idealmente vía `local.properties` + BuildConfig, no hardcodeada.
- [ ] Mapa embebido en `StadiumDetailScreen` con marcador del estadio (usar `GeoPoint`).
- [ ] (Opcional) Mapa general con todos los estadios en `StadiumsScreen`.
- [ ] Mínimo viable rápido: botón "Abrir dirección en mapas" con Intent de `geo:` (URI con lat/lng ya presentes en los datos).
- [ ] Permisos de ubicación solo si se agrega "distancia hasta el estadio" o estadio más cercano.

### D. Arquitectura y calidad — ❌ No implementado
- [ ] Estructura de capas: `data/` (repos + fuentes Firestore), `domain/` (modelos, opcional), `ui/` (screens + viewmodels).
- [ ] ViewModels con `viewModelScope` y estados UI.
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

### Fase 1 — Arquitectura base
- [ ] Crear estructura `data/repository`, `ui/viewmodel`.
- [ ] Definir modelos Firestore (mapeo `Team`, `Stadium` con `GeoPoint`, `Match`, `User`).
- [ ] ViewModels de pantallas principales leyendo desde un repositorio (aún puede devolver datos demo, pero con la forma final).
- [ ] Estados de carga/error en UI (conectar `ConnectionErrorScreen`).

### Fase 2 — Firebase Auth
- [ ] Dependencia + login real email/contraseña.
- [ ] Registro + verificación de email + recuperación de contraseña.
- [ ] Persistencia de sesión y logout real.
- [ ] Colección `users` con rol; acceso al panel admin solo para admin/editor.

### Fase 3 — Firestore
- [ ] Cargar equipos, estadios, partidos y resultados reales desde Firestore.
- [ ] Formularios admin escribiendo/actualizando/borrando documentos.
- [ ] Favoritos persistidos por usuario.
- [ ] Reglas de seguridad + prueba de las reglas.

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
