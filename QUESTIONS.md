# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about
what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and
   manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**

```txt
I would refactor LocationGateway to keep this list in persistent storage or properties rather than having them in code.
Also for each entity, i would use DB adapters. Currently all adapters in warehouse entity. Also StoreResouce and
ProductResouce use db entities in API class. Those should be refactored to use separate model for API.

```

----

2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we
   generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be
   your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**

```txt
The OpenAPI YAML approach offers consistency and standardization making APIs easier to 
maintain and document. It supports an API-first design, allowing clear definitions before implementation. 
However, it requires initial setup and familiarity with the specification format. On the other hand code first apraoch
quickly provide result makding development speed. My preference is OpenAPI YAML as it provide maintainable and 
standardized API development process. Also direct coding speedup the project impelementation and giving quick result 
without much learning curve but it could be hard to maintain specially if we want to distribute APIs as a separate project. 
```

----

3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement
   tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains
   effective over time?

**Answer:**

```txt
I would prioritize integration tests for critical business logic and end-to-end tests for key API flows, as they 
provide broad coverage with fewer tests. I would also include unit tests for complex components. To maintain 
effective test coverage, I’d use test automation, CI/CD integration, and regularly review and update tests as the 
code evolves. Adopting a TDD mindset, where possible, can speed up test coverage and development by specifying 
exactly what is needed for certain use cases. Additionally, we can use AI assistants to generate test cases, 
especially for similar patterns or parameterized tests, can help the creation and maintenance of repetitive 
or data-driven test scenarios, saving time.
```
