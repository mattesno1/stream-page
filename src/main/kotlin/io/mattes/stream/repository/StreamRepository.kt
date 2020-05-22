package io.mattes.stream.repository

import io.mattes.stream.model.Video
import io.mattes.stream.model.VideoPage

interface StreamRepository {

    fun getActiveStream(): Video?

    fun getPageOfCompletedStreams(pageSize: Int, page: String? = null): VideoPage
}