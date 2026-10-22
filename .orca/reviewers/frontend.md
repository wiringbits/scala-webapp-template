---
description: Frontend data fetching must go through AsyncComponent
files: web/src/main/scala
---

Review changes under `web/src/main/scala`. Flag a component that loads data from
the API without using `AsyncComponent` — ad-hoc effects, state, and manual
loading/error flags to fetch API data should be an `AsyncComponent`, as the
existing pages and widgets are.

Not: components that don't fetch from the API; effects unrelated to data loading.
