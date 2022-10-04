package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.databinding.FragmentPostCategoryAllBinding
import com.mate.baedalmate.presentation.fragment.post.adapter.PostCategoryListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCategoryAllFragment : Fragment() {
    private var binding by autoCleared<FragmentPostCategoryAllBinding>()
    private lateinit var postCategoryListAdapter: PostCategoryListAdapter

    private var tmpList = listOf<RecruitDto>(
        RecruitDto(
            "", 0, "", 0, "누리학사", 0, 0, 0, "피자헛 월계 2호점", "", "피자헛 같이 부술 사람~",
            0f, ""
        ),
        RecruitDto(
            "", 0, "", 0, "누리학사", 0, 0, 0, "피자헛 월계 2호점", "", "피자헛 같이 부술 사람~",
            0f, ""
        ),
        RecruitDto(
            "", 0, "", 0, "누리학사", 0, 0, 0, "피자헛 월계 2호점", "", "피자헛 같이 부술 사람~",
            0f, ""
        ),
        RecruitDto(
            "", 0, "", 0, "누리학사", 0, 0, 0, "피자헛 월계 2호점", "", "피자헛 같이 부술 사람~",
            0f, ""
        ),
        RecruitDto(
            "", 0, "", 0, "누리학사", 0, 0, 0, "피자헛 월계 2호점", "", "피자헛 같이 부술 사람~",
            0f, ""
        ),
        RecruitDto(
            "", 0, "", 0, "누리학사", 0, 0, 0, "피자헛 월계 2호점", "", "피자헛 같이 부술 사람~",
            0f, ""
        ),
        RecruitDto(
            "", 0, "", 0, "누리학사", 0, 0, 0, "피자헛 월계 2호점", "", "피자헛 같이 부술 사람~",
            0f, ""
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostCategoryAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListAdapter()
    }

    private fun initListAdapter() {
        postCategoryListAdapter = PostCategoryListAdapter()
        binding.rvPostCategoryAllList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvPostCategoryAllList.adapter = postCategoryListAdapter
        }

        // TODO: 서버 연결
        postCategoryListAdapter.submitList(tmpList.toMutableList())
    }
}