package io.mattes.stream.repository

import com.google.api.services.youtube.YouTube
import io.mattes.stream.model.Video
import io.mattes.stream.model.VideoPage
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.ZoneId
import kotlin.random.Random

@Repository
@ConditionalOnMissingBean(YouTube::class)
class DummyStreamRepository : StreamRepository {

    companion object {
        private val RANDOM = Random(0)
        private val ALL_STREAMS = listOf(
                "ErmiCTUQZ_Y",
                "Cies6QsC1kk",
                "6CKKONGGAqE",
                "tdc2G34wNgY",
                "7rcShn47Ppw",
                "Ry7-8FqsloQ",
                "27cFg0V8Ivo",
                "Mg4_SLl-jtw",
                "EBdTMLxT1Bg",
                "G2Cocrx06zE"
        ).map { Video(
                title = it,
                date = Instant.now().atZone(ZoneId.of("UTC")).minusDays(RANDOM.nextLong(365)),
                url = "https://www.youtube.com/watch?v=$it",
                embedUrl = "https://www.youtube.com/embed/$it")
        }
    }

    override fun getActiveStream(): Video? {
        return ALL_STREAMS[0]
    }

    override fun getPageOfCompletedStreams(pageSize: Int, page: String?): VideoPage {
        val videos = generateSequence(0, { it + 1 })
                .map { ALL_STREAMS[it % ALL_STREAMS.size] }
                .take(pageSize)
                .toList()

        return VideoPage(videos, "dummy")
    }
}