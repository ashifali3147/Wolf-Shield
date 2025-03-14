package com.tlw.wolfshield.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tlw.wolfshield.data.model.ChildModel
import com.tlw.wolfshield.utils.LocalData

class ParentDashboardViewModel : ViewModel() {

    private val _children = MutableLiveData<List<ChildModel>>()
    val children: LiveData<List<ChildModel>> get() = _children

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchChildren()
    }

    private fun fetchChildren() {
        val parentID = LocalData.getUserID()

        db.collection("users").document(parentID)
            .collection("children").get()
            .addOnSuccessListener { snapshot ->
                val childrenList = snapshot.documents.map { doc ->
                    ChildModel(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        approved = doc.getBoolean("approved") ?: false
                    )
                }
                _children.value = childrenList
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching children: ", exception)
            }
    }
}

