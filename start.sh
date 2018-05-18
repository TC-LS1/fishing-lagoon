export FISHING_LAGOON_DATABASE_FILE=mem: 
PORT=5000
while test $PORT -lt 5010; do
  PORT=$PORT java -cp target/classes:target/dependency/* com.drpicox.fishingLagoon.MainServer &
  PORT=$((PORT + 1))
done
