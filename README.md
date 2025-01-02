# Aplicación Bancaria en Spring Boot

1. Agregación de repositorios y entidades

* Se crearon las entidades User, Client, Account, AccountType, Transaction y TransactionAction, de acuerdo a lo descrito en el modelo ER descrito en la carpeta src/main/resources/docs.

* Se crearon las interfaces de repositorios para cada entidad, extendiendo de la interfaz JpaRepository

## Inicialización de la Aplicación

Para iniciar la aplicación solo es necesario correr el siguiente comando en una terminal que se encuentre ubicada en el directorio raíz del proyecto

```
./gradlew bootRun
```

Al hacerlo, el proyecto estará disponible en la dirección localhost:8080. En caso de querer visualizar las tablas creadas, puede ingresar a la dirección localhost:8080/h2-console e iniciar una conexión con las credenciales especificadas en el archivo src/main/resources/application.properties.
