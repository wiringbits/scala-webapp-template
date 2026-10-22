---
description: Server tests must use random, globally-unique data — the suite shares one Postgres container (and may run in parallel), so hardcoded unique values collide
files: server/src/test/scala
---

The server test suite runs against a SINGLE Postgres container that is shared across
specs and NOT reset between tests (and specs may run in parallel). So any value that is
UNIQUE across the whole database must be RANDOM per test, or tests collide — e.g. a
`duplicate key value violates unique constraint "users_email_unique"` failure.

Flag a test that inserts a HARDCODED value into a globally-unique column: a fixed email
(`Email.trusted("a@test.com")`), a fixed user name, etc. Use the `utils.RandomData`
helpers (`randomEmail()`, `randomName()`, `randomString()`), or `createUser()`'s random
default, instead.

Not: a value unique only PER USER and scoped by a random `user_id`, or an assertion that
deliberately pins a value the test itself created — those don't collide across tests.
A spec that owns global state via `IsolatedPostgresSpec` (its own container) is also fine.
