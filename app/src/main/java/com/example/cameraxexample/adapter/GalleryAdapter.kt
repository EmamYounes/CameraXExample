package com.example.cameraxexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraxexample.R
import com.example.cameraxexample.callbacks.ClickImageCallback
import com.example.cameraxexample.databinding.GalleryItemBinding
import com.example.cameraxexample.model.GalleryModel

class GalleryAdapter(var list: MutableList<GalleryModel>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryHolder>() {

    lateinit var binding: GalleryItemBinding

    lateinit var clickImageCallback: ClickImageCallback

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryHolder {

        binding = GalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class GalleryHolder(binding: GalleryItemBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {


        fun bind(model: GalleryModel) {
            binding.imageLayout.tag = adapterPosition
            binding.deleteImage.tag = adapterPosition
            binding.imageId.setImageURI(model.uri)
            if (model.isChecked) {
                binding.deleteImage.visibility = View.VISIBLE
                binding.imageLayout.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.border_button_white_background
                )
            } else {
                binding.imageLayout.background = null
            }

            binding.imageLayout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = v?.tag as Int
            if (binding.imageLayout.id == v.id) {
                if (!list[position].isChecked) {
                    list[position].isChecked = true
                    updateList(position, list[position])
                }

            } else if (binding.deleteImage.id == v.id) {
                removeItem(position)
                clickImageCallback.onDeleteImageClick(list[position])
            }
        }


    }

    private fun removeItem(position: Int) {
        list.removeAt(position)
        list[position].isChecked = true
        notifyItemChanged(position)
    }

    private fun updateList(position: Int, model: GalleryModel) {
        list[position] = model
        notifyItemChanged(position)
    }

    fun updateList(list: MutableList<GalleryModel>) {
        this.list = list
        notifyDataSetChanged()
    }
}