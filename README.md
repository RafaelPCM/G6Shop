Ao baixar o código, é necessário adicionar o *application.properties* no local indicado de acordo com a documentação

Modelo de application.properties (deve ser ajustado de acordo como a máquina onde vai ser instalado):

```
#spring.jpa.hibernate.ddl-auto=updatespring.datasource.url=jdbc:mysql://111.22.333.444:9999/g6shop

#spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
#spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.username=senha
spring.datasource.password=senha

spring.jpa.open-in-view=falsespring.mvc.format.date=yyyy-MM-dd

spring.mvc.format.date-time=yyyy-MM-dd'T'hh:mm
spring.mvc.format.time=HH:mm:ss

admin.standardUserName=teste
admin.standardPassword=teste

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

Comando para compilar e executar servidor em segundo plano:
mvn package & start java -jar target/dependency/webapp-runner.jar --port 5339 target/*.war

Comando só para compilar:
mvn package

Comando só para executar servidor em segundo plano:
start java -jar target/dependency/webapp-runner.jar --port 80 target/*.war

## Relatório de cobertura de código:

Somente na primeira vez:

```
mvn clover:instrument
mvn clover:clover
```

Nas vezes subsequentes

``mvn clover:clover``

Documentação:

``mvn javadoc:javadoc``
