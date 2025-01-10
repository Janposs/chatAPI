#!/bin/bash
#just for my convenience
if [[ "$1" == "-clean" ]]; then
    docker system prune -a -f
fi

mvn package
docker-compose build --no-cache