#!/usr/bin/env bash
echo "Waiting for the DB to start"
sleep 20

echo "Generating migrations"
python manage.py makemigrations
echo "Migrating"
python manage.py migrate
