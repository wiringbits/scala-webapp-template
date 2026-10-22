---
description: Tapir API inputs must bound every free-form field length and every collection size
files: lib/api/shared/.*\.scala
---

Review added/changed endpoint inputs in `lib/api/shared/` for one rule: every
new or changed input must declare explicit limits — a max length on every
free-form string, and a max element count on every list or page-size input.

Flag an unbounded field, name it, and suggest a concrete cap if one is obvious
(a name ≤ 100 chars, a page size ≤ 100); otherwise say the author must pick one.
Not: fields already bounded by a validated primitive in `lib/common/shared/`
(`Name`, `Email`, bounded opaque types), enums, or booleans.
