#!/bin/bash

set -e  # Stop script if any command fails

echo "Navigating to app directory..."
cd ~/app/Appointment-booking-system

echo "Pulling latest code..."
git pull origin main

echo "Pulling latest images..."
docker-compose pull

echo "Restarting containers..."
docker-compose down
docker-compose up -d

echo "Deployment complete!"