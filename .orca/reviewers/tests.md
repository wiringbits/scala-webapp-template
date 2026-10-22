---
description: Test policy — no PII fixtures, fakes/real over mocks, no external calls, no redundant action tests, readable JSON, method-only descriptions
files: src/test/scala
---

Review changes under test sources. Flag:

- Any PII in fixtures or test data — real names, emails, addresses, phone/card
  numbers. Fixtures must be synthetic placeholders.
- A mock used outside an integration/controller test, or outside testing an
  external client's own parsing. Elsewhere use a fake implementation or the real
  one (Postgres via docker). Mocking an external response's JSON to check our
  client parses it is fine.
- A test that calls a real external service.
- Action-level tests for behaviour a controller test already exercises through
  that same action — don't duplicate; the controller test covers it.
- A single-line JSON payload — JSON in tests must be multi-line and
  human-readable.
- A test description that includes the spec/class name
  (`"DocumentParserSpec.parse"`) — name the method only (`"parse"`).

Not: unit tests of pure logic using fakes; integration tests backed by docker
Postgres.
