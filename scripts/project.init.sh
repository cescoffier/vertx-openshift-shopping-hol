#!/bin/bash
oc new-project vertx-workshop
oc policy add-role-to-user view admin -n vertx-workshop
oc policy add-role-to-user view -n vertx-workshop -z default
oc policy add-role-to-group view system:serviceaccounts -n vertx-workshop
oc create -f scripts/templates/redis/redis.yaml
oc create -f scripts/templates/redis/redis-rc.yaml
oc create -f scripts/templates/redis/redis-service.yaml
cd pricer-service
mvn clean fabric8:deploy
