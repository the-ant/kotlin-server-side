package com.theant.route.video

import com.theant.API_VERSION
import com.theant.model.Video
import com.theant.repository.video.VideoRepository
import com.theant.response.BaseDataResponse
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*

const val VIDEOS = "$API_VERSION/videos"

@KtorExperimentalLocationsAPI
@Location(VIDEOS)
class VideosRoute

@KtorExperimentalLocationsAPI
fun Route.videos(
        videoRepository: VideoRepository
) {
    get<VideosRoute> {
        try {
            val videos = videoRepository.getVideos()
            call.respond(HttpStatusCode.OK, BaseDataResponse(HttpStatusCode.OK, videos))
        } catch (e: Exception) {
            application.log.error("Failed to get Todos", e)
            call.respond(HttpStatusCode.BadRequest, "Problems getting Videos")
        }
    }
}
