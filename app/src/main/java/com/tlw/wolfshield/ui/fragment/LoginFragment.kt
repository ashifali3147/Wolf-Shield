package com.tlw.wolfshield.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tlw.wolfshield.R
import com.tlw.wolfshield.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }

            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if(document.exists()) {
                                    val role = document.getString("role") ?: ""
                                    val approved = document.getBoolean("approved") ?: false
                                    if (role == "child" && !approved) {
                                        Toast.makeText(requireContext(), "Waiting for parent approval", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val intent = if (role == "parent") {
                                            Toast.makeText(requireContext(), "Parent", Toast.LENGTH_SHORT).show()
//                                            Intent(this, ParentDashboardActivity::class.java)
                                        } else {
                                            Toast.makeText(requireContext(), "Child", Toast.LENGTH_SHORT).show()
//                                            Intent(this, ChildDashboardActivity::class.java)
                                        }
//                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                }
                            }
                    } else {
                        Toast.makeText(requireContext(), "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }
}