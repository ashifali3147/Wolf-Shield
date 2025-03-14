package com.tlw.wolfshield.ui.fragment

import android.content.Intent
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
import com.tlw.wolfshield.R
import com.tlw.wolfshield.databinding.FragmentLoginBinding
import com.tlw.wolfshield.event.LoginEvent
import com.tlw.wolfshield.ui.activity.ChildDashboardActivity
import com.tlw.wolfshield.ui.activity.ParentDashboardActivity
import com.tlw.wolfshield.utils.Constant
import com.tlw.wolfshield.utils.LocalData
import com.tlw.wolfshield.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by activityViewModels()

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
                viewModel.sendUIEvent(LoginEvent.ShowLoader)
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    viewModel.sendUIEvent(LoginEvent.HideLoader)
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if(document.exists()) {
                                    val role = document.getString("role") ?: ""
                                    val approved = document.getBoolean("approved") ?: false
                                    if (role == Constant.CHILD && !approved) {
                                        viewModel.sendUIEvent(LoginEvent.ShowSnackBar("Waiting for parent approval"))
                                    } else {
                                        val intent = if (role == Constant.PARENT) {
                                            Intent(requireContext(), ParentDashboardActivity::class.java)
                                        } else {
                                            Intent(requireContext(), ChildDashboardActivity::class.java)
                                        }
                                        LocalData.saveUserID(userId)
                                        LocalData.saveParentRole(role == Constant.PARENT)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                }
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