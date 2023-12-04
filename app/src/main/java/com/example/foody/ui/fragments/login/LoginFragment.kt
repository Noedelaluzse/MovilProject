package com.example.foody.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.foody.databinding.FragmentLoginBinding
import com.example.foody.viewmodels.MainViewModel
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment() {

    private companion object {
        const val RC_SIGN_IN = 9001 // Puedes elegir cualquier número entero aquí
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var  mainViewModel: MainViewModel

    private val authResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            Toast.makeText(context, "Bienvenido...", Toast.LENGTH_SHORT).show()
        } else {
            if (IdpResponse.fromResultIntent(it.data) == null) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mainViewModel.initLogin(requireActivity())

        binding.btnLogin.setOnClickListener {
            signInWithGoogle()
        }
        return binding.root
    }

    private fun signInWithGoogle() {
        val signInIntent: Intent = mainViewModel.mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Manejo del resultado de la actividad de inicio de sesión con Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Maneja errores aquí
            }
        }
    }

    // Método para autenticar en Firebase con la cuenta de Google
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        mainViewModel.mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Autenticación exitosa
                    val user = mainViewModel.mAuth.currentUser
                    val name = user?.displayName
                    // Actualiza la interfaz de usuario según sea necesario
                    Toast.makeText(context, "Welcome $name", Toast.LENGTH_SHORT).show()
                    mainViewModel.setSession(true)
                    val action = LoginFragmentDirections.actionLoginFragmentToRecipesFragment()
                    findNavController().navigate(action)
                } else {
                    // Si la autenticación falla, muestra un mensaje al usuario
                    Toast.makeText(context, "Something went wrong. Try later", Toast.LENGTH_SHORT).show()
                    mainViewModel.setSession(false)
                }
            }
    }

}