package com.theant.repository.video

import com.theant.model.Video

interface VideoRepository {

    suspend fun getVideos(): List<Video>

}
