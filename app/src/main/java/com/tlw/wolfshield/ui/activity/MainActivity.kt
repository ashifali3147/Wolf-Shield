package com.tlw.wolfshield.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.tlw.wolfshield.databinding.ActivityMainBinding
import com.tlw.wolfshield.event.LoginEvent
import com.tlw.wolfshield.utils.Constant
import com.tlw.wolfshield.utils.LocalData
import com.tlw.wolfshield.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vieModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        if (LocalData.getUserID().isNotEmpty()){
            val intent = if (LocalData.getParentRole()) {
                Intent(this, ParentDashboardActivity::class.java)
            } else {
                Intent(this, ChildDashboardActivity::class.java)
            }
            startActivity(intent)
            finishAffinity()
        }
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        collectUIMessage()
        binding.apply {
            vieModel.apply {

            }
        }
    }

    private fun collectUIMessage(){
        binding.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                vieModel.uiEvents.collect { event ->
                    when(event) {
                        LoginEvent.HideLoader -> loaderLayout.visibility = View.GONE
                        LoginEvent.ShowLoader -> loaderLayout.visibility = View.VISIBLE
                        is LoginEvent.ShowSnackBar -> showSnackBar(event.message)
                    }
                }
            }
        }
    }

    private fun showSnackBar(message: String, action: String = "", callback: () -> Unit = {}) {
        val snackBar = Snackbar.make(binding.main, message, Snackbar.LENGTH_LONG)
        if (action.isNotEmpty()) {
            snackBar.setAction(action) {
                callback()
            }
        }
        snackBar.show()
    }
}