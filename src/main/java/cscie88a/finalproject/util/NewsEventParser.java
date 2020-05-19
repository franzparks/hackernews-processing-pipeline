package cscie88a.finalproject.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cscie88a.finalproject.model.NewsEvent;

public class NewsEventParser {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static NewsEvent getNewsEventFromString(String newsEventAsString) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(newsEventAsString, NewsEvent.class);
    }

    public static String getNewsEventAsJsonString(NewsEvent newsEvent) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(newsEvent);
    }
}
