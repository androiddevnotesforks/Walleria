package com.andrii_a.walleria.domain.models.topic

import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User

data class Topic(
    val id: String,
    val slug: String?,
    val title: String?,
    val description: String?,
    val featured: Boolean?,
    val startsAt: String?,
    val endsAt: String?,
    val totalPhotos: Int?,
    val links: TopicLinks?,
    val status: String?,
    val owners: List<User>?,
    val coverPhoto: Photo?,
    val previewPhotos: List<Photo>?
)