package com.the.app.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.the.app.*
import com.the.app.models.Information
import com.the.app.viewmodels.ViewModelPermittance
import com.the.app.viewmodels.ViewModelPhoto
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class FragmentPhoto : Fragment(), CoroutineScope {
    private val job by lazy { Job() }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var photoViewModel: ViewModelPhoto
    private lateinit var activityViewModel: ViewModelPermittance
    private val info: Information? by lazy { arguments?.getParcelable(BUNDLE_KEY_INFO) }
    private lateinit var activity: MainActivity
    private var height: Int = 0
    private var width: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        photoViewModel = ViewModelProvider(this).get(ViewModelPhoto::class.java)
        activityViewModel = ViewModelProvider(activity).get(ViewModelPermittance::class.java)
        return inflater.inflate(R.layout.fragment_photo, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        activityViewModel.permissionGrantedStorage()
            .observe(viewLifecycleOwner, { info?.run { downloadImage(url) } })
        photoViewModel.status.observe(viewLifecycleOwner, { showRequestState(it) })
    }

    override fun onResume() {
        super.onResume()

        photoLarge.onInitialized {
            launch {
                delay(1500)
                height = photoLarge.height
                width = photoLarge.width
                fabZoomOut.show()
                fabZoomIn.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun initViews() {
        info?.run {
            photoTitle.text = title
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.placeholder_image)
                .into(photoLarge)
            btnDownload.setOnClickListener { downloadImage(url) }
            fabZoomIn.setOnClickListener { zoomIn(url) }
            fabZoomOut.setOnClickListener { zoomOut(url) }
        }
    }

    private fun downloadImage(url: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION_WRITE_STORAGE
            )
            return
        }
        photoProgress.show()
        tvStatusPhoto.showStatus(R.string.downloading)
        btnDownload.isEnabled = false
        photoViewModel.imageDownload(url)
    }

    private fun showRequestState(status: TransactionsState) {
        when (status) {
            TransactionsState.NO_NETWORK -> {
                photoProgress.hide()
                tvStatusPhoto.showStatus(R.string.noNetwork)
            }
            TransactionsState.DOWNLOADING_SUCCEED -> {
                launch {
                    photoProgress.hide()
                    tvStatusPhoto.hide()
                    btnDownload.animate().alpha(0f).setDuration(1000).start()
                    lottie_download.setAnimation(R.raw.download_load)
                    lottie_download.visibility = View.VISIBLE
                    delay(3500)
                    lottie_download.visibility = View.GONE
                    delay(300)
                }
            }
            TransactionsState.DOWNLOADING_FAILED -> {
                Toast.makeText(requireContext(), R.string.downloadingFailed, Toast.LENGTH_LONG)
                    .show()
                photoProgress.hide()
                tvStatusPhoto.hide()
            }
            else -> return
        }
    }


    private fun zoomIn(url: String) {
        if (height > 0 && width > 0) {
            height += ZOOM_CHANGE_SIZE
            width += ZOOM_CHANGE_SIZE
            Picasso.get()
                .load(url)
                .resize(height, width)
                .into(photoLarge)
        }
    }

    private fun zoomOut(url: String) {
        if (height > ZOOM_CHANGE_SIZE && width > ZOOM_CHANGE_SIZE) {
            height -= ZOOM_CHANGE_SIZE
            width -= ZOOM_CHANGE_SIZE
            Picasso.get()
                .load(url)
                .resize(height, width)
                .into(photoLarge)
        }
    }


}