package com.example.foody.data

import com.example.foody.data.databse.RecipeDao
import com.example.foody.data.databse.entities.FavoriteEntity
import com.example.foody.data.databse.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipeDao: RecipeDao
) {

    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipeDao.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoriteEntity>> {
        return recipeDao.readFavoriteRecipes()
    }
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipeDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavoriteRefcipes(favoriteEntity: FavoriteEntity) {
        recipeDao.insertFavoriteRecipe(favoriteEntity)
    }

    suspend fun deleteFavoriteRecipe(favoriteEntity: FavoriteEntity) {
        recipeDao.deleteFavoriteRecipe(favoriteEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipeDao.deleteAllFavoriteRecipes()
    }
}