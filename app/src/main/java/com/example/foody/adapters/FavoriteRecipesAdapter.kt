package com.example.foody.adapters

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.data.databse.entities.FavoriteEntity
import com.example.foody.databinding.FavoriteRecipesRowLayooutBinding
import com.example.foody.ui.fragments.favorites.FavoriteFragmentDirections
import com.example.foody.util.RecipesDiffUtil
import com.example.foody.viewmodels.MainViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
): RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(), ActionMode.Callback{

    private var multiselection = false

    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    private var favoriteRecipes = emptyList<FavoriteEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()
    private var selectedRecipes = arrayListOf<FavoriteEntity>()


    class MyViewHolder(private val binding: FavoriteRecipesRowLayooutBinding ): RecyclerView.ViewHolder(binding.root){

        fun bind(favoritesEntity: FavoriteEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayooutBinding.inflate(layoutInflater, parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int = favoriteRecipes.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView

        val currenRecipe = favoriteRecipes[position]
        holder.bind(currenRecipe)

        saveItemStateOnScroll(currenRecipe, holder)

        // Single Click listener
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout).setOnClickListener {
            if (multiselection) {
                applySelection(holder, currenRecipe)
            } else {
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToDetailsActivity(currenRecipe.result)
                holder.itemView.findNavController().navigate(action)
            }
        }

        // Long Click listener
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout).setOnLongClickListener {
            if (!multiselection) {
                multiselection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currenRecipe)
                true
            } else {
                applySelection(holder, currenRecipe)
                true
            }
        }
    }
    
    private fun saveItemStateOnScroll(currentRecipe: FavoriteEntity, holder: MyViewHolder) {
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder, R.color.cardBackgroundLigthColor, R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: FavoriteEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModetitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLigthColor, R.color.colorPrimary)
            applyActionModetitle()
        }
    }

    private fun changeRecipeStyle(holder: MyViewHolder, background: Int, strokeColor: Int) {
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout).setBackgroundColor(
            ContextCompat.getColor(requireActivity, background))

        holder.itemView.findViewById<MaterialCardView>(R.id.favorite_row_cardView).strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    private fun applyActionModetitle() {
        when(selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
                multiselection = false
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} item selected"
            }
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorite_contextual_menu, menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.delete_favorite_menu) {
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipe(it)
            }
            showSnackBar("${selectedRecipes.size} Recipes/s removed")
            multiselection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiselection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)

    }

    fun setData(newFavoriteRecipes: List<FavoriteEntity>) {

        val favoriteRecipesDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction(requireActivity.getString(R.string.okay)) {}
            .show()
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }
}