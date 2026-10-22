---
description: Repository/DAO patterns — per-user queries scoped to the user, reusable queries in DAOs, transactions for multi-call operations
files: server/src/main/scala/net/wiringbits/repositories
---

Review changes in the repositories/DAO package. Flag:

- A `SELECT` or `UPDATE` affecting a single user's rows without a
  `WHERE user_id = ...` bound to the current user. Every per-user read and write
  must be scoped to its user.
- A reusable query written inline in a repository that belongs in a DAO — DAOs
  hold the reusable queries, repositories compose them.
- `withConnection` used where several DB calls run together (writing multiple
  records, or read-then-update). Multiple calls that must succeed or fail as a
  unit belong in one `withTransaction`.

Not: single-call reads/writes under `withConnection`; queries over global /
non-user-owned tables.
