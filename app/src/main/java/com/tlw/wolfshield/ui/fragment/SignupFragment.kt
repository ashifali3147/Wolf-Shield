package com.tlw.wolfshield.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tlw.wolfshield.databinding.FragmentSignupBinding
import com.tlw.wolfshield.utils.Constant

class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
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
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val name = edtName.text.toString()
                val role = if (rdParent.isChecked) Constant.PARENT else Constant.CHILD

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userID = auth.currentUser?.uid ?: return@addOnCompleteListener

                        //Store in DB
                        val user =
                            hashMapOf("name" to name, "role" to role, "approved" to (role == Constant.PARENT))

                        db.collection("users").document(userID).set(user).addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Signup successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Signup failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        return binding.root
    }
}