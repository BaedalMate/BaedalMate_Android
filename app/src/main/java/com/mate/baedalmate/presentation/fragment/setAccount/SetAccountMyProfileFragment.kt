package com.mate.baedalmate.presentation.fragment.setAccount

import android.app.AlertDialog
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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
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

    private lateinit var userProfileImageString: String
    private lateinit var userProfileImageExtension: String
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
        observeNavigationMyProfileImageCallBack()
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
            findNavController().navigate(R.id.action_setAccountMyProfileFragment_to_setAccountMyProfileImageOptionFragment)
        }
    }

    private fun observeNavigationMyProfileImageCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("userProfileImageString")
            ?.observe(viewLifecycleOwner) {
                userProfileImageString = it
                if (this::userProfileImageString.isInitialized) {
                    if (userProfileImageString == "resetMyProfileImage") {
                        glideRequestManager.load(R.drawable.ic_person)
                            .centerCrop().into(binding.imgMyProfileChangePhotoThumbnail)
                    } else {
                        glideRequestManager.load(userProfileImageString.toUri()).centerCrop()
                            .into(binding.imgMyProfileChangePhotoThumbnail)
                    }
                }
            }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("fileExtension")
            ?.observe(viewLifecycleOwner) {
                userProfileImageExtension = it
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

    private fun getMyProfileImageFile(): File? {
        return if (!::userProfileImageString.isInitialized) {
            null
        } else {
            setUploadImagePath(userProfileImageExtension)
            val originalBitmap = userProfileImageString.toUri().toBitmap()
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