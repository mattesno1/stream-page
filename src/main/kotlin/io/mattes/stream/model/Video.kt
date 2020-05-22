package io.mattes.stream.model

import java.time.ZonedDateTime

data class Video(
        val title: String,
        val date: ZonedDateTime,
        val url: String,
        val embedUrl: String
)

data class VideoPage(
        val videos: List<Video>,
        val nextPage: String?
)