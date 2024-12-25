package com.example.gallery

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
data class Pixabay(
    val hits: List<PhotoItem>,
    val total: Int,
    val totalHits: Int
)

@Parcelize
data class PhotoItem(
    @SerializedName("collections")val collections: Int,
    val comments: Int,
    val downloads: Int,
    @SerializedName("id") val photoId: Int,
    val imageHeight: Int,
    val imageSize: Int,
    val imageWidth: Int,
    @SerializedName("largeImageURL") val fullViewUrl: String,
    @SerializedName("likes")val likes: Int,
    val pageURL: String,
    val previewHeight: Int,
    @SerializedName("previewURL") val previewUrl: String,
    val previewWidth: Int,
    val tags: String,
    val type: String,
    val user: String,
    val userImageURL: String,
    @SerializedName("user_id")val userId: Int,
    val views: Int,
    @SerializedName("webformatHeight")val photoHeight: Int,
    @SerializedName("webformatURL") val photoUrl: String,
    @SerializedName("webformatWidth")val photoWidth: Int
) : Parcelable