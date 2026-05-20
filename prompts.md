***

```markdown
# prompts.md

# AI Interaction and Prompts Log

## AI Model Used
The specific model used to assist in developing this assignment was Gemini[cite: 147].

## Usage Overview
Gemini was utilized as an AI programming assistant throughout the development of the IssueFlow backend[cite: 8]. The agent was used to plan the architecture, generate boilerplate Spring Boot code, implement complex business logic, and debug compilation errors. 

## Main Prompts and Interactions

### 1. Authentication and Security
* **Prompt:** "How can I implement JWT based authentication in a Spring Boot 3 project to secure all endpoints, including a POST /auth/login that accepts a username and password and returns a signed JWT access token?"
* **Result:** The agent provided the structure for `JwtAuthenticationFilter`, `AuthService`, and the necessary Spring Security configurations to protect the API[cite: 25, 27].

### 2. CSV Export and Import
* **Prompt:** "Write a Spring Boot service to export tickets to a CSV file, and another method to import tickets from a multipart CSV file. The import must handle commas and quotes inside field values and return a summary JSON with created, failed, and errors arrays."
* **Result:** The agent suggested using the `org.apache.commons.csv` library and provided the parsing logic to meet the exact input and output requirements[cite: 87, 89, 91].

### 3. Soft Delete and Role Authorization
* **Prompt:** "How do I implement soft delete for JPA entities where deleted items are hidden by default, but create endpoints like GET /tickets/deleted that use native SQL queries and are strictly accessible only to ADMIN users?"
* **Result:** The agent guided the implementation of `@SQLRestriction` on the entities, custom native queries in the repositories, and the usage of `@PreAuthorize("hasRole('ADMIN')")` on the controllers[cite: 95, 97].

### 4. Compilation and Debugging
* **Prompt:** "I am getting a compilation error in CsvController: 'cannot find symbol class MultipartFile'. How do I fix this?"
* **Result:** The agent identified the missing imports and provided the corrected class structure to successfully compile the project.

### 5. Database Seeding for Testing
* **Prompt:** "Create a DataSeeder in Spring Boot to automatically insert dummy users and projects into the PostgreSQL database on startup, only if the database is empty, so I can test the API immediately."
* **Result:** The agent provided a `CommandLineRunner` implementation to seed the database safely without violating unique constraints.