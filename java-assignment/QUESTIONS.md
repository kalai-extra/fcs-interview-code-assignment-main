# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt

The codebase currently uses a "split personality" architecture, mixing two distinct patterns: Active Record and Data Mapper.

If I were maintaining this codebase, I would definitely refactor it to achieve architectural consistency. Here is how I would approach it and why:

1. The Conflict: Active Record vs. Data Mapper
Active Record (The Store approach): The Store entity extends PanacheEntity and handles its own database operations (e.g., Store.findById(id)).

Data Mapper (The Warehouse approach): You have a separate WarehouseRepository that maps a clean Warehouse domain object to a DbWarehouse database entity.

2. My Refactor Strategy: Standardize on the Data Mapper Pattern
I would refactor the Store module to match the Warehouse pattern (Data Mapper).

Why?

Separation of Concerns: In the current Store implementation, the business logic and database schema are tightly coupled in one class. If the database schema changes, you risk breaking business logic.

Testability: Data Mappers make unit testing much easier. You can mock the WarehouseRepository without needing a database connection. Mocking static methods like Store.findById() is notoriously difficult and usually requires specialized tools like PowerMock.

Domain Purity: The Warehouse domain model is "clean"—it doesn't know about Hibernate, Panache, or JPA. This makes the code more portable and easier to understand for someone who isn't a database expert.

3. Refactoring the Legacy Integration
Currently, the "Legacy Sync" logic is inside the Resource or UseCase. I would move this to a Domain Event pattern.

Instead of manually registering a JTA synchronization in every method, I would:

Fire a CDI Event: event.fire(new StoreUpdatedEvent(store)).

Create a Listener: Use @Observes(during = TransactionPhase.AFTER_SUCCESS).

Why? * It removes boilerplate code from your business logic.

It ensures that adding a new "side effect" (like sending an email or notifying another service) doesn't require changing the core update or create methods.

4. Summary for your submission

Consistency: "The current mix of patterns increases cognitive load for developers."

Decoupling: "Standardizing on repositories allows for better unit testing and a cleaner domain model."

DRY (Don't Repeat Yourself): "Moving legacy sync to an event-driven listener prevents logic duplication."
`
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
This question touches on the debate between Design-First (OpenAPI YAML) and Code-First (Direct Coding). Both have their place, but in a professional environment, the choice usually depends on the scale of the team and the stability of the requirements.1. Design-First (The Warehouse Approach)In this approach, you write the YAML specification first and use a generator (like the openapi-generator-maven-plugin) to create the interfaces and models.Pros:Contract as Truth: The API contract is agreed upon before a single line of code is written.
 Front-end and back-end teams can work in parallel because the mock responses are already defined.Documentation for Free: Your Swagger/OpenAPI UI is always 100% accurate and up to date.Consistency: The generator ensures that all endpoints follow the same naming conventions, error structures, and data formats.Cons:Verbosity: Writing YAML/JSON can be tedious and prone to syntax errors.Tooling Overhead: You have to manage a code-generation step in your build process (Maven/Gradle), which can sometimes lead to "hidden" code that is hard to debug.
 2. Code-First (The Store & Product Approach)In this approach, you write the JAX-RS/Quarkus resource classes and use annotations like @Schema to generate the documentation.Pros:Speed and Agility: It is much faster for small teams or prototyping. You stay in the Java code without switching back and forth to YAML.Flexibility: You have total control over the implementation details without being constrained by what the generator produces.Cons:Documentation Drift: It’s easy to change a field in Java and forget to update the annotations, leading to a mismatch between the documentation and the actual API.Coupling: The contract is "hidden" inside the implementation. A client developer has to wait for you to finish the code to see how the API behaves.
 3. Comparison Table
 Feature        Design-First (YAML)         C ode-First (Java)
 
 Best ForL      arge teams, Public APIs         Internal tools, PrototypingP
 Parallelism    High (Teams can use mocks)      Low (Wait for implementation)
 Syncing        Contract is always the lead     Docs often trail behind code
 Effort         Higher initial effort           Fast start, high maintenance
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
In a fulfillment environment where business rules (like capacity and stock matching) are the most critical part of the system, I would prioritize tests based on Risk and Business Value.

Here is my strategy for effective, sustainable test coverage:

1. Prioritization: The Testing Pyramid
I would focus heavily on the base and middle of the pyramid to ensure speed and reliability.

High Priority: Unit Tests (The "Logic" Layer)

Focus: Testing individual business rules in the Use Cases (e.g., "Does the replacement fail if stock doesn't match?").

Implementation: Use JUnit 5 and Mockito to mock the WarehouseStore and LocationGateway.

Why: These are fast to run and catch 80% of bugs during development.

Medium Priority: Integration Tests (The "Contract" Layer)

Focus: Testing the API endpoints via WarehouseEndpointIT.

Implementation: Use @QuarkusTest and RestAssured.

Why: This ensures that the JSON mapping, status codes (201, 404, 422), and database transactions are working correctly together.

Lower Priority: End-to-End (The "Workflow" Layer)

Focus: A full "Happy Path" (Create Location -> Create Warehouse -> Replace Warehouse -> Archive).

Why: These are fragile and slow; I would only maintain a few of these for the most critical revenue-generating flows.

2. Ensuring Effective Coverage Over Time
To keep coverage from "rotting" or becoming a checkbox exercise, I would implement:

A. Boundary Value Analysis
Instead of just testing "valid" data, I would focus on the edges.

Example: If a location has a maxWarehouses of 5, I would write tests for exactly 4 (pass), 5 (pass), and 6 (fail).

B. Automated Coverage Reporting (Jacoco)
I would integrate a tool like Jacoco into the Maven build.

Action: Set a "Quality Gate" where the build fails if test coverage drops below a certain percentage (e.g., 80%).

Why: This prevents developers from adding new features without adding corresponding tests.

C. Mutation Testing (Optional but Advanced)
Occasionally run a tool like PITest.

How It changes the code slightly (e.g., changing a > to a >=) and checks if your tests fail. If the tests still pass, it means your test suite isn't actually "watching" that logic closely enough.

If I had to choose where to spend my final 30 minutes, I would focus on:

Validation Logic: Testing the WebApplicationException triggers in CreateWarehouseUseCase.

Concurrency: Ensuring @Transactional works so that two people can't "fill" the same warehouse slot at the exact same millisecond.
```