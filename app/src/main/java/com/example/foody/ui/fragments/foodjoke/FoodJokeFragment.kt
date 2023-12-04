package com.example.foody.ui.fragments.foodjoke

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.foody.R
import com.example.foody.databinding.FragmentFoodJokeBinding
import com.example.foody.viewmodels.MainViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.foody.bindingadapters.RecipesRowBinding.Companion.applyVeganColor
import com.example.foody.models.Result
import com.example.foody.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    private var _binding: FragmentFoodJokeBinding? = null
    private val binding get() = _binding!!
    //private  var randomRecipe: Result? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFoodJokeBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.food_joke_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.log_out_menu) {
                    mainViewModel.logOut(false)
                    mainViewModel.initLogin(requireActivity())
                    mainViewModel.mGoogleSignInClient.signOut()
                        .addOnCompleteListener(requireActivity()) {
                            val action = FoodJokeFragmentDirections.actionFoodJokeFragmentToLoginFragment()
                            findNavController().navigate(action)
                            Toast.makeText(context, "Goodbye", Toast.LENGTH_SHORT).show()
                        }
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)
        setRandomRecipe()
        return binding.root
    }

    private fun setRandomRecipe() {
        mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
            val random = java.util.Random()
            if (database.isNotEmpty()) {
                val data = database[0].foodRecipe.results
                val randomIndex = random.nextInt(data.size)
                val randomRecipe = data[randomIndex]
                setData(randomRecipe)

            }
        }
    }

    private fun setData(randomRecipe: Result) {
        binding.titleTextViewRandom.text = randomRecipe.title
        binding.descriptionTextViewRandom.text = randomRecipe.summary
        binding.clockTextViewRandom.text = randomRecipe.readyInMinutes.toString()
        binding.heartTextViewRandom.text = randomRecipe.aggregateLikes.toString()
        loadImage(randomRecipe.image)
        applyVeganColor(binding.leafImageViewRandom, randomRecipe.vegan)
        applyVeganColor(binding.leaftTextViewRandom, randomRecipe.vegan)
        goToDetails(randomRecipe)
    }

    private fun goToDetails(recipe: Result) {
        val wrapper = binding.wrapperRandomRecipe
        wrapper.setOnClickListener {
            try {
                val action = FoodJokeFragmentDirections.actionFoodJokeFragmentToDetailsActivity(recipe)
                wrapper.findNavController().navigate(action)
            } catch (e: Exception) {
                Log.d("onGoToDetails", e.toString())
            }
        }
    }

    private fun loadImage(imageUrl: String) {
        binding.recipeImageViewRandom
            .load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
    }

}