package com.cookedspecially.restaurantservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.restaurantservice.domain.Restaurant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

/**
 * Restaurant Event Publisher
 */
@Component
public class RestaurantEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RestaurantEventPublisher.class);

    private final SnsClient snsClient;

    // Constructor
    public RestaurantEventPublisher(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.restaurant-events-topic-arn}")
    private String restaurantEventsTopicArn;

    /**
     * Publish restaurant created event
     */
    @Async
    public void publishRestaurantCreated(Restaurant restaurant) {
        log.info("Publishing RESTAURANT_CREATED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RESTAURANT_CREATED);

        publishEvent(event);
    }

    /**
     * Publish restaurant updated event
     */
    @Async
    public void publishRestaurantUpdated(Restaurant restaurant) {
        log.info("Publishing RESTAURANT_UPDATED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RESTAURANT_UPDATED);

        publishEvent(event);
    }

    /**
     * Publish restaurant status changed event
     */
    @Async
    public void publishRestaurantStatusChanged(Restaurant restaurant, String oldStatus) {
        log.info("Publishing RESTAURANT_STATUS_CHANGED event for restaurant {}: {} -> {}",
            restaurant.getId(), oldStatus, restaurant.getStatus());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RESTAURANT_STATUS_CHANGED);

        publishEvent(event);
    }

    /**
     * Publish restaurant approved event
     */
    @Async
    public void publishRestaurantApproved(Restaurant restaurant) {
        log.info("Publishing RESTAURANT_APPROVED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RESTAURANT_APPROVED);

        publishEvent(event);
    }

    /**
     * Publish restaurant suspended event
     */
    @Async
    public void publishRestaurantSuspended(Restaurant restaurant) {
        log.info("Publishing RESTAURANT_SUSPENDED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RESTAURANT_SUSPENDED);

        publishEvent(event);
    }

    /**
     * Publish restaurant closed event
     */
    @Async
    public void publishRestaurantClosed(Restaurant restaurant) {
        log.info("Publishing RESTAURANT_CLOSED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RESTAURANT_CLOSED);

        publishEvent(event);
    }

    /**
     * Publish restaurant reopened event
     */
    @Async
    public void publishRestaurantReopened(Restaurant restaurant) {
        log.info("Publishing RESTAURANT_REOPENED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RESTAURANT_REOPENED);

        publishEvent(event);
    }

    /**
     * Publish menu updated event
     */
    @Async
    public void publishMenuUpdated(Restaurant restaurant) {
        log.info("Publishing MENU_UPDATED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.MENU_UPDATED);

        publishEvent(event);
    }

    /**
     * Publish rating updated event
     */
    @Async
    public void publishRatingUpdated(Restaurant restaurant) {
        log.info("Publishing RATING_UPDATED event for restaurant: {}", restaurant.getId());

        RestaurantEvent event = RestaurantEvent.fromRestaurant(
            restaurant, RestaurantEvent.EventType.RATING_UPDATED);

        publishEvent(event);
    }

    /**
     * Publish event to SNS
     */
    private void publishEvent(RestaurantEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);

            PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(restaurantEventsTopicArn)
                .message(message)
                .subject(event.getEventType())
                .build();

            PublishResponse response = snsClient.publish(publishRequest);

            log.info("Published event {} to SNS. MessageId: {}",
                event.getEventType(), response.messageId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", event, e);
        } catch (Exception e) {
            log.error("Failed to publish event to SNS: {}", event, e);
        }
    }
}
