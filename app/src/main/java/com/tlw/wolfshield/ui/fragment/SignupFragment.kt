package com.tlw.wolfshield.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tlw.wolfshield.databinding.FragmentSignupBinding
import com.tlw.wolfshield.event.LoginEvent
import com.tlw.wolfshield.utils.Constant
import com.tlw.wolfshield.viewmodel.LoginViewModel

class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private val viewModel: LoginViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            btnLogin.setOnClickListener { findNavController().popBackStack() }
            btnSignUp.setOnClickListener {
                viewModel.sendUIEvent(LoginEvent.ShowLoader)
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val name = edtName.text.toString()
                val role = if (rdParent.isChecked) Constant.PARENT else Constant.CHILD

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    viewModel.sendUIEvent(LoginEvent.HideLoader)
                    if (task.isSuccessful) {
                        val userID = auth.currentUser?.uid ?: return@addOnCompleteListener

                        //Store in DB
                        val user =
                            hashMapOf("name" to name, "role" to role, "approved" to (role == Constant.PARENT))

                        db.collection("users").document(userID).set(user).addOnSuccessListener {
                            viewModel.sendUIEvent(LoginEvent.ShowSnackBar("Signup successful!"))
                            findNavController().popBackStack()
                        } .addOnFailureListener { error->
                            viewModel.sendUIEvent(LoginEvent.ShowSnackBar(error.message.toString()))
                        }
                    } else {
                        viewModel.sendUIEvent(LoginEvent.ShowSnackBar(task.exception?.message.toString()))
                    }
                }
            }
        }
        return binding.root
    }
}