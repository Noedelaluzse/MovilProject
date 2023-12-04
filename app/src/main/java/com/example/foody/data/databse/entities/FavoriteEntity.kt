package com.example.foody.data.databse.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.FAVORITE_RECIPE_TABLE

@Entity(tableName = FAVORITE_RECIPE_TABLE)
class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val result: Result
)