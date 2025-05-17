package infrastructure.ports;

import domain.Discount;
import domain.Event;
import domain.Tweet;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Repository {
    void initializeDatabase() throws Exception;
    ArrayList<Discount> getAllDiscounts() throws Exception;
    ArrayList<Event> getAllEvents() throws Exception;
    ArrayList<Tweet> getAllTweets() throws Exception;
    void save(Discount discount) throws Exception;
    void save(Event event) throws Exception;
    void save(Tweet tweet) throws Exception;
    Discount getDiscountById(String id) throws Exception;
    Event getEventById(String id) throws Exception;
    Tweet getTweetById(String id) throws Exception;
    ArrayList<Tweet> getTweetByEventId(String id) throws Exception;
    ArrayList<Event> getEventsByType(String type) throws Exception;
    boolean isDiscountApplied(String playerName) throws Exception;
}
