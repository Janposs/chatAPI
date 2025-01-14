#!/bin/bash
#just for my convenience
if [[ "$1" == "-clean" ]]; then
    docker-compose down
    docker volume rm chat_pgdata
    docker system prune -a -f
fi

echo "STARTING MAVEN BUILD"
maven=$(mvn package)
if [[ $maven == *"[ERROR]"* ]]; then
    echo "maven build failed. exit"
    echo "$maven"
    exit
fi
docker-compose build --no-cache