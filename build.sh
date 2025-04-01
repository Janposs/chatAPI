#!/bin/bash
#just for my convenience
#I'm pretty sure there are better ways. The point is I don't want to rebuild
#the backend all the time while testing the frontend
#calling build with no arguments only rebuilds the frontend

docker-compose down

if [[ $@ =~ "-new-cert" ]]; then
	echo "creating new certificate"
	cd src/main/resources
	if [ -d "certs" ]; then
		echo "directory exists"
	else
		mkdir certs
	fi
	cd certs
	
	openssl genrsa -out keypair.pem 2048
	openssl rsa -in keypair.pem -pubout -out public.pem
	openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
	
fi

if [[ $@ =~ "-clean" ]]; then
	echo "cleaning up ..."
	docker volume rm chat_pgdata
	docker system prune -a -f
fi

mvn package
docker compose build --no-cache
