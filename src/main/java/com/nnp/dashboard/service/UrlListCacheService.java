package com.nnp.dashboard.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Redis-backed caching service for authorized user URLs.
 * Stores permitted URLs in Redis lists key-patterned as "urls:{userId}:{envId}" with a default TTL of 2.5 hours.
 */
@Service
@RequiredArgsConstructor
public class UrlListCacheService {

	private static final Duration TTL = Duration.ofHours(2).plusMinutes(30); // 2h 30m

    private final StringRedisTemplate redis;

    /**
     * Builds the Redis cache key for a given user ID and environment ID.
     *
     * @param userId user identifier
     * @param envId environment identifier
     * @return Redis key string
     */
    private String buildKey(String userId, String envId) {
        return "urls:" + userId + ":" + envId;
    }

    /**
     * Adds an authorized URL to the Redis cache list for a user/environment pair and refreshes key TTL.
     *
     * @param userId user identifier
     * @param envId environment identifier
     * @param url authorized URL host string
     */
    public void addUrl(String userId, String envId, String url) {
        String key = buildKey(userId, envId);
        redis.opsForList().rightPush(key, url);
        redis.expire(key, TTL);
    }

    /**
     * Retrieves the list of cached authorized URLs for a user/environment pair.
     *
     * @param userId user identifier
     * @param envId environment identifier
     * @return list of URL strings cached in Redis
     */
    public List<String> getUrls(String userId, String envId) {
        String key = buildKey(userId, envId);
        return redis.opsForList().range(key, 0, -1);
    }

    /**
     * Evicts the cached URL list from Redis for a user/environment pair.
     *
     * @param userId user identifier
     * @param envId environment identifier
     */
    public void clearUrls(String userId, String envId) {
        redis.delete(buildKey(userId, envId));
    }
}
