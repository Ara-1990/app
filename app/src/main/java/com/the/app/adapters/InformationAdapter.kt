package com.the.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.the.app.R
import com.the.app.databinding.InformationHolderBinding
import com.the.app.models.Information

class InformationAdapter(
private var infoList: List<Information>?,
private val openPhoto: (Information?) -> Unit
) : RecyclerView.Adapter<InformationAdapter.Holder>() {

    fun setAlbums(info: List<Information>?) {
        this.infoList = info
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = InformationHolderBinding.bind(itemView)
        fun bind(info: Information) {
            binding.infoId.text = (layoutPosition + 1).toString()
            binding.infoTitle.text = info.title
            Picasso.get()
                .load(info.thumbnailUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.photoInto)

            binding.photoInto.setOnClickListener { openPhoto(infoList?.get(layoutPosition)) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.information_holder, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = infoList?.size ?: 0

    override fun onBindViewHolder(holder: Holder, position: Int) {
        infoList?.let { holder.bind(it[position]) }
    }

}