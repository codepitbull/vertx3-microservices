#!/bin/bash

BASE_FILE=haproxy.config_base
NEW_CONFIG=haproxy.config_new
OLD_CONFIG=haproxy.config

IFS=$'\n'
ACL=()
USE_BACKEND=()
BACKENDS=()

for SERVICE in $(etcdctl ls /)
do
	RESPONSE=$(etcdctl ls $SERVICE)
	if [ "${#RESPONSE[0]}" -gt 0"" ]
	then
		RAWSERVICE=${SERVICE///}
		ACL+=("    acl is-${RAWSERVICE}-path path_dir /${RAWSERVICE}")
		USE_BACKEND+=("    use_backend ${RAWSERVICE} if is-${RAWSERVICE}-path")

		BACKENDS+=("backend ${RAWSERVICE}"$'\n'"    mode http"$'\n')

		for NODE in $(etcdctl ls $SERVICE)
		do
			BACKENDS+=("    server ${NODE///} $(etcdctl get $NODE)"$'\n')
		done
	fi
done

cp $BASE_FILE $NEW_CONFIG

for a in ${ACL[@]}
do
	echo $a >> $NEW_CONFIG
done

for a in ${USE_BACKEND[@]}
do
	echo $a >> $NEW_CONFIG
done

for a in ${BACKENDS[@]}
do
	echo $a >> $NEW_CONFIG
done

cp $NEW_CONFIG $OLD_CONFIG
haproxy -f haproxy.config -p ./haproxy.pid -sf $(cat ./haproxy.pid)

