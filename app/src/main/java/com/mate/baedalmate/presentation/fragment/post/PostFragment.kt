package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitDetail
import com.mate.baedalmate.databinding.FragmentPostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var binding by autoCleared<FragmentPostBinding>()
    private lateinit var starIndicator: Array<ImageView?>

    private var tmp = RecruitDetail(
        "", "", 0, "", "", 0, 0, 0, false,
        "", "", "", 0f, ""
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        initContents()
    }

    private fun setBackClickListener() {
        binding.btnPostActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initContents() {
        with(binding) {
//            imgPostBack = tmp.thumbnailImage
            tvPostFrontContentsDeadlineTime.text = tmp.deadlineDate
            tvPostFrontContentsDeadlineDeliveryFee.text = tmp.deliveryFee.toString()
            tvPostFrontContentsDeadlinePeople.text = tmp.minPeople.toString()
            // TODO: 유저 Dormitory와 배달 Dormitory가 다를 수 있으므로, 어떻게 구분할건지 설정 필요
            tvPostFrontContentsDormitory.text = tmp.dormitory
            tvPostFrontUserDormitory.text = tmp.dormitory
            tvPostFrontUserName.text = tmp.username
            // TODO: 유저 이미지 썸네일 필요

            starIndicator = arrayOfNulls<ImageView>(5)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(3, 0, 0, 0)
            for (i in starIndicator.indices) {
                starIndicator[i] = ImageView(requireContext())
                if (i <= tmp.userScore) {
                    starIndicator[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_star_full
                        )
                    )
                } else if (i > tmp.userScore && (i - tmp.userScore) != 0f && (i - tmp.userScore) < 1) {
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
                binding.layoutPostFrontUserScore.addView(starIndicator[i])
            }

            tvPostFrontContentsTitle.text = tmp.title
            tvPostFrontContentsDetail.text = tmp.description

            // TODO: 방장인지 체크하는 조건 추가 필요
            if(tmp.participate) {
                btnPostFrontContentsParticipate.text = getString(R.string.post_participate_out)
            } else {
                btnPostFrontContentsParticipate.text = getString(R.string.post_participate_in)
            }
        }
    }
}