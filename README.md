# Hibernate Final Project

Java project for working with MySQL and Redis using Hibernate.

The application loads city and country data from MySQL, transforms it into JSON, stores it in Redis, and compares Redis and MySQL read performance.

## Technologies

- Java 21
- Hibernate
- MySQL
- Redis / Lettuce
- Jackson
- Maven
- Docker
- P6Spy
- JUnit 5
- JaCoCo

## Run

Start MySQL and Redis:

```bash
docker start mysql-javarush
docker start redis-javarush
```

Run tests:

```bash
mvn clean test
```

Build project:

```bash
mvn clean compile
```

## Testing

JUnit 5 is used for tests and JaCoCo for test coverage.

Coverage report:

```text
target/site/jacoco/index.html
```
