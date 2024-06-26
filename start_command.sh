#!/bin/bash

ELASTICSEARCH_HOST=${ELASTIC_HOST:-localhost}
ELASTICSEARCH_PORT=9200
MAX_ATTEMPTS=30
SLEEP_INTERVAL=5
RUN_COMMAND="java -jar dm.jar"

ELASTICSEARCH_URL="http://$ELASTICSEARCH_HOST:$ELASTICSEARCH_PORT"

check_elasticsearch() {
    local response
    response=$(curl -s -w "%{http_code}" "$ELASTICSEARCH_URL/_cat/health" -o /dev/null)
    echo "$response"
}

attempt=1
while [ $attempt -le $MAX_ATTEMPTS ]; do
    echo "Attempting to connect to Elasticsearch (Attempt $attempt/$MAX_ATTEMPTS)..."
    echo "IP and port are $ELASTICSEARCH_HOST, $ELASTICSEARCH_PORT)..."

    if [ "$(check_elasticsearch)" -eq 200 ]; then
        echo "Elasticsearch is ready!"
        echo "Executing run command: $RUN_COMMAND"
        $RUN_COMMAND
        exit 0
    fi

    echo "Elasticsearch is not ready yet. Retrying in $SLEEP_INTERVAL seconds..."
    sleep $SLEEP_INTERVAL
    ((attempt++))
done

echo "Failed to connect to Elasticsearch after $MAX_ATTEMPTS attempts. Exiting..."
exit 1