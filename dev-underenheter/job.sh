#!/usr/bin/env bash

set -eu

# Setup logging as JSON
exec 100>&1 200>&2
coproc JSON_LOGGER { jq --unbuffered -Rc '{"@timestamp": now|strftime("%Y-%m-%dT%H:%M:%S%z"),
                                           "message":.}' 1>&100 2>&200; }
exec 1>&${JSON_LOGGER[1]} 2>&${JSON_LOGGER[1]}
# End setup JSON logging

for doc in /data/*.json; do
    orgnr=$(basename $doc .json)
    curl -sS -u "$ES_USER:$ES_PASSWORD" -XPOST -H 'Content-type: application/json' --data-binary @"$doc" "$ES_STILLING_BACKEND/underenhet/_doc/$orgnr?pretty=true"
    echo
    echo "Indexed dev-underenhet $orgnr"
done

sleep 1
echo Finished.
