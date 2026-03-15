#!/bin/bash

set -e  # Stop script if any command fails

echo "Pulling latest images..."
docker compose pull

echo "Restarting containers..."
docker compose up -d

echo "Deployment complete!"