Key Features & Architecture
1. High-Concurrency Server Engine
Scalable Thread Management: Utilizes Java’s ExecutorService with a fixed thread pool to manage multiple client connections efficiently.

Producer-Consumer Model: Implements a robust request-handling architecture using BlockingQueue to decouple connection acceptance from command processing.

Thread-Safety: Core logic in Account and TransactionManager is optimized with synchronized blocks to ensure data integrity during simultaneous transactions.

2. Data Persistence (SQLite Integration)
Relational Database: Migrated from in-memory storage to SQLite for reliable data persistence.

Automated Schema Management: The DatabaseHandler ensures tables (users, accounts, transactions) are automatically initialized on server startup.

Audit Trail (Requirement 18): Every financial movement is logged into a dedicated transaction ledger for real-time auditing and historical retrieval.

3. Security & Integrity
Credential Protection: Integrated Password4j for secure password hashing and verification.

Input Sanitization: Strict validation in UserInterface to prevent command injection and unauthorized character usage (e.g., | filtering).

Infrastructure: Designed to run behind an Nginx reverse proxy to mask backend architecture and enhance traffic management.

4. CI/CD & Quality Assurance
Automated Testing: Integrated GitHub Actions pipeline to run JUnit tests (mvn test) automatically on every push to the feature/distributed-banking-architecture branch.

Unit Testing: Comprehensive test suite covering transaction limits, thread-safe balance updates, and database connectivity.

 Tech Stack
Language: Java 17

Database: SQLite (JDBC)

Security: Password4j

Infrastructure: Nginx, GitHub Actions

Build Tool: Maven

 Project Structure
src/main/java/newbank/server/: Core banking logic, ExecutorService implementation, and DatabaseHandler.

src/main/java/newbank/client/: User interface and socket communication logic.

.github/workflows/: CI/CD pipeline configurations.

Note: The nginx.conf file is currently not included in this repository to allow for environment-specific configurations (e.g., SSL certificate paths, specific port mapping). However, the backend is fully optimized to operate behind a reverse proxy. If the team requires a standardized Nginx template for deployment, I can provide a pre-configured version upon request

Please see : https://trello.com/b/JVe1Ppa8/uobgroup03newbank
