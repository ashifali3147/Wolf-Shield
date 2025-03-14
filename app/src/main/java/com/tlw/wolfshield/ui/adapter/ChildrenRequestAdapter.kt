package com.tlw.wolfshield.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tlw.wolfshield.data.model.ChildModel
import com.tlw.wolfshield.databinding.CustomChildrenRequestLayBinding

class ChildrenRequestAdapter :
    RecyclerView.Adapter<ChildrenRequestAdapter.ChildrenRequestViewHolder>() {
    private var list = listOf<ChildModel>()

    class ChildrenRequestViewHolder(val binding: CustomChildrenRequestLayBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenRequestViewHolder {
        return ChildrenRequestViewHolder(
            CustomChildrenRequestLayBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ChildrenRequestViewHolder, position: Int) {
        val child = list[position]
        holder.binding.apply {
            tvName.text = child.name
            tvEmail.text = child.email
            btnApprove.isVisible = !child.approved
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(items: List<ChildModel>) {
        list = items
        notifyDataSetChanged()
    }
}