#!/bin/bash
#brew install haproxy
killall haproxy
rm haproxy.pid
rm haproxy.config
rm haproxy.config_new
./build_cfg_and_reload.sh
etcdctl exec-watch --recursive / -- ./build_cfg_and_reload.sh