---
description: SQL evolution hygiene — one new evolution per PR, existing ones immutable, named constraints, FK indexes, no downs/defaults, casing
files: evolutions/default/.*\.sql
---

Review added or changed Play evolution files. Flag:

- More than one **new** evolution file added in a single PR (at most one per PR).
- Any change to an evolution that already exists on the base branch — merged
  evolutions are immutable; corrections go in a new numbered file.
- A `# --- !Downs` section or any down migration — this project has no downs on
  purpose.
- A foreign key without a matching index on the referencing column(s): deletes
  on the referenced table would full-scan. Every FK needs an index.
- An unnamed constraint — every primary key, foreign key, unique, and check
  constraint must be named explicitly (`CONSTRAINT <name> ...`).
- A column `DEFAULT` — this project does not use database defaults.
- SQL keywords not in UPPER CASE, or table/column identifiers not in
  snake_case.
