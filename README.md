### 🏷️ Type: Interview Assignment

# Task Management

## 📌 Overview

This is a task management application that uses Spring Boot and PostgreSQL as the database.

## 🧩 Architecture

This project consists of the following services:

| Service            | Description |
|--------------------|-------------|
| **task-service**   | Manages tasks: CRUD operations, pagination, filtering by status, priority, author, and assignee |
| **auth-service**   | Handles user authentication, registration, and role-based authorization |
| **jwt-service**    | Provides JWT generation, validation, and security filters for authentication flow |
| **comment-service**| Manages comments linked to tasks, providing full CRUD functionality |


## 🛠️ Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Docker
- Docker Compose


## 🚀 Features

### auth-service
- User registration and login
- Role-based authorization
- JWT token generation and validation

### jwt-service
- Generates and validates JWT tokens
- Provides security filters for authentication
- Ensures secure communication between services

### task-service
- Full task management (CRUD operations)
- Pagination for listing tasks
- Filtering by status, priority, author, and assignee
- Tracks created and updated timestamps

### comment-service
- CRUD operations on comments
- Links comments to specific tasks
- Fetch comments by author or task ID

## ▶️ How to Run

### Steps:

1. Clone the repository:
   ```bash
   git clone <repo_url>
   cd <project_directory>
   ```

2. Create your own .env file in the root of the project by copying the example:
   ```bash
   cp .env.example .env
   ```

3. Edit the .env file to provide your own values if needed:

   ```bash
   DB_USERNAME=postgres
   DB_PASSWORD=your_secure_password
   DB_NAME=taskdb
   JWT_SECRET=your_base64_encoded_secret
   ```

4. Build the project: 
   Before running the project, make sure you have Maven installed and run the following command:
   ```bash
    mvn clean package
   ```

5. Build and start the containers using Docker Compose: 
To build and run the application with the database, use the following command:
   ```bash
    docker-compose up --build
   ```

6. Access the application: 
Once the containers are up and running, you can access the application at the following address:
   ```bash
   http://localhost:8080
   ```

7. To stop the containers:
   ```bash
   docker-compose down
   ```

