WIRD-BACKEND 

DESCRIPCIÓN

Este proyecto es un servicio para la consulta y almacenamiento en caché de datos meteorológicos de varias ciudades. 
Utiliza una API meteorológica externa (Tomorrow.io) para obtener los datos de clima y un caché basado en Redis.

REQUISITOS PREVIOS

-  Kotlin (versión x.x.x)
-  (versión x.x.x)
-  JDK 8+
-  Redis account
-  Tomorrow.io account


INSTALACIÓN Y EJECUCIÓN LOCAL

- Clona este repositorio
- Configura las variables de entorno:
  - Crear archivo .env en carpeta src
  - Definir variables:
      REDIS_HOST=your_redis_host
      REDIS_PORT=your_redis_port
      REDIS_PASSWORD=your_redis_password
      TOMORROW_API_KEY=tomorrow_api_key
- Realizar build del proyecto:
   directamente desde el ide o ejecutando ./gradlew build
- Correr proyecto
   directamente desde el ide o ejecutando ./gradlew run


INSTALACIÓN Y EJECUCIÓN PRODUCCIÓN RAILWAY DESDE GITHUB

- Crear cuenta en Railway
- Vincular proyecto de github (Deploy from github repo)
- En la nueva pestaña abrir proyecto y en pestaña Variable ingresar variables de entorno
      REDIS_HOST=your_redis_host
      REDIS_PORT=your_redis_port
      REDIS_PASSWORD=your_redis_password
      TOMORROW_API_KEY=tomorrow_api_key
- En pestaña Settings apartado Networking generar dominio (url)
- Ejecutar deploy
