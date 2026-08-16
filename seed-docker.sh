#!/bin/bash
# =============================================================================
# NexusEnroll 2.0 - Load Seed Data into Docker MySQL (Linux / macOS)
# =============================================================================

echo "================================================="
echo "  NexusEnroll 2.0 - Docker Data Seeder"
echo "================================================="

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SEED_FILE="${SCRIPT_DIR}/sql/02-seed-all-data.sql"

# Check if nexus-mysql is running
if [ -z "$(docker ps -q -f name=nexus-mysql)" ]; then
    echo "Starting MySQL container via docker compose..."
    docker compose up -d mysql
    echo "Waiting 10 seconds for MySQL to initialize..."
    sleep 10
fi

echo "Loading seed data into Docker MySQL..."
if [ -f "$SEED_FILE" ]; then
    docker exec -i nexus-mysql mysql -u root -ppassword < "$SEED_FILE"
    if [ $? -eq 0 ]; then
        echo "Seed data successfully loaded into all 8 microservices databases!"
        echo ""
        echo "Demo Credentials:"
        echo "  Admin:   admin    / Password123"
        echo "  Faculty: faculty1 / Password123 (Sarah Connor)"
        echo "  Faculty: faculty2 / Password123 (Albert Einstein)"
        echo "  Student: student1 / Password123 (John Doe)"
        echo "  Student: student2 / Password123 (James Bond)"
    else
        echo "Error occurred while executing seed script in container."
    fi
else
    echo "Seed file not found at: $SEED_FILE"
fi
