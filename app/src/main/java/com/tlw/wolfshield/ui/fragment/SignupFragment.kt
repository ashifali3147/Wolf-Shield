package com.tlw.wolfshield.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
            rdChild.setOnCheckedChangeListener { _, isChecked ->
                edtParentEmail.isVisible = isChecked
            }
            btnSignUp.setOnClickListener {
                viewModel.sendUIEvent(LoginEvent.ShowLoader)

                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val name = edtName.text.toString()
                val role = if (rdParent.isChecked) Constant.PARENT else Constant.CHILD
                val parentEmail = if (role == Constant.CHILD) edtParentEmail.text.toString() else ""

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    viewModel.sendUIEvent(LoginEvent.HideLoader)

                    if (task.isSuccessful) {
                        val userID = auth.currentUser?.uid ?: return@addOnCompleteListener
                        viewModel.sendUIEvent(LoginEvent.ShowLoader)
                        if (role == Constant.PARENT) {
                            // Store Parent Data
                            val user = hashMapOf(
                                "name" to name,
                                "role" to role,
                                "approved" to true,
                                "email" to email.toLowerCase()  // Ensure email is lowercase
                            )
                            db.collection("users").document(userID).set(user)
                                .addOnSuccessListener {
                                    viewModel.sendUIEvent(LoginEvent.ShowSnackBar("Signup successful!"))
                                    findNavController().popBackStack()
                                }
                                .addOnFailureListener { error ->
                                    viewModel.sendUIEvent(LoginEvent.ShowSnackBar(error.message.toString()))
                                }
                        }
                        else {
                            // Store Child Data (Only if Parent Exists)
                            db.collection("users")
                                .whereEqualTo("email", parentEmail.toLowerCase())
                                .get()
                                .addOnSuccessListener { parentDocs ->
                                    if (!parentDocs.isEmpty) {
                                        val parentID = parentDocs.documents[0].id
                                        val childData = hashMapOf(
                                            "name" to name,
                                            "email" to email.toLowerCase(),
                                            "parentId" to parentID,
                                            "role" to role,
                                            "approved" to false
                                        )

                                        // Store child in users collection
                                        db.collection("users").document(userID).set(childData)

                                        // Add child under parent's document
                                        db.collection("users").document(parentID)
                                            .collection("children").document(userID).set(childData)
                                            .addOnSuccessListener {
                                                viewModel.sendUIEvent(LoginEvent.ShowSnackBar("Signup successful! Waiting for parent approval."))
                                                findNavController().popBackStack()
                                            }
                                            .addOnFailureListener { error ->
                                                viewModel.sendUIEvent(LoginEvent.ShowSnackBar(error.message.toString()))
                                            }
                                    } else {
                                        viewModel.sendUIEvent(LoginEvent.ShowSnackBar("Parent not found. Please enter a valid parent email."))
                                    }
                                }
                                .addOnFailureListener { error ->
                                    viewModel.sendUIEvent(LoginEvent.ShowSnackBar(error.message.toString()))
                                }
                        }
                        viewModel.sendUIEvent(LoginEvent.HideLoader)
                    } else {
                        viewModel.sendUIEvent(LoginEvent.ShowSnackBar(task.exception?.message.toString()))
                    }
                }
            }

        }
        return binding.root
    }
}