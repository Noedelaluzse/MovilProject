package com.example.foody.data.databse

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foody.data.databse.entities.FavoriteEntity
import com.example.foody.data.databse.entities.RecipesEntity


@Database(
    entities = [RecipesEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipeDao
}