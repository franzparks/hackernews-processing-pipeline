package cscie88a.finalproject.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import cscie88a.finalproject.model.NewsEvent;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class NewsEventDeSerializer implements Deserializer<NewsEvent> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public NewsEvent deserialize(String topic, byte[] data) {
        if (data == null)
            return null;

        NewsEvent newsEvent;
        try {
            newsEvent = objectMapper.readValue(data, NewsEvent.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return newsEvent;
    }

    @Override
    public void close() {

    }
}
