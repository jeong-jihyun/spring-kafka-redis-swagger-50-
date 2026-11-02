package com.example.demo.controller;

import com.example.demo.service.ExampleCatalog;
import com.example.demo.service.KafkaDemoService;
import com.example.demo.service.RedisDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@Tag(name = "Examples", description = "Kafka, Redis and API examples")
public class ExampleController {
    private final ExampleCatalog catalog;
    private final KafkaDemoService kafka;
    private final RedisDemoService redis;

    public ExampleController(ExampleCatalog catalog, KafkaDemoService kafka, RedisDemoService redis) {
        this.catalog = catalog;
        this.kafka = kafka;
        this.redis = redis;
    }

    @GetMapping("/examples")
    @Operation(summary = "List the 50 example scenarios")
    public Object examples() { return catalog.all(); }

    @GetMapping("/examples/{id}")
    @Operation(summary = "Get an example scenario by number")
    public Object example(@PathVariable int id) {
        return catalog.all().stream().filter(x -> x.get("id").equals(id)).findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Example not found"));
    }

    @PostMapping("/kafka/messages")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Publish a Kafka message to demo.events")
    public Map<String, Object> publish(@Valid @RequestBody PublishRequest request) {
        kafka.publish(request.key(), request.message());
        return Map.of("status", "accepted", "topic", "demo.events", "key", request.key());
    }

    @PutMapping("/redis/{key}")
    @Operation(summary = "Write a Redis string value; ttlSeconds=0 means no expiry")
    public Map<String, Object> set(@PathVariable @Size(max = 200) String key, @Valid @RequestBody RedisWrite request) {
        String value = redis.set(key, request.value(), request.ttlSeconds());
        return Map.of("key", key, "value", value == null ? "" : value, "ttlSeconds", request.ttlSeconds());
    }

    @GetMapping("/redis/{key}")
    @Operation(summary = "Read a Redis string value")
    public Map<String, Object> get(@PathVariable String key) {
        String value = redis.get(key);
        if (value == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Redis key not found");
        return Map.of("key", key, "value", value, "ttlSeconds", redis.ttl(key));
    }

    @DeleteMapping("/redis/{key}")
    @Operation(summary = "Delete a Redis key")
    public Map<String, Object> delete(@PathVariable String key) {
        return Map.of("key", key, "deleted", Boolean.TRUE.equals(redis.delete(key)));
    }

    @PostMapping("/redis/{key}/increment")
    @Operation(summary = "Atomically increment a Redis counter")
    public Map<String, Object> increment(@PathVariable String key) {
        return Map.of("key", key, "value", redis.increment(key));
    }

    public record PublishRequest(@NotBlank @Size(max = 200) String key,
                                 @NotBlank @Size(max = 5000) String message) {}
    public record RedisWrite(@NotBlank @Size(max = 5000) String value,
                             @Min(0) long ttlSeconds) {}
}
