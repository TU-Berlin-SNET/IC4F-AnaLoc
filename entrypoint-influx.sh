#!/bin/bash
# forked from https://stackoverflow.com/questions/55471438/docker-influxdb-restore-db-on-start-up
# modified by Yong Wu
if [ ! -f "/var/lib/influxdb/.init" ]; then
    exec influxd $@ &

    until wget -q "http://localhost:8086/ping" 2> /dev/null
    do
        sleep 1
    done

    influx -database=ArenaEmulator -import -path=dataSource.txt -precision=ns
    
    touch "/var/lib/influxdb/.init"

    kill -s TERM %1
fi

exec influxd $@