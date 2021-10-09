#!/usr/bin/env bash
sbt admin/compile || mv ./admin/target/scala-2.13/scalajs-bundler/main/node_modules/ra-core/esm/form/submitErrorsMutators.d.ts ./admin/target/scala-2.13/scalajs-bundler/main/node_modules/ra-core/esm/form/submitErrorsMutators.d.ts.bak
sbt admin/compile

