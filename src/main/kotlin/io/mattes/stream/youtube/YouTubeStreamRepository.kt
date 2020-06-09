package io.mattes.stream.youtube

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.LiveBroadcast
import com.google.api.services.youtube.model.PlaylistItem
import io.mattes.stream.model.Video
import io.mattes.stream.model.VideoPage
import io.mattes.stream.repository.StreamRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.ZoneId


@Primary
@Repository
@ConditionalOnBean(YouTube::class)
class YouTubeStreamRepository(
        private val youtube: YouTube
) : StreamRepository {

    companion object {
        private val LOG = LoggerFactory.getLogger(YouTubeStreamRepository::class.java)
    }

    @Cacheable(cacheNames = ["youtube-live"])
    override fun getActiveStream(): Video? {
        LOG.info("getting active stream from youtube.")
        return youtube.liveBroadcasts().list("id,snippet,contentDetails")
                .setBroadcastStatus("active")
                .setMaxResults(1)
                .execute()
                .items
                .map { toModelObject(it) }
                .firstOrNull()
    }

    @Cacheable(cacheNames = ["youtube-video"])
    override fun getPageOfCompletedStreams(pageSize: Int, page: String?): VideoPage {
        LOG.info("getting page from youtube. pageSize=$pageSize page=$page")
        val response = youtube.liveBroadcasts().list("id,snippet")
                .setBroadcastStatus("completed")
                .setMaxResults(pageSize.toLong())
                .setPageToken(page)
                .execute()

        val videos = response
                .items
                .map { toModelObject(it) }

        return VideoPage(videos, response.nextPageToken)
    }

    private fun toModelObject(it: LiveBroadcast): Video {
        return Video(
                title = it.snippet.title,
                date = Instant.ofEpochMilli(it.snippet.actualStartTime.value).atZone(ZoneId.of("UTC")),
                url = "https://www.youtube.com/watch?v=${it.id}",
                embedUrl = "https://www.youtube.com/embed/${it.id}")
    }
}