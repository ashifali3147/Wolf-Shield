package com.tlw.wolfshield.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tlw.wolfshield.databinding.ActivityParentDashboardBinding
import com.tlw.wolfshield.ui.adapter.ChildrenRequestAdapter
import com.tlw.wolfshield.viewmodel.ParentDashboardViewModel

class ParentDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentDashboardBinding
    private val vieModel: ParentDashboardViewModel by viewModels()
    private val adapter by lazy { ChildrenRequestAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityParentDashboardBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            vieModel.apply {
                rvRequest.layoutManager = LinearLayoutManager(this@ParentDashboardActivity)
                rvRequest.adapter = adapter
                children.observe(this@ParentDashboardActivity) { children ->
                    adapter.submitList(children)
                }
            }
        }
    }

//    private fun collectUIMessage(){
//        binding.apply {
//            lifecycleScope.launch(Dispatchers.Main) {
//                vieModel.uiEvents.collect { event ->
//                    when(event) {
//                        LoginEvent.HideLoader -> loaderLayout.visibility = View.GONE
//                        LoginEvent.ShowLoader -> loaderLayout.visibility = View.VISIBLE
//                        is LoginEvent.ShowSnackBar -> showSnackBar(event.message)
//                    }
//                }
//            }
//        }
//    }

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