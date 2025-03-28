# Diagrams

This directory contains diagrams for the CoinDesk project.

## Entity-Relationship Diagram

The `CoinDesk_ER_Diagram.puml` file contains the PlantUML definition for the database schema. It shows the relationship between the `coins` and `coin_i18n` tables.

A pre-rendered PNG version of the diagram is available as `CoinDesk_ER_Diagram.png` for quick reference without needing to render the PlantUML file.

### Viewing the Diagram

To view the diagram:

1. Open the provided `CoinDesk_ER_Diagram.png` file directly

2. Or render the PlantUML file yourself:
   - Install a PlantUML viewer/renderer:
     - IntelliJ IDEA has a PlantUML plugin
     - VS Code has PlantUML extension
     - Online viewer: [PlantUML Web Server](http://www.plantuml.com/plantuml/uml/)
   - Open the `.puml` file with your PlantUML viewer

### Updating the Diagram

When you make changes to the database schema:

1. Update the `CoinDesk_ER_Diagram.puml` file to match the new schema
2. Re-render the diagram to generate an updated PNG
3. Replace the existing PNG file with the new version

This ensures both the source file and the rendered image stay in sync with the actual database structure.

### Diagram Overview

![CoinDesk Entity-Relationship Diagram](./CoinDesk_ER_Diagram.png)

The current diagram shows:
- `coins` table with basic coin information
- `coin_i18n` table with internationalized names
- One-to-many relationship between coins and their translations
