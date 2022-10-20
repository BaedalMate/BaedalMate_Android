package com.mate.baedalmate.presentation.fragment.mypage

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
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
    }

    private fun getUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                memberViewModel.requestUserInfo()
            }
        }
    }

    private fun initUserData() {
        starIndicator = arrayOfNulls<ImageView>(5)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(3, 0, 0, 0)

        memberViewModel.userInfo.observe(viewLifecycleOwner) { info ->
            glideRequestManager.load(info.profileImage)
                .override(GetDeviceSize.getDeviceWidthSize(requireContext()))
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgMyPageUserInfo)

            binding.tvMyPageUserInfoNickname.text = info.nickname
            binding.tvMyPageUserInfoDormitory.text = "현재 거점: ${info.dormitory}"

            binding.layoutMyPageUserInfoScore.removeAllViews()
            for (i in starIndicator.indices) {
                starIndicator[i] = ImageView(requireContext())
                if (i <= info.score) {
                    starIndicator[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_star_full
                        )
                    )
                } else if (i > info.score && (i - info.score) != 0f && (i - info.score) < 1) {
                    starIndicator[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_star_half
                        )
                    )
                } else {
                    starIndicator[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_star_empty
                        )
                    )
                }
                starIndicator[i]!!.layoutParams = params
                binding.layoutMyPageUserInfoScore.addView(starIndicator[i])
            }

            val paramsTextView = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            paramsTextView.setMargins(5.dp, 0, 0, 0)
            val scoreTextView = TextView(requireContext())
            scoreTextView.setTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.applesdgothic_neo_bold
                ), Typeface.NORMAL
            )
            scoreTextView.text = "${info.score}"
            scoreTextView.layoutParams = paramsTextView
            scoreTextView.gravity = Gravity.CENTER
            scoreTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.main_FB5F1C
                )
            )
            binding.layoutMyPageUserInfoScore.addView(scoreTextView)
        }
    }
}