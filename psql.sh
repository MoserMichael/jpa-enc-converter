#!/usr/bin/env bash -i

echo "*** connecting to postgress, started by docker-compose up -d ***"

export PGPASSWORD='1234'

psql -h localhost -p 5433 -U dbtestuser -d dbname


