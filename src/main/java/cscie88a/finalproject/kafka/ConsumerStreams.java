package cscie88a.finalproject.kafka;

import cscie88a.finalproject.model.NewsEvent;
import cscie88a.finalproject.serialize.NewsEventSerializer;
import cscie88a.finalproject.serialize.NewsEventDeSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Properties;

public class ConsumerStreams {
    String kafkaUrl ;
    String kafkaTopic ;
    String consumerName;
    KafkaStreams processingStream;
    public static Serde<NewsEvent> SENSOR_EVENT_SERDE = Serdes.serdeFrom(new NewsEventSerializer(), new NewsEventDeSerializer());


    public static void main(String[] args) {
        ConsumerStreams consumerStreams = new ConsumerStreams();
        consumerStreams.start();

    }

    public ConsumerStreams() {
        kafkaTopic = System.getProperty("kafka_topic", "test_topic");
        kafkaUrl = System.getProperty("kafka_url", "localhost:9092");
        consumerName = System.getProperty("kafka_consumer_id", "test-consumer-new3");
        String news_title_count_topic = System.getProperty("news_title_count_topic", "news_title_count_topic");
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, consumerName);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.TOPOLOGY_OPTIMIZATION, "all");
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, NewsEvent> stream = builder.stream(kafkaTopic, Consumed.with(Serdes.String(), SENSOR_EVENT_SERDE));

        /*KTable<String, Long> countByNewsTitle = stream.groupBy((key, value) -> value.getTitle())
                .count();

        countByNewsTitle.toStream()
                .mapValues((key,values) -> key +" : "+  values.toString())
                .to(news_title_count_topic, Produced.with(Serdes.String(), Serdes.String()));*/

        KTable<Windowed<String>, Long> countByNewsTitleMinute = stream.groupBy((key, value) ->
                value.getTitle() + " <--> " + value.getRank()
        )
        .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
        .count();

        countByNewsTitleMinute
                .toStream((key, value) -> key.key() + " " + key.window().start())
                .mapValues((key,values) -> key +" : "+  values.toString())
                .to(news_title_count_topic, Produced.with(Serdes.String(), Serdes.String()));

        processingStream = new KafkaStreams(builder.build(), config);
        Runtime.getRuntime().addShutdownHook(new Thread(processingStream::close));
    }

    public void start(){
        processingStream.start();
    }
}
