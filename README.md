💰 MoneyTracker - Control de Gastos Personales

MoneyTracker es una aplicación nativa de Android diseñada para ayudar a los usuarios a gestionar sus finanzas personales de manera eficiente. Permite registrar ingresos y gastos, controlar el presupuesto mensual con alertas visuales, visualizar estadísticas gráficas y consultar tasas de cambio en tiempo real.

📱 Funcionalidades Principales

  -Gestión de Transacciones (CRUD): Registro completo de ingresos y gastos categorizados.

  -Dashboard Interactivo: Resumen de balance, ingresos y gastos totales.

  -Control de Presupuesto: Barra de progreso con alertas de colores (Azul/Naranja/Rojo) según el porcentaje gastado.

  -Conversión de Monedas: Integración con API REST para convertir gastos en moneda extranjera a la moneda local en tiempo real.

  -Estadísticas Visuales: Gráficos circulares (distribución) y de barras (historial diario) usando MPAndroidChart.

  -Persistencia Local: Todos los datos se guardan en el dispositivo (funciona offline).

  -Configuración: Gestión de perfil de usuario y restablecimiento de datos.

🛠️ Stack Tecnológico

El proyecto sigue la arquitectura recomendada por Google (MVVM) para garantizar escalabilidad y mantenimiento.

  -Lenguaje: Java 17

  -Arquitectura: Model-View-ViewModel (MVVM)

  -Base de Datos: Room Database (SQLite abstraction)

  -Conexión API: Retrofit 2 + GSON

  -Gráficos: MPAndroidChart

  -Diseño: Material Design (CardView, FloatingActionButton, CoordinatorLayout)

  -Otros: SharedPreferences (para configuración ligera).

🌐 Integración API

La aplicación consume la API pública ExchangeRate-API para obtener las tasas de cambio actualizadas.

  -Endpoint: https://api.exchangerate-api.com/v4/latest/{moneda}

  -Uso: Permite al usuario ingresar un monto en USD, EUR, etc., y calcular su equivalencia antes de guardar la transacción.

🚀 Instalación y Uso

Clonar el repositorio:

  -git clone [https://github.com/TU_USUARIO/MoneyTracker.git](https://github.com/TU_USUARIO/MoneyTracker.git)


Abrir en Android Studio: Selecciona la carpeta clonada.

  -Sincronizar Gradle: Asegúrate de tener conexión a internet para descargar las dependencias (Room, Retrofit, MPAndroidChart).

  -Ejecutar: Conecta un dispositivo físico o usa el emulador (Min SDK 24).

📄 Estructura del Proyecto

  -ui/: Contiene Activities, ViewModels y Adapters.

  -data/: Contiene las Entidades (Room), DAOs y el Repositorio.

  -api/: Contiene la interfaz de Retrofit y los modelos de respuesta JSON.

  -utils/: Clases de utilidad como PrefsManager.

Desarrollado por: Lara Steven

Curso: Desarrollo de Aplicaciones Móviles
