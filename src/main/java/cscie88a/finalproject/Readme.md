finalproject run steps :   
Build images :   


When you are in CSCIE88AFP-2020/fp-java-homework
Run `docker-compose -f docker/docker-compose-fp-finalproject.yaml build` to build docker image
Then to start the containers :
Run `docker-compose -f docker/docker-compose-fp-finalproject.yaml up -d` to run all containers

To check if kafka producer is producing message to input topic :
Run `docker exec -it docker_kafka_1 /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic input_topic`

^^ should show messages getting into `input_topic` every 30 seconds like :
`{"eventId":"c5f726d4-af4e-4ee3-bd70-7abd3a0a79a7","sensorId":"sensor-id-675","sensorType":"D","windDirection":"E","eventTimestamp":1585944518005,"zipCode":"82443","humidityPercentage":73.78018,"windSpeedInMPH":13,"pressureInAtm":0.0,"temperatureInCelcius":-3.0}
 {"eventId":"192e0517-8002-46eb-b53c-bdc403dc701c","sensorId":"sensor-id-803","sensorType":"A","windDirection":"W","eventTimestamp":1586352678305,"zipCode":"63555","humidityPercentage":81.09388,"windSpeedInMPH":66,"pressureInAtm":0.0,"temperatureInCelcius":-1.0}`

To check if kafka stream is processing :
Run `docker exec -it docker_kafka_1 /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic sensor_type_count_topic`

^^ should show processed counts getting into `sensor_type_count_topic`  :
`F : 536
 D : 538
 G : 555
 B : 541
 C : 595
 A : 562
 E : 573`

