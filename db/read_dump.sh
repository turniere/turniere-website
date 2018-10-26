#!/bin/bash
cat dump | docker exec -i `docker ps -aqf "name=postgres"` psql -U postgres -d turniere
