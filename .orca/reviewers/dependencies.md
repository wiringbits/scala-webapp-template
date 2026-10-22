---
description: Dependencies must not change unless the task strongly requires it — build.sbt, sbt plugins/versions, package.json, lockfiles
files: (build\.sbt|project/.+\.(sbt|properties)|package\.json|yarn\.lock)
---

Review changes to build and dependency manifests. Flag, on the added/changed
lines:

- A dependency **added, removed, or version-changed** in `build.sbt`,
  `project/plugins.sbt`, `project/build.properties` (sbt/Scala version), or
  `package.json` — unless the task clearly requires that exact change.
- **Incidental lockfile churn**: a `yarn.lock` whose entries changed without a
  matching, intended `package.json` change. That is almost always a side effect
  of a local build (transitive bumps) and should be reverted, not committed.

The default is to keep the smallest footprint: reuse what the stack already
declares; do not upgrade a version, add an sbt plugin, or pull in a new library
to do something the existing dependencies already support.

Not: a dependency change the feature genuinely needs and that the PR description
calls out and justifies.
