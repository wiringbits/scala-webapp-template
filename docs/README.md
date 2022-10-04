# Wiringbits Scala WebApp template - Docs

- [Setup development environment](./setup-dev-environment.md)
- [Architecture](./architecture.md)
- [Design desicions](./design-decisions.md)
- [Learning material](./learning-material.md)
- Swagger integration (ToDo).


## Diagrams

The docs include diagrams created with plantuml, you will need to compile them after they are updated.

Before you can compile those, you will require some dependencies:

1. `graphviz` which can be installed with `apt install graphviz`
2. Download `plantuml.jar` from the official [site](https://plantuml.com/starting).

Then, you can execute this to generate them all:
- `java -jar ~/Downloads/plantuml.jar ./diagram-sources -o ../assets/diagrams/`

Consider using the [plantuml plugin for IntelliJ](https://plugins.jetbrains.com/plugin/7017-plantuml-integration/) when editing diagrams, this way, you can preview the changes.
