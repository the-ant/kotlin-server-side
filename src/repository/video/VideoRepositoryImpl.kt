package com.theant.repository.video

import com.theant.factory.DataFactory.dbQuery
import com.theant.model.Video
import com.theant.table.Videos
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class VideoRepositoryImpl : VideoRepository {

    override suspend fun getVideos(): List<Video> = dbQuery {
        Videos.selectAll().mapNotNull { it.toVideo() }
    }

    private fun ResultRow?.toVideo(): Video? {
        val row = this ?: return null
        return Video(
                id = row[Videos.id],
                title = row[Videos.title],
                speaker = row[Videos.speaker],
                url = row[Videos.url]
        )
    }

}
