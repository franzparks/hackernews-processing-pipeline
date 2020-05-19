package cscie88a.finalproject.model;

import java.util.Objects;

public class NewsEvent {
    private String eventId;
    private int rank;
    private String title;
    private String link;
    private String comments;
    private String timestamp;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsEvent newsEvent = (NewsEvent) o;
        return rank == newsEvent.rank &&
                Objects.equals(eventId, newsEvent.eventId) &&
                title.equals(newsEvent.title) &&
                link.equals(newsEvent.link) &&
                comments.equals(newsEvent.comments) &&
                Objects.equals(timestamp, newsEvent.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, rank, title, link, comments, timestamp);
    }
}
