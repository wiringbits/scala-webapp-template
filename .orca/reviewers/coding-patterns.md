---
description: Scala coding patterns — Chimney for case-class mapping, no dropped Futures, no silent defaults for missing data
---

Review the added/changed lines for the patterns below only. Report clear,
actionable cases with file and line; if none apply, report nothing.

## Use Chimney for case-class mapping
Flag a case class built from another by hand (`Target(a.x, a.y, ...)` or
`Target(x = src.x, ...)`) when the fields line up by name — use
`src.transformInto[Target]` / `src.into[Target].transform` instead. Not:
mappings that are mostly computation, or where most fields need custom handling.

## Never drop a Future
Flag a `Future[_]` in statement position whose value is neither returned,
awaited, assigned, nor composed into the result — it silently loses its errors
and ordering. Not: a Future that is returned, used, or chained in.

## No silent defaults for missing required data
Flag a parser/importer that invents a required value it cannot determine —
`LocalDate.now()`, a hardcoded year, `0` for a missing amount, a default
currency, `getOrElse(<guess>)`. It must fail (`Left`/throw) so the record is
rejected. Not: a default for a genuinely optional field.

## Prefer enums over stringly-typed values
Flag a `String` (or bare `Int` code) used to represent a fixed, known set of
values — a status, kind, type, mode — where a Scala `enum`/sealed trait should
model it. Not: genuinely free-form text (names, descriptions, external ids).

## Log user-scoped operations with the user id
Flag a `logger.*` statement describing an operation on a specific user's data
that omits the user id when a `userId` is in scope (or trivially derivable, e.g.
from a loaded entity) — include `userId=$userId` so the log is attributable, and
log it only after the user is authenticated/resolved, never before. Not: genuine
system/background logs with no user in scope (startup, an unauthenticated webhook
before the user is identified, a purely entity-keyed message where no userId is
available).
