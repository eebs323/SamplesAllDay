package com.appballstudio.samplesallday.domain.foody.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodyOrderDto(
    val id: String,
    val state: String,
    val price: Int,
    val item: String,
    val shelf: String,
    val timestamp: Long,
    val destination: String,
    val changelog: List<ChangelogEntry> = emptyList()
) : Parcelable

@Parcelize
data class ChangelogEntry(
    val timestamp: Long,
    val changeType: String,
    val oldValue: String,
    val newValue: String
) : Parcelable

enum class Shelf {
    HOT, COLD, FROZEN, OVERFLOW, NONE
}