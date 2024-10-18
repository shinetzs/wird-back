Wird-Backend

Descripción

Este proyecto es un servicio para la consulta y almacenamiento en caché de datos meteorológicos de varias ciudades. 
Utiliza una API meteorológica externa (Tomorrow.io) para obtener los datos de clima y un caché basado en Redis.

Requisitos previos

Kotlin (versión x.x.x)
Ktor (versión x.x.x)
JDK 8+
Redis account
Tomorrow.io account


Instalación y ejecución

Clona este repositorio
Configura las variables de entorno:
 - Crear archivo .env en carpeta src
 - Definir variables:
      REDIS_HOST=your_redis_host
      REDIS_PORT=your_redis_port
      REDIS_PASSWORD=your_redis_password
      TOMORROW_API_KEY=tomorrow_api_key
  - Realizar build del proyecto:
      ./gradlew build
  - Correr proyecto
      ./gradlew run
