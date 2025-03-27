# CoinDesk Project - H2 Database Setup

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
