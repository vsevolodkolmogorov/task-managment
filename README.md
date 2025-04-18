# Task Management Project

## Description
This is a task management application that uses Spring Boot and PostgreSQL as the database.

## Technologies:
- Java 17
- Spring Boot
- PostgreSQL
- Docker
- Docker Compose

## Local Setup

### Steps:

1. Clone the repository:
   ```bash
   git clone <repo_url>
   cd <project_directory>
   ```
2. Build the project: 
   Before running the project, make sure you have Maven installed and run the following command:
   ```bash
    mvn clean package
   ```

3. Build and start the containers using Docker Compose: 
To build and run the application with the database, use the following command:
   ```bash
    docker-compose up --build
   ```

4. Access the application: 
Once the containers are up and running, you can access the application at the following address:
   ```bash
   http://localhost:8080
   ```

5. To stop the containers:
   ```bash
   docker-compose down
   ```

