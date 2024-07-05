package com.the.app

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.the.app.databinding.ActivityMainBinding
import com.the.app.viewmodels.ViewModelPermittance

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by lazy {
        ViewModelProvider(this).get(ViewModelPermittance::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                viewModel.permissionStorageGrant()
        }

        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            if (grantResults.isNotEmpty())
                run breaker@{
                    grantResults.forEach {
                        if (it == PackageManager.PERMISSION_GRANTED) {
                            viewModel.permissionLocation()
                            return@breaker
                        }
                    }
                }
        }
    }

}