package com.the.app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.the.app.*
import com.the.app.adapters.InformationAdapter
import com.the.app.databinding.FragmentInformationBinding
import com.the.app.models.Album
import com.the.app.models.Information
import com.the.app.viewmodels.ViewModelInformation
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class InformationFragment : Fragment(), CoroutineScope {

    private val job: Job by lazy { Job() }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var viewModel: ViewModelInformation
    private lateinit var binding:FragmentInformationBinding
    private val album: Album? by lazy{arguments?.getParcelable(BUNDLE_KEY_ALBUM)}
    private val fromSavedFolder: Boolean? by lazy {
        arguments?.getBoolean(BUNDLE_KEY_FROM_SAVED_FOLDER)
    }
    private lateinit var adapter: InformationAdapter
    private var infoList: List<Information>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_information, container, false)
        binding = FragmentInformationBinding.bind(v)
        viewModel = ViewModelProvider(this).get(ViewModelInformation::class.java)
        return v

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        load()

        viewModel.infoAlbum.observe(viewLifecycleOwner, {
            if (it.first) showSavedInfo(it.second)
            else showLoadedInfo(it.second)
        })
        viewModel.status.observe(viewLifecycleOwner, { showRequestState(it) })
    }

    private fun initViews() {

        binding.swipeRefreshInfo.setOnRefreshListener { refresh() }
        binding.recyclerInfo.setHasFixedSize(true)
        album?.run {
            val albTitle = getString(R.string.album) + ": " + title
            binding.albumSelected.text = albTitle
        }
        adapter = InformationAdapter(null) { openPhoto(it) }
        binding.recyclerInfo.adapter = adapter
        fromSavedFolder?.let { removable ->
            if (removable) binding.btnSaveRemove.customize(R.string.remove,R.drawable.mini_trash)
            else binding.btnSaveRemove.customize(R.string.save,R.drawable.ic_star)
            binding.btnSaveRemove.setOnClickListener { if (removable) remove() else save() }
        }
    }

    private fun openPhoto(info: Information?) {
        info?.let {
            findNavController().navigate(
                R.id.nav_photo,
                Bundle().apply {
                    putParcelable(BUNDLE_KEY_INFO, info)
                }
            )
        }
    }


    private fun load() {
        binding.progress.show()
        binding.status.showStatus(R.string.loading)
        album?.run {
            viewModel.loading(id)
        }
    }

    private fun showSavedInfo(info: List<Information>?) {
        info?.let {
            infoList = info
            adapter.setAlbums(it)
            binding.statusSaved.visibility = View.VISIBLE
            fromSavedFolder?.let { f ->
                binding.btnSaveRemove.visibility = if (f) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showLoadedInfo(info: List<Information>?) {
        info?.let {
            infoList = info
            adapter.setAlbums(it)
            binding.statusSaved.visibility = View.GONE
            binding.btnSaveRemove.visibility = View.VISIBLE
        }
    }

    private fun showRequestState(status: TransactionsState) {
        when (status) {
            TransactionsState.LOADING_ERROR -> {
                binding.progress.hide()
                binding.swipeRefreshInfo.isRefreshing = false
                binding.status.showStatus(R.string.error_loading)
                binding.btnSaveRemove.visibility = View.GONE
            }
            TransactionsState.LOADING_SUCCEED -> {
                binding.progress.hide()
                binding.status.hide()
                binding.swipeRefreshInfo.isRefreshing = false
            }
            TransactionsState.NO_NETWORK -> {
                binding.progress.visibility = View.GONE
                binding.swipeRefreshInfo.isRefreshing = false
                binding.status.showStatus(R.string.noNetwork)
            }
            TransactionsState.REMOVING_SUCCEED -> {
                binding.progress.hide()
                binding.status.hide()
                launch {
                    binding.animSaveRemove.setAnimation(R.raw.remove)
                    binding.animSaveRemove.visibility = View.VISIBLE
                    binding.btnSaveRemove.animate().alpha(0f).setDuration(1500).start()
                    delay(3000)
                    binding.statusSaved.visibility = View.GONE
                    delay(500)
                    binding.animSaveRemove.visibility = View.GONE
                    delay(300)
                    findNavController().popBackStack()
                }
            }
            TransactionsState.REMOVING_FAILED -> {
                binding.progress.hide()
                binding.status.hide()
                binding.btnSaveRemove.isEnabled = true
                Toast.makeText(requireContext(), R.string.error_removing, Toast.LENGTH_LONG).show()
            }
            else -> return
        }

    }

    private fun refresh() {
        binding.status.showStatus(R.string.refreshing)
        album?.run {
            viewModel.refreshing(id)
        }
    }

    private fun save() {
        infoList?.let {
            if (album != null) {
                viewModel.save(album!!, it)
                launch {
                    binding.btnSaveRemove.isEnabled = false
                    binding.animSaveRemove.setAnimation(R.raw.saved)
                    binding.animSaveRemove.visibility = View.VISIBLE
                    binding.btnSaveRemove.animate().alpha(0f).setDuration(1500).start()
                    delay(3500)
                    binding.animSaveRemove.visibility = View.GONE
                    delay(300)
                    binding.statusSaved.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun remove() {
        album?.run {
            binding.progress.show()
            binding.status.showStatus(R.string.removing)
            binding.btnSaveRemove.isEnabled = false
            viewModel.remove(id)
        }
    }




}