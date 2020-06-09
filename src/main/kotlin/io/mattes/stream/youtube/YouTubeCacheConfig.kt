package io.mattes.stream.youtube

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


@Configuration
class YouTubeCacheConfig {

    @Bean
    fun youtubeLiveCache(): CaffeineCache {
        return CaffeineCache("youtube-live",
                Caffeine.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(1, TimeUnit.MINUTES)
                        .build())
    }

    @Bean
    fun youtubeVideoCache(): CaffeineCache {
        return CaffeineCache("youtube-video",
                Caffeine.newBuilder()
                        .maximumSize(1_000)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .build())
    }
}