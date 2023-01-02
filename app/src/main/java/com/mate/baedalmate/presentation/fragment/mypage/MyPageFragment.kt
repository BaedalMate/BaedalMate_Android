package com.mate.baedalmate.presentation.fragment.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentMyPageBinding
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var binding by autoCleared<FragmentMyPageBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var starIndicator: Array<ImageView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        getUserData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserData()
        setMenusSettingClickListener()
        setMenusInfoClickListener()
        setAccountActionClickListener()
    }

    private fun getUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                memberViewModel.requestUserInfo()
            }
        }
    }

    private fun initUserData() {
        memberViewModel.userInfo.observe(viewLifecycleOwner) { info ->
            glideRequestManager.load("http://3.35.27.107:8080/images/${info.profileImage}")
                .override(GetDeviceSize.getDeviceWidthSize(requireContext()))
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgMyPageUserInfo)

            binding.tvMyPageUserInfoNickname.text = info.nickname
            binding.tvMyPageUserInfoDormitory.text = "${info.dormitory}"
            binding.tvMyPageUserInfoScore.text = info.score.toString()
        }
    }

    private fun setMenusSettingClickListener() {
        // TODO 알림 설정
        binding.layoutMyPageMenusSettingLocationChange.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_locationCertificationFragment)
        }

        binding.layoutMyPageMenusSettingBlock.setOnDebounceClickListener {
            // TODO 차단관리 navigation
        }
    }

    private fun setMenusInfoClickListener() {
        binding.layoutMyPageMenusInfoNotice.setOnDebounceClickListener {
            // TODO 공지사항 navigation
        }
        binding.layoutMyPageMenusInfoInquiry.setOnDebounceClickListener {
            // TODO 문의하기 클릭
        }
    }

    private fun setAccountActionClickListener() {
        binding.tvMyPageAccountActionLogout.setOnDebounceClickListener {
            // TODO 로그아웃 동작
        }
        binding.tvMyPageAccountActionResign.setOnDebounceClickListener {
            // TODO 회원탈퇴 동작
        }
    }
}