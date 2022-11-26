package com.mate.baedalmate.presentation.fragment.write

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.domain.model.TagDto
import com.mate.baedalmate.databinding.FragmentWriteThirdBinding
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteThirdFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteThirdBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setNextClickListener()
        initSubjectInput()
        initDescriptionInput()
        initTagList()
    }

    private fun setBackClickListener() {
        binding.btnWriteThirdActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnWriteThirdNext.setOnClickListener {
            val titleText =
                if(binding.etWriteThirdSubjectInput.text.toString().trim().length > 20) binding.etWriteThirdSubjectInput.text.toString().trim().substring(0 until 20)
                else binding.etWriteThirdSubjectInput.text.toString().trim()
            val descriptionText =
                if(binding.etWriteThirdDescriptionInput.text.toString().trim().length > 200) binding.etWriteThirdDescriptionInput.text.toString().trim().substring(0 until 200)
                else binding.etWriteThirdDescriptionInput.text.toString().trim()
            writeViewModel.postTitle = titleText
            writeViewModel.postDetail = descriptionText
            val tagList = mutableListOf<TagDto>()
            for(tagText in binding.chipgroupWriteThirdTagListSaved.getAllChipsTagText()) {
                if (tagText.length > 8) tagList.add(TagDto(tagname = tagText.trim().substring(0 until 8)))
                else tagList.add(TagDto(tagname = tagText))
            }
            writeViewModel.postTagList = tagList
            findNavController().navigate(R.id.action_writeThirdFragment_to_writeFourthFragment)
        }
    }

    private fun initSubjectInput() {
        binding.etWriteThirdSubjectInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                val isDescriptionTextLimitError = binding.etWriteThirdDescriptionInput.text.length > 200
                val isDescriptionTextBlankError = binding.etWriteThirdDescriptionInput.text.trim().isNullOrBlank()
                val isTitleTextLimitError = s.toString().length > 20
                val isTitleTextBlankError = s.toString().trim().isNullOrBlank()
                val isErrorButtonActivateCondition = isTitleTextBlankError || isTitleTextLimitError || isDescriptionTextBlankError || isDescriptionTextLimitError

                displayTitleLimitError(isError = isTitleTextLimitError)
                displayTitleBlankError(isError = isTitleTextBlankError)
                if (isTitleTextLimitError || isTitleTextBlankError)
                    binding.etWriteThirdSubjectInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10_stroke_red)
                else
                    binding.etWriteThirdSubjectInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10)
                activationButtonNext(isError = isErrorButtonActivateCondition)

                binding.tvWriteThirdSubjectTextCountCurrent.text = binding.etWriteThirdSubjectInput.text.length.toString()
            }
        })
    }

    private fun initDescriptionInput() {
        binding.etWriteThirdDescriptionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                val isDescriptionTextLimitError = s.toString().length > 200
                val isDescriptionTextBlankError = s.toString().trim().isNullOrBlank()
                val isTitleTextLimitError = binding.etWriteThirdSubjectInput.text.length > 20
                val isTitleTextBlankError = binding.etWriteThirdSubjectInput.text.trim().isNullOrBlank()
                val isErrorButtonActivateCondition = isDescriptionTextBlankError || isDescriptionTextLimitError || isTitleTextBlankError || isTitleTextLimitError

                displayDescriptionBlankError(isDescriptionTextBlankError)
                displayDescriptionLimitError(isError = isDescriptionTextLimitError)
                if (isDescriptionTextBlankError || isDescriptionTextLimitError)
                    binding.etWriteThirdDescriptionInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10_stroke_red)
                else
                    binding.etWriteThirdDescriptionInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10)
                activationButtonNext(isError = isErrorButtonActivateCondition)

                binding.tvWriteThirdDescriptionTextCountCurrent.text = binding.etWriteThirdDescriptionInput.text.length.toString()
            }
        })
    }

    private fun initTagList() {
        setEditTextChangeListener()
        setAddTagClickListener()
        setEditTextAddTagKeyClickListener()
    }

    private fun setEditTextChangeListener() {
        binding.etWriteThirdTagInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().trim().isNullOrBlank()) {
                    binding.btnWriteThirdTagAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_main_C4C4C4))
                    binding.btnWriteThirdTagAdd.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10_stroke_gray_line)
                    binding.btnWriteThirdTagAdd.backgroundTintList = null
                } else {
                    binding.btnWriteThirdTagAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_FFFFFF))
                    binding.btnWriteThirdTagAdd.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10)
                    binding.btnWriteThirdTagAdd.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.line_orange_FFA077)
                }
            }
        })
    }

    private fun setAddTagClickListener() {
        binding.btnWriteThirdTagAdd.setOnClickListener {
            setAddTag()
        }
    }

    private fun setEditTextAddTagKeyClickListener() {
        binding.etWriteThirdTagInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    setAddTag()
                    true
                }
                else -> false
            }
        }
    }

    private fun setTagCountLimit() {
        with(binding) {
            val currentTagCount = chipgroupWriteThirdTagListSaved.size
            if (currentTagCount >= 8) {
                Toast.makeText(
                    requireContext(),
                    "태그는 8개까지 등록 가능합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setAddTag() {
        val originalTag = binding.etWriteThirdTagInput.text.toString().trim()
//                    val removeBlankTag = ReplaceUnicode.replaceBlankText(originalTag)

        if (originalTag.isEmpty()
            || binding.chipgroupWriteThirdTagListSaved.getAllChipsTagText()
                .contains(originalTag)
        ) {
            binding.etWriteThirdTagInput.setText("")
        } else {
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.item_write_tag_chip, null) as Chip
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )
            with(chip) {
                setTextAppearance(R.style.style_body2_kor)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C))
                setOnCloseIconClickListener {
                    binding.chipgroupWriteThirdTagListSaved.removeView(chip as View)
                    setTagCountLimit()
                }
                ensureAccessibleTouchTarget(42.dp)
                text = "#$originalTag"
            }
            if (binding.chipgroupWriteThirdTagListSaved.size < 8) {
                binding.chipgroupWriteThirdTagListSaved.addView(chip, layoutParams)
            }
            binding.etWriteThirdTagInput.setText("")
        }
        setTagCountLimit()
    }

    private fun ChipGroup.getAllChipsTagText(): List<String> {
        return (0 until childCount).mapNotNull { index ->
            val currentChip = getChildAt(index) as? Chip
            currentChip?.text.toString().split("#")[1]
        }
    }

    private fun ChipGroup.clearChips() {
        val chipViews = (0 until childCount).mapNotNull { index ->
            val view = getChildAt(index)
            if (view is Chip) view else null
        }
        chipViews.forEach { removeView(it) }
    }

    private fun activationButtonNext(isError: Boolean) {
        binding.btnWriteThirdNext.isEnabled = !isError
    }

    private fun displayTitleBlankError(isError: Boolean) {
        if (isError) binding.tvWriteThirdSubjectError.visibility = View.VISIBLE
        else binding.tvWriteThirdSubjectError.visibility = View.INVISIBLE
    }

    private fun displayTitleLimitError(isError: Boolean) {
        with(binding) {
            if (isError) {
                tvWriteThirdSubjectTextCountCurrent.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_FF0000))
                tvWriteThirdSubjectTextCountMax.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_FF0000))
            } else {
                tvWriteThirdSubjectTextCountCurrent.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C))
                tvWriteThirdSubjectTextCountMax.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_000000))
            }
        }
    }

    private fun displayDescriptionBlankError(isError: Boolean) {
        if (isError) binding.tvWriteThirdDescriptionError.visibility = View.VISIBLE
        else binding.tvWriteThirdDescriptionError.visibility = View.INVISIBLE
    }

    private fun displayDescriptionLimitError(isError: Boolean) {
        with(binding) {
            if (isError) {
                tvWriteThirdDescriptionTextCountCurrent.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_FF0000))
                tvWriteThirdDescriptionTextCountMax.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_FF0000))
            } else {
                tvWriteThirdDescriptionTextCountCurrent.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C))
                tvWriteThirdDescriptionTextCountMax.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_000000))
            }
        }
    }
}