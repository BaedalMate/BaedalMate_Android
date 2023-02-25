package com.mate.baedalmate.presentation.fragment.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mate.baedalmate.common.HideKeyBoardUtil
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.RoundDialogFragment
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.domain.model.PlaceDto
import com.mate.baedalmate.data.datasource.remote.write.Place
import com.mate.baedalmate.databinding.FragmentWriteSecondPlaceDialogBinding
import com.mate.baedalmate.domain.model.FoodCategory
import com.mate.baedalmate.presentation.adapter.write.WriteSecondPlaceDialogListAdapter
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WriteSecondPlaceDialogFragment : RoundDialogFragment() {
    private var binding by autoCleared<FragmentWriteSecondPlaceDialogBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var writeSecondPlaceDialogListAdapter: WriteSecondPlaceDialogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogHeightSizeRatio = 0.6f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteSecondPlaceDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialGetSearchResult()
        setBackClickListener()
        setSearchResultList()
        editTextSearchClickListener()
        setSelectStoreClickListener()
    }

    private fun initialGetSearchResult() {
        val categoryIds = FoodCategory.values().associateBy { it.categoryId }
        showLoadingView()
        writeViewModel.searchPlaceKeyword(
            keyword = categoryIds[writeViewModel.categoryId]?.categoryName ?: "음식점"
        )
        // 기본적으로 Dialog가 띄워지면 주변 음식점을 검색하도록 설정
    }

    private fun setBackClickListener() {
        binding.btnWriteSecondPlaceClose.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun editTextSearchClickListener() {
        binding.etWriteSecondPlaceUserInput.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    if (binding.etWriteSecondPlaceUserInput.text.toString().trim().isNotEmpty()) {
                        showLoadingView()
                        writeViewModel.searchPlaceKeyword(
                            keyword = binding.etWriteSecondPlaceUserInput.text.toString().trim()
                        )
                    }
                    binding.etWriteSecondPlaceUserInput.clearFocus()
                    HideKeyBoardUtil.hideEditText(
                        requireContext(),
                        binding.etWriteSecondPlaceUserInput
                    )
                    false
                }
                else -> false
            }
        }
    }

    private fun setSearchResultList() {
        writeSecondPlaceDialogListAdapter = WriteSecondPlaceDialogListAdapter()
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        with(binding.rvWriteSecondPlaceResultList) {
            this.layoutManager = layoutManager
            this.adapter = writeSecondPlaceDialogListAdapter
        }
        observePlaceListResult()
    }

    private fun observePlaceListResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                writeViewModel.searchResultList.observe(viewLifecycleOwner) { resultList ->
                    resultList.documents?.let { documentList ->
                        hideLoadingView()
                        displayEmptyView(documentList.isEmpty())
                        binding.tvWriteSecondPlaceResultEmpty.isVisible = documentList.isEmpty()
                        writeSecondPlaceDialogListAdapter.submitList(documentList.toMutableList())
                    }
                }
            }
        }
    }

    private fun setSelectStoreClickListener() {
        writeSecondPlaceDialogListAdapter.setOnItemClickListener(object :
            WriteSecondPlaceDialogListAdapter.OnItemClickListener {
            override fun selectStore(contents: Place, pos: Int) {
                writeViewModel.deliveryStore.postValue(
                    PlaceDto(
                        addressName = contents.address_name,
                        name = contents.place_name,
                        roadAddressName = contents.road_address_name,
                        x = contents.x.toFloat(),
                        y = contents.y.toFloat()
                    )
                )
                findNavController().navigateUp()
            }
        })
    }


    private fun showLoadingView() {
        with(binding) {
            rvWriteSecondPlaceResultList.visibility = View.GONE
            tvWriteSecondPlaceResultEmpty.visibility = View.GONE
            lottieWriteSecondPlaceResultLoading.visibility = View.VISIBLE
        }
    }

    private fun hideLoadingView() {
        with(binding) {
            rvWriteSecondPlaceResultList.visibility = View.VISIBLE
            tvWriteSecondPlaceResultEmpty.visibility = View.GONE
            lottieWriteSecondPlaceResultLoading.visibility = View.GONE
        }
    }

    private fun displayEmptyView(isResultEmpty: Boolean) {
        with(binding) {
            rvWriteSecondPlaceResultList.isVisible = !isResultEmpty
            tvWriteSecondPlaceResultEmpty.isVisible = isResultEmpty
        }
    }
}