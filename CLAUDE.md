# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

- Build (compile + test + package): `./gradlew build`
- Run all tests: `./gradlew test`
- Run a single test class: `./gradlew test --tests "org.example.auth.AuthControllerTest"`
- Run the Spring Boot app: `./gradlew bootRun` — requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` for a running Postgres instance, and `JWT_SECRET` (32+ bytes for HS256) in any non-local environment. All have dev-mode defaults in `application.yml`. `Main.java` is an unrelated scratch file (see Architecture) and is not the app entry point — that's `Application.java`.

CI (`.github/workflows/ci.yml`) runs `./gradlew build` on every push/PR to `master`.

## Architecture

This repository contains two unrelated pieces of code under `org.example`:

1. **A standalone BST exercise** — `BinarySearchTree.java` (generic, mutable binary search tree: insert/delete/contains/bfs/dfs/height) and `Main.java` (an IntelliJ scratch file, not wired into the Spring app). Tested by `BinarySearchTreeTest`.

2. **A Spring Boot JWT authentication service** — entry point `Application.java`. Split into three packages:
   - `org.example.auth` — `AuthController` exposes `/api/auth/login`, `/api/auth/register`, `/api/auth/logout`. `AuthExceptionHandler` is scoped with `@RestControllerAdvice(assignableTypes = AuthController.class)` deliberately, so its catch-all 500 handler doesn't swallow errors from other controllers added later.
   - `org.example.security` — `SecurityConfig` wires a stateless filter chain (`SessionCreationPolicy.STATELESS`); `JwtAuthenticationFilter` is registered via `addFilterBefore(..., UsernamePasswordAuthenticationFilter.class)` and builds the `Authentication` directly from the JWT's claims (no DB lookup per request). `JwtService` issues/validates HS256 tokens via jjwt.
   - `org.example.user` — `User`/`Role` JPA entities and their repositories.

   Two non-obvious constraints that span multiple files:
   - **The DB schema is owned externally.** `application.yml` sets `ddl-auto: validate`, not `update`/`create` — the `users`/`roles`/`user_roles` tables are created and migrated outside this app. Entity classes must be changed to match the real schema, never the other way around.
   - **JWTs are fully stateless by design.** There is no server-side session or token store: `/api/auth/logout` is a no-op (the client just discards the token), and `JwtAuthenticationFilter` trusts the token's embedded `roles` claim without re-checking the database — a deactivated user's existing token remains valid until it naturally expires (`JWT_EXPIRATION_MINUTES`).

### Test patterns

- `AuthControllerTest` uses `@WebMvcTest(AuthController.class)` with `@AutoConfigureMockMvc(addFilters = false)` and `@MockitoBean` for every collaborator (`UserRepository`, `RoleRepository`, `PasswordEncoder`, `AuthenticationManager`, `JwtService`) — no real DB or security filter chain involved.
- `JwtAuthenticationFilterTest` is a plain unit test with no Spring context at all — it mocks `JwtService`/`HttpServletRequest`/`FilterChain` directly and asserts on `SecurityContextHolder`.
