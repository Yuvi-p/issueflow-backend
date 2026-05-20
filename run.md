# run.md

# IssueFlow Backend: Setup and Run Instructions

[cite_start]This document provides the exact steps required to install dependencies, start the database, build the project, run the application, and execute the tests[cite: 145].

## Prerequisites
* Java 21 
* Docker and Docker Compose
* Maven (included via the `mvnw` wrapper)

## 1. Start the Database
[cite_start]The application relies on a local PostgreSQL database[cite: 140]. To start the database using the provided compose file, open your terminal in the root directory of the project and run:
```bash
docker-compose up -d

## 2. Install Dependencies and Build the Project
[cite: 140]. To install all necessary dependencies and build the application executable, run the following Maven command:
```bash
./mvnw clean install -DskipTests

## 3. Run the Application
[cite: 140]. Once the database is running and the project is built, start the Spring Boot backend server by executing:
```bash
./mvnw spring-boot:run
[cite: 140]. The application will start and listen for incoming REST API requests on http://localhost:8080.

## 4. Run the Tests
[cite: 140]. To execute the automated test suite and verify the system behavior, run:
```bash
./mvnw test