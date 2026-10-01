#!/bin/bash
set -e

BACKUP_DIR="$(dirname "$0")"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
OUTPUT_PATH="$BACKUP_DIR/neo4j_backup_$TIMESTAMP.dump"
CONTAINER_NAME="pds-neo4j"

if ! docker ps | grep -q "$CONTAINER_NAME"; then
  echo "Neo4j container is not running. Start it with: docker compose up -d"
  exit 1
fi

echo "Taking Neo4j dump at $TIMESTAMP"
docker exec "$CONTAINER_NAME" neo4j-admin database dump --to-path=/var/lib/neo4j/import "neo4j"

# Move the generated dump from the container to host backup directory
if docker exec "$CONTAINER_NAME" ls /var/lib/neo4j/import/neo4j.dump >/dev/null 2>&1; then
  docker cp "$CONTAINER_NAME:/var/lib/neo4j/import/neo4j.dump" "$OUTPUT_PATH"
  echo "Backup created: $OUTPUT_PATH"
else
  echo "Backup file not found in container import directory"
  exit 1
fi

# Optional: keep only the last 30 backups
find "$BACKUP_DIR" -type f -name 'neo4j_backup_*.dump' -mtime +30 -delete
