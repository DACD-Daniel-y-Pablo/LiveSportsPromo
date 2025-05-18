 # 🚀 LiveSportsPromo

> **Propuesta de valor**: Un sistema modular para capturar datos en tiempo real (tweets, eventos de fútbol), procesarlos con análisis de sentimiento y almacenarlos en un event-store y datamart, listo para alimentar promociones dinámicas mediante una API.

---

## 📂 Estructura del Repositorio

```

LiveSportsPromo/
|
|
├── discount-core/
|        |
│        └── src/
|              ├── main/java/
|              |         |
|              |         ├── adapters/
|              |         |      |
|              |         |      └── helpers
|              |         |
|              |         ├── entities
|              |         |
|              |         ├── ports
|              |         |
|              |         └── useCases
|              |
|              ├── resources
|              |
|              └── pom.xml
|
├── EventStoreBuilder/ 
|        |
│        └── src/
|             ├── main/java/
|             |      |
|             |      ├── X
|             |      |
|             |      ├── X
|             |      |
|             |      └── X
|             |
|             └── pom.xml
|
|
├── football-feeder/
|        |
│        └── src/
|              ├── main/java/
|              |         |
|              |         ├── adapters/
|              |         |      |
|              |         |      └── helpers
|              |         |
|              |         ├── entities
|              |         |
|              |         ├── ports
|              |         |
|              |         └── useCases
|              |
|              ├── resources
|              |
|              └── pom.xml
|
|
├── twitter-feeder/
|        |
│        └── src/
|              ├── main/java/
|              |         |
|              |         ├── adapters
|              |         |
|              |         |
|              |         ├── entities
|              |         |
|              |         ├── ports
|              |         |
|              |         ├── ports
|              |         |
|              |         └── utils
|              |
|              └── pom.xml
|
|
├── diagrams/ 
|
├── README.md                  ← Documentación del Proyecto
|
└── .gitignore

```

---

## 👥 Equipo

- **Daniel Rodríguez Alonso**
- **Pablo Martínez Suárez**

Repositorio: https://github.com/DACD-Daniel-y-Pablo/LiveSportsPromo.git

---

## 🛠️ Módulos y Ejecución

Este proyecto se compone de 4 módulos principales:

1. `football-feeder`
2. `twitter-feeder`
3. `EventStoreBuilder`
4. `discount-api`

### Requisitos Previos

Antes de ejecutar cualquiera de los módulos, asegúrate de tener:

- **ActiveMQ** descargado y ejecutándose localmente (por defecto en `tcp://localhost:61616`).
- **MySQL** instalado y en funcionamiento en tu máquina.
- Una base de datos creada llamada **`discount_promo`**.

### Ejecución de Módulos

#### 🔹 football-feeder

Ejecuta el método `main` del módulo pasando los siguientes argumentos:

```bash
<FOOTBALL_API_KEY> <BASE_URL_API> <URL_MYSQLITE> EventsTopic <URL_ACTIVEMQ>
```
---

- <FOOTBALL_API_KEY>: Tu clave de API para Football API Sports.
- <BASE_URL_API>: La url base de consulta a tu api de deportes
- <URL_MYSQLITE>: La ubicación de tu archivo sqlite en tu ordenador
- <URL_ACTIVEMQ>: La url donde se conecta al Broker, por default se utiliza `tcp://localhost:61616`

#### 🔹 twitter-feeder

Ejecuta el método `main` del módulo pasando los siguientes argumentos:

```bash 
<URL_ACTIVEMQ> EventsTopic tweets <TWITTER_BEARER_TOKEN>
```

- <TWITTER_BEARER_TOKEN>: El Token necesario para conectarte a la api de Twitter.

#### 🔹 EventStoreBuilder

Ejecuta el método `main` del módulo pasando los siguientes argumentos:

```bash 
<URL_ACTIVEMQ> EventsTopic tweets <TWITTER_BEARER_TOKEN>
```

#### 🔹 discount-api

Ejecuta el método `main` del módulo pasando los siguientes argumentos:


```bash 
<URL_ACTIVEMQ> EventsTopic tweets jdbc:mysql://localhost:3306/discount_promo <MYSQL_USER> <MYSQL_PASSWORD>
```

- <MYSQL_USER> y <MYSQL_PASSWORD>: Son el usuario y la contraseña de la conexión a Mysql


## ⚙️ Configuración Previa

Antes de ejecutar los módulos, asegúrate de tener los siguientes servicios configurados y activos:

