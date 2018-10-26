#!/bin/bash
docker exec -i `docker ps -aqf "name=postgres"` pg_dump -U postgres turniere > dump
