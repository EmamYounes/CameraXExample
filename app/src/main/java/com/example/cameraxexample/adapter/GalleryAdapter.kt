package com.example.cameraxexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                    R.drawable.white_border_for_image
                )
            } else {
                binding.deleteImage.visibility = View.GONE
                binding.imageLayout.background = null
            }

            binding.imageLayout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            var position = v?.tag as Int
            if (list[position].isChecked) {
                removeItem(position)

                if (position != 0)
                    position -= 1

            } else {
                list[position].isChecked = true
                updateList(position, list[position])
            }
            clickImageCallback.onSelectedImage(list[position])
        }


    }

    private fun removeItem(position: Int) {
        list.removeAt(position)
        val itemPosition = if (position != 0)
            position - 1
        else
            0
        list[itemPosition].isChecked = true
        notifyDataSetChanged()
    }

    private fun updateList(position: Int, model: GalleryModel) {
        list[position] = model
        notifyDataSetChanged()
    }

    fun updateList(list: MutableList<GalleryModel>) {
        this.list = list
        notifyDataSetChanged()
    }
}