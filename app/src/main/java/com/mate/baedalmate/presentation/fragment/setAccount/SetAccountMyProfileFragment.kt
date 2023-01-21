package com.mate.baedalmate.presentation.fragment.setAccount

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.HideKeyBoardUtil
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.LoadingAlertDialog
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentMyProfileChangeBinding
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

@AndroidEntryPoint
class SetAccountMyProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentMyProfileChangeBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var loadingAlertDialog: AlertDialog

    private var userProfileImageString: String? = null
    private var userProfileImageExtension: String? = null
    private val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileChangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), view)
        initAlertDialog()
        setActionbarInfo()
        setImageChangeClickListener()
        setInitSubmitClick()
        setLimitEditTextInputType()
        observeMyProfileChange()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun initAlertDialog() {
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        loadingAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setActionbarInfo() {
        with(binding) {
            btnMyProfileChangeActionbarBack.visibility = View.INVISIBLE
            btnMyProfileChangeActionbarBack.isEnabled = false
            tvMyProfileChangeActionbarTitle.text =
                getString(R.string.set_account_my_profile_actionbar_title)
        }
    }

    private fun setImageChangeClickListener() {
        binding.layoutMyProfileChangePhoto.setOnDebounceClickListener {
            requestOpenGallery.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun setInitSubmitClick() {
        with(binding.btnMyProfileChangeSubmit) {
            text = getString(R.string.next_to)
            setOnDebounceClickListener { setMyProfileSubmit() }
        }

        binding.etMyProfileChangeNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.btnMyProfileChangeSubmit.isEnabled = !s.isNullOrEmpty()
            }
        })
    }

    private fun setLimitEditTextInputType() {
        val filterInputCheck =
            InputFilter { source, start, end, dest, dstart, dend ->
                val ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-흐]+$")
                if (!ps.matcher(source).matches()) {
                    return@InputFilter ""
                }
                null
            }
        val lengthFilter: InputFilter = InputFilter.LengthFilter(5)
        binding.etMyProfileChangeNickname.filters =
            arrayOf(filterInputCheck, lengthFilter)
    }

    private fun setMyProfileSubmit() {
        showLoadingDialog()
        memberViewModel.requestPutChangeMyProfile(
            isChangingDefaultImage = getMyProfileImageFile() == null,
            newNickname = binding.etMyProfileChangeNickname.text.trim().toString(),
            newImageFile = getMyProfileImageFile()
        )
    }

    private fun observeMyProfileChange() {
        memberViewModel.isMyProfileChangeSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                with(findNavController()) {
                    if (currentDestination?.id == R.id.SetAccountMyProfileFragment) {
                        currentDestination?.getAction(R.id.action_setAccountMyProfileFragment_to_locationCertificationFragment)
                            ?.let {
                                navigate(
                                    SetAccountMyProfileFragmentDirections.actionSetAccountMyProfileFragmentToLocationCertificationFragment(
                                        isInitialCertificate = true
                                    )
                                )
                            }
                    }
                }
            }
        }
    }

    private val requestOpenGallery =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value == false) {
                    return@registerForActivityResult
                }
            }
            openGallery()
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
                setMyProfileImageView(uri)
            }
        }

    private fun setMyProfileImage(ImageString: String, fileExtension: String) {
        userProfileImageString = ImageString
        userProfileImageExtension = fileExtension
    }

    private fun setMyProfileImageView(uri: Uri) {
        glideRequestManager.load(uri)
            .override(GetDeviceSize.getDeviceWidthSize(requireContext()))
            .priority(Priority.HIGH)
            .centerCrop()
            .into(binding.imgMyProfileChangePhotoThumbnail)
    }

    private fun getMyProfileImageFile(): File? {
        return if (userProfileImageString.isNullOrEmpty() || userProfileImageExtension.isNullOrEmpty()) {
            null
        } else {
            setUploadImagePath(userProfileImageExtension!!)
            val originalBitmap = userProfileImageString!!.toUri().toBitmap()
            bitmapToFile(originalBitmap, imagePath)
        }
    }

    private fun setUploadImagePath(fileExtension: String) {
        // uri를 통하여 불러온 이미지를 임시로 파일로 저장할 경로로 앱 내부 캐시 디렉토리로 설정,
        // 파일 이름은 불러온 시간 사용
        val fileName = imageFileTimeFormat.format(Date(System.currentTimeMillis()))
            .toString() + "." + fileExtension
        val cacheDir = requireContext().cacheDir.toString()
        imagePath = "$cacheDir/$fileName"
    }

    private fun bitmapToFile(bitmap: Bitmap?, path: String?): File? {
        if (bitmap == null || path == null) {
            return null
        }
        var file = File(path)
        var out: OutputStream? = null
        try {
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } finally {
            out?.close()
        }
        return file
    }

    private fun Uri.toBitmap(): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    requireContext().contentResolver,
                    this
                )
            )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, this)
        }
    }

    private fun showLoadingDialog() {
        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
        LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog)
    }
}