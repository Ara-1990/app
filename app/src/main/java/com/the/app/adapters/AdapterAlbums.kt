package com.the.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.the.app.R
import com.the.app.databinding.AlbumHolderBinding
import com.the.app.models.Album

sealed class AdapterAlbums (
    private var albums: List<Album>?,
    private val savedOnly: Boolean,
    private var open: (Album?) -> Unit,
    private val remove: ((Int, Int) -> Unit)? = null
):    RecyclerView.Adapter<AdapterAlbums.Holder>() {

    private var iconEnabled: Boolean = true
    fun enableIconRemove() {
        iconEnabled = true
    }

    class AlbumsLoadingAdapter(
        albums: List<Album>?,
        open: (Album?) -> Unit
    ) : AdapterAlbums(albums, false, open)

    class AlbumsSavedAdapter(
        albums: List<Album>?,
        open: (Album?) -> Unit,
        remove: ((Int, Int) -> Unit)
    ) : AdapterAlbums(albums, true, open, remove)

    fun setAlbums(albums: List<Album>?) {
        this.albums = albums
        iconEnabled = true
        notifyDataSetChanged()
    }

    fun removeAlbum(pos: Int): Boolean? {
        return albums?.run {
            (this as MutableList).removeAt(pos)
            notifyItemRemoved(pos)
            isEmpty()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = AlbumHolderBinding.bind(itemView)
        fun bind(album: Album) {
            binding.albumId.text =
                if (savedOnly) (layoutPosition + 1).toString() else album.id.toString()
            binding.albumTitle.text = album.title
            binding.manipulate.visibility = if (savedOnly) View.VISIBLE else View.GONE
            binding.albumLayout.setOnClickListener { open(albums?.get(layoutPosition)) }
            binding.manipulate.setOnClickListener {
                remove?.run {
                    val albumId = albums?.get(layoutPosition)?.id
                    albumId?.let {
                        invoke(layoutPosition, it)
                        iconEnabled = false
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_holder, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = albums?.size ?: 0

    override fun onBindViewHolder(holder: Holder, position: Int) {
        albums?.let { holder.bind(it[position]) }
    }

}
