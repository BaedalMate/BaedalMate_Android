package com.mate.baedalmate.presentation.fragment.setAccount

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.BuildConfig
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentSetAccountMyProfileImageOptionBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@AndroidEntryPoint
class SetAccountMyProfileImageOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentSetAccountMyProfileImageOptionBinding>()
    private lateinit var currentTakenPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetAccountMyProfileImageOptionBinding.inflate(inflater, container, false)
        initDialogFragmentLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCameraClickListener()
        setGalleryClickListener()
    }

    override fun onResume() {
        super.onResume()
        resizeOptionDialogFragment()
    }

    private fun initDialogFragmentLayout() {
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requireDialog().window?.requestFeature(Window.FEATURE_NO_TITLE)
        requireDialog().window?.setGravity(Gravity.BOTTOM)
    }

    private fun resizeOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = GetDeviceSize.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setCameraClickListener() {
        binding.layoutSetAccountMyProfileChangeOptionSelectCamera.setOnDebounceClickListener {
            requestOpenCamera.launch(PERMISSIONS_CAMERA)
        }
    }

    private fun setGalleryClickListener() {
        binding.layoutSetAccountMyProfileChangeOptionSelectGallery.setOnDebounceClickListener {
            requestOpenGallery.launch(PERMISSIONS_GALLERY)
        }
    }

    private val requestOpenCamera =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedList: List<String> = permissions.filter { !it.value }.map { it.key }

            when {
                deniedList.isNotEmpty() -> {
                    val map = deniedList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) getString(R.string.permission_fail_second)
                        else getString(R.string.permission_fail_final)
                    }
                    map[getString(R.string.permission_fail_second)]?.let {
                        // request denied , request again
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_fail_message_camera),
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            PERMISSIONS_CAMERA,
                            1000
                        )
                    }
                    map[getString(R.string.permission_fail_final)]?.let {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                            val uri = Uri.parse("package:${requireContext().packageName}")
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            it.data = uri
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_fail_final_message_camera),
                            Toast.LENGTH_SHORT
                        ).show()
                        //request denied ,send to settings
                    }
                }
                else -> {
                    //All request are permitted
                    openCamera()
                }
            }
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            var photoFile: File? = null
            val tmpDir: File? = requireContext().cacheDir
            val timeStamp: String =
                SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA).format(Date())
            val photoFileName = "Capture_${timeStamp}_"
            try {
                val tmpPhoto = File.createTempFile(photoFileName, ".jpg", tmpDir)
                currentTakenPhotoPath = tmpPhoto.absolutePath
                photoFile = tmpPhoto
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    Objects.requireNonNull(requireContext().applicationContext),
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                requestTakePhotoActivity.launch(intent)
            }
        }
    }

    private val requestOpenGallery =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedList: List<String> = permissions.filter {
                !it.value
            }.map {
                it.key
            }

            when {
                deniedList.isNotEmpty() -> {
                    val map = deniedList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) getString(R.string.permission_fail_second)
                        else getString(R.string.permission_fail_final)
                    }
                    map[getString(R.string.permission_fail_second)]?.let {
                        // request denied , request again
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_fail_message_gallery),
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            PERMISSIONS_GALLERY,
                            1000
                        )
                    }
                    map[getString(R.string.permission_fail_final)]?.let {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                            val uri = Uri.parse("package:${requireContext().packageName}")
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            it.data = uri
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_fail_final_message_gallery),
                            Toast.LENGTH_SHORT
                        ).show()
                        //request denied ,send to settings
                    }
                }
                else -> {
                    //All request are permitted
                    openGallery()
                }
            }
        }

    private fun openGallery() {
        // ACTION PICK 사용시, intent type에서 설정한 종류의 데이터를 MediaStore에서 불러와서 목록으로 나열 후 선택할 수 있는 앱 실행
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        requestActivity.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val requestActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val data: Intent? = activityResult.data
                // 호출된 갤러리에서 이미지 선택시, data의 data속성으로 해당 이미지의 Uri 전달
                val uri = data?.data!!
                // 이미지 파일과 함께, 파일 확장자도 같이 저장
                val fileExtension =
                    requireContext().contentResolver.getType(uri).toString().split("/")[1]
                setMyProfileImage(uri.toString(), fileExtension)
            } else {
                findNavController().popBackStack()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val requestTakePhotoActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val photoFile = File(currentTakenPhotoPath)
                // 이미지 파일과 함께, 파일 확장자도 같이 저장
                val fileExtension = Uri.fromFile(photoFile).toString().split("/")[1]
                setMyProfileImage(Uri.fromFile(photoFile).toString(), fileExtension)
            } else {
                findNavController().popBackStack()
            }
        }

    private fun setMyProfileImage(ImageString: String, fileExtension: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "userProfileImageString",
            ImageString
        )
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "fileExtension",
            fileExtension
        )
        findNavController().popBackStack()
    }

    companion object {
        val PERMISSIONS_CAMERA = if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        val PERMISSIONS_GALLERY = if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}