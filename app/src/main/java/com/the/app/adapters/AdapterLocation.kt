package com.the.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.the.app.R
import com.the.app.databinding.LocationHolderBinding

class AdapterLocation : RecyclerView.Adapter<AdapterLocation.Holder>() {

    private var locationInfo: List<Pair<String, String>>? = null

    fun updateLocation(infoList: List<Pair<String, String>>?) {
        locationInfo = infoList
        notifyDataSetChanged()
    }


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = LocationHolderBinding.bind(itemView)

        fun bind(info: Pair<String, String>) {

            binding.locSubInfo.text = info.first
            binding.locInfo.text = info.second
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.location_holder, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = locationInfo?.size ?: 0

    override fun onBindViewHolder(holder: Holder, position: Int) {
        locationInfo?.let { holder.bind(it[position]) }
    }

}