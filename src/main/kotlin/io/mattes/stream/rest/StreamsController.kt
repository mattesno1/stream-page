package io.mattes.stream.rest

import io.mattes.stream.model.Video
import io.mattes.stream.model.VideoPage
import io.mattes.stream.repository.StreamRepository
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@Validated
class StreamsController(
        private val streamRepository: StreamRepository
) {

    @GetMapping("/api/streams/live", produces = ["application/json"])
    fun getLiveStream(): Video? {
        return streamRepository.getActiveStream()
    }

    @GetMapping("/api/streams/completed", produces = ["application/json"])
    fun getCompletedStreams(
            @RequestParam(defaultValue = "10") @Max(50) @Min(1) pageSize: Int,
            @RequestParam(required = false) page: String?
    ): VideoPage {
        return streamRepository.getPageOfCompletedStreams(pageSize, page)
    }
}