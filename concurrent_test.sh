#!/bin/bash

LOG_FILE="client_execution_log.txt"
TIMEOUT_LIMIT=5  # Tiempo límite de espera para cada cliente (en segundos)
NUM_CLIENTS=30
FIB_NUMBER=3000
JAR_PATH="C:\Users\USUARIO\Desktop\Universidad\Semestre_6\ChatCallback\client\build\libs\client.jar"

> $LOG_FILE

run_client() {
  local client_id=$1
  echo "$(date '+%H:%M:%S') - Cliente $client_id: Iniciando ejecución con Fibonacci($FIB_NUMBER)" >> $LOG_FILE

  if timeout $TIMEOUT_LIMIT java -jar "$JAR_PATH" "user$client_id" "$FIB_NUMBER" > /dev/null 2>&1; then
    echo "$(date '+%H:%M:%S') - Cliente $client_id: Ejecución completada" >> $LOG_FILE
  else
    if [ $? -eq 124 ]; then
      echo "$(date '+%H:%M:%S') - Cliente $client_id: Tiempo de espera excedido (timeout)" >> $LOG_FILE
    else
      echo "$(date '+%H:%M:%S') - Cliente $client_id: Error durante la ejecución" >> $LOG_FILE
    fi
  fi
}

echo "Iniciando prueba con $NUM_CLIENTS clientes simultáneos" >> $LOG_FILE
echo "Número de Fibonacci a calcular: $FIB_NUMBER" >> $LOG_FILE
echo "Tiempo límite por cliente: $TIMEOUT_LIMIT segundos" >> $LOG_FILE
echo "----------------------------------------" >> $LOG_FILE

for ((i=1; i<=NUM_CLIENTS; i++))
do
  run_client $i &
done

wait

echo "----------------------------------------" >> $LOG_FILE
echo "Prueba completada. Revisa la salida del servidor para ver los resultados de los cálculos."