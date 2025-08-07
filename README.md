# Desafío Galicia - Android App

Una aplicación Android nativa desarrollada en Kotlin que consume la API de RandomUser.me para mostrar una lista paginada de usuarios con funcionalidad de favoritos.

## 🏗️ Arquitectura y Decisiones Técnicas

### Arquitectura MVVM + Clean Architecture

El proyecto implementa una arquitectura limpia basada en capas bien definidas:

```
presentation/     # UI Layer (Compose + ViewModels)
├── screens/      # Pantallas de la aplicación
├── components/   # Componentes reutilizables
└── navigation/   # Navegación entre pantallas

data/            # Data Layer
├── model/       # Modelos de datos
├── remote/      # API y servicios remotos
├── local/       # Base de datos local (Room)
└── repository/  # Repository pattern

di/              # Dependency Injection (Hilt)
```

### Stack Tecnológico

| Tecnología | Propósito | Justificación |
|------------|-----------|---------------|
| **Jetpack Compose** | UI Framework | UI declarativa moderna, mejor rendimiento y menos boilerplate |
| **Hilt** | Dependency Injection | Integración nativa con Android, simplifica testing |
| **Room** | Base de datos local | Persistencia de favoritos offline-first |
| **Retrofit** | Cliente HTTP | Standard de facto para APIs REST en Android |
| **Coroutines + Flow** | Programación asíncrona | Manejo elegante de operaciones async y estados reactivos |
| **Coil** | Carga de imágenes | Optimizado para Compose, manejo automático de cache |

## 📱 Funcionalidades Implementadas

### Core Features
- ✅ **Lista paginada de usuarios** (10 usuarios por página)
- ✅ **Filtrado por nacionalidad** (BR, GB, US, etc.)
- ✅ **Sistema de favoritos** persistente
- ✅ **Navegación entre páginas** con controles discretos
- ✅ **Detalle de usuario** con información completa

### UX/UI Features
- ✅ **Loading states** y manejo de errores
- ✅ **Ocultación automática del teclado** al buscar
- ✅ **Controles de paginación persistentes**
- ✅ **Indicadores visuales** de estado de carga
- ✅ **Material Design 3** con temas consistentes

## 🔧 Decisiones Técnicas Importantes

### 1. Paginación Discreta vs Infinite Scroll

**Decisión**: Implementar paginación con botones en lugar de scroll infinito.

**Justificación**:
- Mayor control del usuario sobre la carga de datos
- Mejor rendimiento (no carga datos innecesarios)
- Navegación más predecible (puede volver a páginas anteriores)
- Experiencia familiar para usuarios de web

### 2. Seed Dinámico por Nacionalidad

**Problema**: Con seed fijo, algunos UUIDs se repetían entre nacionalidades.

**Solución**:
```kotlin
val seed = if (nationality.isNullOrBlank()) {
    "challenge"
} else {
    "challenge-$nationality"
}
```

**Beneficio**: Garantiza usuarios únicos por nacionalidad, evitando favoritos "pegajosos".

### 3. Estado Centralizado vs Estado Distribuido

**Decisión**: Centralizar el estado de favoritos en `UserListViewModel`.

**Antes**: Cada `UserListItem` manejaba su propio estado de favoritos.  
**Después**: Un solo ViewModel observa cambios en la base de datos.

**Beneficios**:
- Single source of truth
- Sincronización automática entre pantallas
- Menos complejidad en componentes

### 4. Room para Persistencia Local

**Decisión**: Usar Room en lugar de SharedPreferences para favoritos.

**Justificación**:
- Estructura de datos más compleja (UUID, nombre, email, país, imagen)
- Queries eficientes para grandes cantidades de favoritos
- Type safety y migraciones automáticas
- Mejor integración con Coroutines/Flow

## 📁 Estructura del Proyecto

```
app/src/main/java/com/example/desafiogalicia/
├── DesafioGaliciaApplication.kt          # Application class con Hilt
├── MainActivity.kt                       # Activity principal
├── data/
│   ├── local/
│   │   ├── dao/FavoriteUserDao.kt       # DAO para favoritos
│   │   ├── database/AppDatabase.kt      # Configuración Room
│   │   └── entity/FavoriteUser.kt       # Entidad de favoritos
│   ├── model/User.kt                    # Modelos de API
│   ├── remote/RandomUserApi.kt          # Interface Retrofit
│   └── repository/UserRepository.kt     # Repository pattern
├── di/
│   ├── DatabaseModule.kt                # Módulos Hilt para Room
│   └── NetworkModule.kt                 # Módulos Hilt para Retrofit
├── presentation/
│   ├── components/
│   │   ├── LoadingIndicator.kt          # Componente de loading
│   │   └── UserListItem.kt              # Item de lista de usuarios
│   ├── screens/
│   │   ├── userdetail/                  # Pantalla de detalle
│   │   └── userlist/                    # Pantalla principal
│   └── navigation/AppNavigation.kt      # Navegación Compose
└── ui/theme/                            # Temas Material Design
```

## 🧪 Testing

### Unit Tests Implementados

```
app/src/test/java/com/example/desafiogalicia/
├── data/repository/UserRepositoryTest.kt     # Tests de Repository
├── presentation/screens/userlist/UserListViewModelTest.kt  # Tests de ViewModel
└── ExampleUnitTest.kt                        # Test básico
```

### Estrategia de Testing

- **Repository Layer**: Mocking de API y DAO para testing aislado
- **ViewModel Layer**: Testing de estados UI y lógica de negocio
- **Coroutines Testing**: Uso de `TestDispatcher` para operaciones async

### Frameworks de Testing

- **JUnit 4**: Framework base
- **Mockito**: Mocking de dependencias  
- **Kotlin Coroutines Test**: Testing de código asíncrono

## 🚀 Configuración y Ejecución

### Requisitos
- Android Studio Hedgehog+ (2023.1.1)
- JDK 17
- Android SDK 34
- Gradle 8.9

### Ejecución
```bash
git clone <repository-url>
cd DesafioGalicia
./gradlew app:assembleDebug
```

### Testing
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Integration tests
```

## 📊 Métricas y Rendimiento

### API Configuration
- **Base URL**: `https://randomuser.me/api/`
- **Seed**: `"challenge"` (garantiza resultados consistentes)
- **Paginación**: 10 usuarios por página
- **Timeout**: Configurado para conexiones lentas

### Optimizaciones Implementadas
- **Image Loading**: Cache automático con Coil
- **State Management**: Flow para estados reactivos
- **Memory Management**: ViewModels sobreviven rotaciones
- **Network Efficiency**: Paginación reduce llamadas innecesarias
