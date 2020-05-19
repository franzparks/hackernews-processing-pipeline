package cscie88a.finalproject.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import cscie88a.finalproject.kafka.MessageProducer;
import cscie88a.finalproject.model.NewsEvent;
import cscie88a.finalproject.util.FileReader;
import cscie88a.finalproject.util.NewsEventParser;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Randomly picks sample data from input directory and generates sensor events in json format
 * to output directory
 */
public class KafkaDataGenerator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final Instant DEFAULT_START_DATE = Instant.now().truncatedTo(ChronoUnit.DAYS);
    public static final String DEFAULT_DAYS = "7";
    public static final String DEFAULT_TEST_TOPIC = "test";
    public static final String DEFAULT_KAFKA_URL = "localhost:9092";
    private String kafkaTopic ;
    private String kafkaUrl ;
    private String streamingFlag;
    private Integer streamingIntervalSec;
    Set<String> newsEventList = FileReader.readAllValuesFile("finalproject/input/hackernews.txt");
    String[] newsEventArray;

    Instant dayBeginningEpoch = Instant.now().truncatedTo(ChronoUnit.DAYS);
    MessageProducer kafkaProducer = null ;
    int numberOfDaysRange ;
    long daysInMillis;
    private int numberOfEvents;

    public KafkaDataGenerator() {
        newsEventArray = newsEventList.stream().toArray(String[]::new);
        System.out.println(Arrays.toString(newsEventArray));
        String startDateAsEpochString = System.getProperty("start_date_epoch_millis",null);
        String endDaysFromStartString = System.getProperty("end_no_days_from_start", DEFAULT_DAYS);
        validateStartParameters(startDateAsEpochString, endDaysFromStartString);
        daysInMillis = TimeUnit.DAYS.toMillis(numberOfDaysRange);
        String noOfEventsString = System.getProperty("no_of_events", "500");
        streamingFlag = System.getProperty("streaming", "y");
        String streamingIntervalString = System.getProperty("streaming_interval_sec", "10");
        numberOfEvents = Integer.parseInt(noOfEventsString);
        streamingIntervalSec = Integer.parseInt(streamingIntervalString);
        kafkaTopic = System.getProperty("kafka_topic", DEFAULT_TEST_TOPIC);
        kafkaUrl = System.getProperty("kafka_url", DEFAULT_KAFKA_URL);
        kafkaProducer = new MessageProducer(kafkaUrl, kafkaTopic);
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaProducer::closeProducer));
    }

    private void validateStartParameters(String startDateAsEpochString, String endDaysFromStartString) {
        if(startDateAsEpochString == null) {
            dayBeginningEpoch = DEFAULT_START_DATE;
        }else{
            try {
                dayBeginningEpoch = Instant.ofEpochMilli(Long.parseLong(startDateAsEpochString));
            } catch (NumberFormatException e) {
                System.out.println("invalid date. using default start date");
                dayBeginningEpoch = DEFAULT_START_DATE;
            }
        }

        try {
            numberOfDaysRange = Integer.parseInt(endDaysFromStartString);
        } catch (NumberFormatException e) {
            numberOfDaysRange = Integer.parseInt(DEFAULT_DAYS);
        }
    }

    public void generateData() throws IOException, InterruptedException {
        do{
            for(int i = 0; i< numberOfEvents; i++){
                NewsEvent event = new NewsEvent();

                event.setEventId(UUID.randomUUID().toString());
                event.setTitle(newsEventArray[i]); //TODO: break up input properly

                kafkaProducer.sendMessage(kafkaTopic, NewsEventParser.getNewsEventAsJsonString(event));
            }
            if(streamingFlag.equalsIgnoreCase("y")){
                System.out.println("******************Waiting for  "+ streamingIntervalSec +" seconds before it can produce again ******************" );
                TimeUnit.SECONDS.sleep(streamingIntervalSec);
                System.out.println("******************Generating events ******************" );
            }

        }while (streamingFlag.equalsIgnoreCase("y"));

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        KafkaDataGenerator dataGenerator = new KafkaDataGenerator();
        dataGenerator.generateData();
    }

}
