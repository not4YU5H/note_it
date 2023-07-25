package com.noteit.data.model

data class NotesModel (
    val title:String,
    val description:String,
)

data class NotesResponse(
    val Id:Int,
    val title:String,
    val description:String,
    val createdAt:String,
    val updatedAt:String
)