### *🟢 ActiveMQ*

Debe estar corriendo en `tcp://localhost:61616` con dos topics configurados:

- `EventsTopic` → para consumir
- `tweets`      → para publicar

### *🟢 MySQL*

Debes tener instalado y en ejecución un servidor **MySQL** en tu máquina local. Además, es necesario que:

- Exista una base de datos creada llamada: `discount_promo`
- Tengas las credenciales (usuario y contraseña) preparadas para conectarte a dicha base de datos durante la ejecución de los módulos.

---

## 🏗️ Arquitectura

### football-feeder 

![football-feeder](system-design/football-feeder.drawio.png)

### twitter-feeder

![twitter-feeder](system-design/imagen)

### EventStoreBuilder

![twitter-feeder](system-design/EventStoreBuilder.drawio.png)

### discount-api

![twitter-feeder](system-design/discount-api.drawio.png)

> *Figura: flujo de datos entre feeders, broker (ActiveMQ), Event Store y Business Unit.*


* **Feeder modules**
    * `football-feeder`: Lee la API-Football y publica JSON de eventos
    * `twitter-feeder`: Lee tweets (mock o real), analiza sentimiento y publica JSON con `score`

* **Broker**: ActiveMQ
* **EventStoreBuilder**: consume topics y escribe ficheros
  `eventstore/{topic}/{ss}/{YYYYMMDD}.events`

* **Business-Unit**: Consume del broker y habilita un endpoint en el que consumir los descuentos disponibles

---

## 📝 Documentación y Diagramas

* **README.md**:

---

## 🧪 Ejemplo de Uso

Un ejemplo de uso sería conectarse al endpoint expuesto por el módulo `discount-api`. Desde allí, otras plataformas pueden consultar los descuentos generados por los módulos `football-feeder` y `twitter-feeder`, y aplicarlos en su sistema correspondiente.

Esto permite integrar la lógica de promociones en tiempo real dentro de una plataforma externa (por ejemplo, una tienda online o una app de servicios).

### 🔗 Endpoint de ejemplo

```http
GET http://localhost:8080/discounts
```

---

## 📚 Buenas Prácticas y Patrones

* **Ports & Adapters (Arquitectura Hexagonal)**
* **Single Responsibility**: cada módulo se encarga de una fuente y persistencia distinta
* **Event-Driven**: pub/sub con ActiveMQ
* **Clean Code**

---

## 🗓️ Roadmap

* **Sprint 1**: consumo de APIs
* **Sprint 2**: Uso del Broker + Event Store Builder
* **Sprint 3**: Business Unit (API REST / CLI)

---

| Requisitos                                               | ¿Cubierto? | Comentario                                                                        |
| -------------------------------------------------------- | ---------- | --------------------------------------------------------------------------------- |
| Breve descripción del proyecto y propuesta de valor      | ✅          | Muy bien expresado en el pitch inicial (`> Propuesta de valor`)                  |
| Principios y patrones de diseño aplicados en cada módulo | ✅          | Bien explicados en la sección **Buenas Prácticas y Patrones**                    |
| Arquitectura de sistema y de aplicación (con diagramas)  | ✅          | Esquema textual y promesa de `docs/architecture.png` (perfecto si luego lo subes)|
| Documentación en formato README.md                       | ✅          | Markdown limpio, estructurado y legible                                          |
| Uso del broker y flujo de eventos                        | ✅          | Claramente explicado en **Arquitectura**                                         |
| Configuración previa del sistema                         | ✅          | Muy útil la sección de `ActiveMQ` y `Twitter_token.txt`                          |
| Separación modular clara con explicación                 | ✅          | Gracias al bloque **🛠️ Módulos y Ejecución** y el árbol de carpetas              |
| Justificación de la elección de APIs y estructura del datamart | 🟠 Parcial | Mencionas las APIs, pero **no justificas por qué** se eligió API-Football o la API de Twitter ni cómo se estructura el datamart (campos, organización, formato).               |
| Instrucciones para compilar y ejecutar cada módulo             | 🟥 Falta   | Tienes un placeholder "X". Deberías incluir los comandos Maven para `package` y ejecución (`java -jar target/...jar` o `mvn exec:java`).                                       |
| Ejemplos de uso (consultas, peticiones REST, etc.)             | 🟥 Falta   | La sección **🧪 Ejemplo de Uso** está vacía. Debes añadir ejemplos como: cómo publicar un evento manual, cómo se vería un tweet analizado, qué estructura JSON se genera, etc. |
