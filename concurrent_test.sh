#!/bin/bash

LOG_FILE="client_execution_log.txt"
TIMEOUT_LIMIT=10  # Tiempo límite de espera para cada cliente (en segundos)
NUM_CLIENTS=25
FIB_NUMBER=3000
JAR_PATH="C:\Users\USUARIO\Desktop\Universidad\Semestre_6\ChatCallback\client\build\libs\client.jar"

> $LOG_FILE

# Función para ejecutar un cliente
run_client() {
    local client_id=$1
    local start_time=$(date +%s%N)

    if timeout $TIMEOUT_LIMIT java -jar "$JAR_PATH" "user$client_id" $FIB_NUMBER; then
        local end_time=$(date +%s%N)
        local duration=$(( (end_time - start_time) / 1000000 ))
        echo "Cliente $client_id completado en $duration ms" >> $LOG_FILE
    else
        echo "Cliente $client_id ha alcanzado el timeout de $TIMEOUT_LIMIT segundos" >> $LOG_FILE
    fi
}

# Ejecutar clientes en paralelo
for i in $(seq 1 $NUM_CLIENTS); do
    run_client $i &
done

wait

echo "Todos los clientes han terminado. Resultados en $LOG_FILE"