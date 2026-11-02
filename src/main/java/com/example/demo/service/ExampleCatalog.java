package com.example.demo.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ExampleCatalog {
    public List<Map<String, Object>> all() {
        return List.of(
            Map.of("id", 1, "category", "Kafka", "title", "Publish a message", "description", "Send a string message to a topic."),
            Map.of("id", 2, "category", "Kafka", "title", "Message key", "description", "Publish with a key to influence partition selection."),
            Map.of("id", 3, "category", "Kafka", "title", "Partitioned topic", "description", "Create a topic with multiple partitions."),
            Map.of("id", 4, "category", "Kafka", "title", "Consumer listener", "description", "Consume messages with @KafkaListener."),
            Map.of("id", 5, "category", "Kafka", "title", "Consumer group", "description", "Distribute records across consumers with group.id."),
            Map.of("id", 6, "category", "Kafka", "title", "Manual acknowledgement", "description", "Acknowledge only after successful processing."),
            Map.of("id", 7, "category", "Kafka", "title", "Retry topic", "description", "Retry transient failures before sending to a DLT."),
            Map.of("id", 8, "category", "Kafka", "title", "Dead-letter topic", "description", "Route exhausted failures to a dead-letter topic."),
            Map.of("id", 9, "category", "Kafka", "title", "Batch listener", "description", "Consume records in batches."),
            Map.of("id", 10, "category", "Kafka", "title", "Headers", "description", "Attach metadata headers to a Kafka record."),
            Map.of("id", 11, "category", "Kafka", "title", "JSON payload", "description", "Serialize and publish a structured payload."),
            Map.of("id", 12, "category", "Kafka", "title", "Request correlation", "description", "Add a correlation ID to message headers."),
            Map.of("id", 13, "category", "Kafka", "title", "Partition inspection", "description", "Inspect partition and offset metadata."),
            Map.of("id", 14, "category", "Kafka", "title", "Consumer concurrency", "description", "Configure concurrent listener containers."),
            Map.of("id", 15, "category", "Kafka", "title", "Transactional publish", "description", "Publish records inside a Kafka transaction."),
            Map.of("id", 16, "category", "Kafka", "title", "Idempotent producer", "description", "Enable producer idempotence for safe retries."),
            Map.of("id", 17, "category", "Kafka", "title", "Producer callback", "description", "Observe asynchronous send success or failure."),
            Map.of("id", 18, "category", "Kafka", "title", "Pause and resume", "description", "Pause consumption during backpressure."),
            Map.of("id", 19, "category", "Kafka", "title", "Consumer lag", "description", "Monitor consumer lag through broker metrics."),
            Map.of("id", 20, "category", "Kafka", "title", "Schema evolution", "description", "Version event payloads with compatibility rules."),
            Map.of("id", 21, "category", "Redis", "title", "String set/get", "description", "Store and retrieve a simple string value."),
            Map.of("id", 22, "category", "Redis", "title", "TTL key", "description", "Set an expiry when writing a value."),
            Map.of("id", 23, "category", "Redis", "title", "Increment counter", "description", "Atomically increment a numeric key."),
            Map.of("id", 24, "category", "Redis", "title", "Delete key", "description", "Delete a cached value."),
            Map.of("id", 25, "category", "Redis", "title", "Key existence", "description", "Check whether a key exists."),
            Map.of("id", 26, "category", "Redis", "title", "Refresh expiry", "description", "Change the expiry of an existing key."),
            Map.of("id", 27, "category", "Redis", "title", "Read TTL", "description", "Inspect remaining key lifetime."),
            Map.of("id", 28, "category", "Redis", "title", "List push", "description", "Append an item to a Redis list."),
            Map.of("id", 29, "category", "Redis", "title", "Hash fields", "description", "Store structured fields in a Redis hash."),
            Map.of("id", 30, "category", "Redis", "title", "Set membership", "description", "Use a Redis set for unique membership."),
            Map.of("id", 31, "category", "Redis", "title", "Sorted set ranking", "description", "Use scores for leaderboard ranking."),
            Map.of("id", 32, "category", "Redis", "title", "Cache-aside", "description", "Read cache first and fall back to a source."),
            Map.of("id", 33, "category", "Redis", "title", "Cache invalidation", "description", "Delete a cached value after a write."),
            Map.of("id", 34, "category", "Redis", "title", "Distributed lock", "description", "Acquire a lock with SET NX and an expiry."),
            Map.of("id", 35, "category", "Redis", "title", "Rate limiting", "description", "Count requests in a time window."),
            Map.of("id", 36, "category", "Redis", "title", "Session storage", "description", "Store a session token with a TTL."),
            Map.of("id", 37, "category", "Redis", "title", "Idempotency key", "description", "Remember processed request IDs."),
            Map.of("id", 38, "category", "Redis", "title", "Pub/Sub", "description", "Publish and subscribe to lightweight notifications."),
            Map.of("id", 39, "category", "Redis", "title", "Stream append", "description", "Append events to a Redis Stream."),
            Map.of("id", 40, "category", "Redis", "title", "Atomic operation", "description", "Use Lua for a compound atomic action."),
            Map.of("id", 41, "category", "Integration", "title", "Kafka to Redis", "description", "Consume an event and update a Redis view."),
            Map.of("id", 42, "category", "Integration", "title", "Redis then Kafka", "description", "Persist a short-lived dedupe key before publish."),
            Map.of("id", 43, "category", "Integration", "title", "Outbox pattern", "description", "Publish database outbox records reliably."),
            Map.of("id", 44, "category", "Integration", "title", "Event replay", "description", "Replay retained events into a read model."),
            Map.of("id", 45, "category", "Integration", "title", "Cache warming", "description", "Preload frequently requested values."),
            Map.of("id", 46, "category", "Integration", "title", "Fallback behavior", "description", "Return a clear error when a dependency is unavailable."),
            Map.of("id", 47, "category", "API / Swagger", "title", "OpenAPI metadata", "description", "Set API title, description and version."),
            Map.of("id", 48, "category", "API / Swagger", "title", "Request validation", "description", "Validate input with Jakarta Bean Validation."),
            Map.of("id", 49, "category", "API / Swagger", "title", "Standard error response", "description", "Return a consistent error body."),
            Map.of("id", 50, "category", "API / Swagger", "title", "Pagination contract", "description", "Document page and size parameters."),
            Map.of("id", 51, "category", "API / Swagger", "title", "Health endpoint", "description", "Expose readiness and liveness information.")
        );
    }
}
