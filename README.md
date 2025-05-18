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

- X

---

## ⚙️ Configuración Previa

* **ActiveMQ**
  Debe estar corriendo en `tcp://localhost:61616` con dos topics:

    * `EventsTopic` → para consumir
    * `tweets`      → para publicar

---

## 🏗️ Arquitectura

** Añadir imágenes **
  ```
                                                        +------------------------+
                                                        |     API-Football       |
                                                        +------------------------+
                                                                    |
                                                                    v
                                                            +--------------------+
                                                            |  football-feeder   |
                                                            +--------------------+
                                                                     |
                                                                     v
                                                               [EventsTopic]
                                                                     |
                                                                     |
                                          +--------------------+     |      +--------------------+
                                          |  twitter-feeder    | <------->  |   tweets (topic)   |
                                          +--------------------+            +--------------------+
                                                                                      |
                                                                                      v
                                                                          +-------------------------+
                                                                          |  EventStoreBuilder      |
                                                                          | (consume + persistencia)|
                                                                          +-------------------------+
                                                                                       |
                                                                                       |
                                                                                       v
                                                                         +-----------------------------+
                                                                         |    Business Unit (API/GUI)  |
                                                                         +-----------------------------+

  ```

> *Figura: flujo de datos entre feeders, broker (ActiveMQ), Event Store y Business Unit.*


* **Feeder modules**
    * `football-feeder`: Lee la API-Football y publica JSON de eventos
    * `twitter-feeder`: Lee tweets (mock o real), analiza sentimiento y publica JSON con `score`

* **Broker**: ActiveMQ
* **EventStoreBuilder**: consume topics y escribe ficheros
  `eventstore/{topic}/{ss}/{YYYYMMDD}.events`

* **Business-Unit**: X

---

## 📝 Documentación y Diagramas

* **README.md**:

---

## 🧪 Ejemplo de Uso

- X

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
