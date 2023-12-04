package com.example.foody.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foody.R
import com.example.foody.models.ExtendedIngredient
import com.example.foody.util.Constants.Companion.BASE_IMAGE_URL
import com.example.foody.util.RecipesDiffUtil

class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>(){

    private var ingredientsList = emptyList<ExtendedIngredient>()
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ingredients_row_layout, parent,false ))
    }

    override fun onBindViewHolder(holder: IngredientsAdapter.MyViewHolder, position: Int) {
        holder.itemView.findViewById<ImageView>(R.id.ingredient_imageView).load(BASE_IMAGE_URL + ingredientsList[position].image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        holder.itemView.findViewById<TextView>(R.id.ingredient_name).text = ingredientsList[position].name.capitalize()
        holder.itemView.findViewById<TextView>(R.id.ingredient_amount).text = ingredientsList[position].amount.toString()
        holder.itemView.findViewById<TextView>(R.id.ingredient_unit).text = ingredientsList[position].unit.toString()
        holder.itemView.findViewById<TextView>(R.id.ingredient_consistency).text = ingredientsList[position].consistency.toString()
        holder.itemView.findViewById<TextView>(R.id.ingrediente_original).text = ingredientsList[position].original.toString()
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun setData(newIngredients: List<ExtendedIngredient>) {
        val ingredientesDiffUtil = RecipesDiffUtil(ingredientsList, newIngredients )
        val diffUtilResult = DiffUtil.calculateDiff(ingredientesDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }
}