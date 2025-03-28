# CoinDesk Project - H2 Database with Flyway Migration

## Docker Compose Database

### Starting Database without Persistent Storage
```bash
docker-compose up -d
```

### Starting Database with Persistent Storage
```bash
docker-compose -f docker-compose.yml -f docker-compose.h2.volume.yml up -d
```

### Stopping the Database
```bash
docker-compose down
```

### Stopping the Database with Volume
```bash
docker-compose -f docker-compose.yml -f docker-compose.h2.volume.yml down
```

### Notes
- Default setup is for local testing without persistent storage
- Use the additional compose file to enable volume persistence

## Flyway Database Migration

### Profile-based Migrations
Flyway migrations are organized in profile-specific directories:
- `src/main/resources/db/migration/common` - Schema migrations for all environments
- `src/main/resources/db/migration/local` - Sample data only for local development

Current migrations:
- `common/V1__Create_coins_table.sql` - Creates the initial coins table (all environments)
- `local/V1_1__Insert_sample_coins.sql` - Adds sample coin data (local only)
- `common/V2__Create_coin_i18n_table.sql` - Creates the i18n names table (all environments)
- `local/V2_1__Insert_sample_coin_i18n.sql` - Adds sample i18n data (local only)

### Adding New Migrations
Place migrations in the appropriate directory:
```
src/main/resources/db/migration/common/V3__Add_new_feature.sql     # For all environments
src/main/resources/db/migration/local/V3__Add_local_data.sql       # For local only
```

### Running with Different Profiles
- Local development (with sample data): `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
- Testing (no sample data): `./mvnw spring-boot:run -Dspring-boot.run.profiles=test`
- Production (no sample data): `./mvnw spring-boot:run -Dspring-boot.run.profiles=prod`

## Database Schema

An Entity-Relationship diagram is available in the `diagrams` folder. The diagram is created using PlantUML and shows the relationship between the tables in the database.

- `coins` - Stores basic coin information
- `coin_i18n` - Stores internationalized names for coins

For more details and a visual representation, see the [Diagrams](./diagrams/README.md).
