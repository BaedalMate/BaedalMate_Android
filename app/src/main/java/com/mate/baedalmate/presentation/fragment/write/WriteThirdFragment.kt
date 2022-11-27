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
                if (binding.etWriteThirdSubjectInput.text.toString()
                        .trim().length > MAX_TITLE_LENGTH
                ) binding.etWriteThirdSubjectInput.text.toString().trim()
                    .substring(0 until MAX_TITLE_LENGTH)
                else binding.etWriteThirdSubjectInput.text.toString().trim()
            val descriptionText =
                if (binding.etWriteThirdDescriptionInput.text.toString()
                        .trim().length > MAX_DESCRIPTION_LENGTH
                ) binding.etWriteThirdDescriptionInput.text.toString().trim()
                    .substring(0 until MAX_DESCRIPTION_LENGTH)
                else binding.etWriteThirdDescriptionInput.text.toString().trim()
            writeViewModel.postTitle = titleText
            writeViewModel.postDetail = descriptionText
            val tagList = mutableListOf<TagDto>()
            for (tagText in binding.chipgroupWriteThirdTagListSaved.getAllChipsTagText()) {
                if (tagText.length > MAX_TAG_LENGTH_WRITE) tagList.add(
                    TagDto(
                        tagname = tagText.trim().substring(0 until MAX_TAG_LENGTH_WRITE)
                    )
                )
                else tagList.add(TagDto(tagname = tagText))
            }
            writeViewModel.postTagList = tagList
            findNavController().navigate(R.id.action_writeThirdFragment_to_writeFourthFragment)
        }
    }

    private fun initSubjectInput() {
        binding.etWriteThirdSubjectInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isDescriptionTextLimitError =
                    binding.etWriteThirdDescriptionInput.text.length > MAX_DESCRIPTION_LENGTH
                val isDescriptionTextBlankError =
                    binding.etWriteThirdDescriptionInput.text.trim().isNullOrBlank()
                val isTitleTextLimitError = s.toString().length > MAX_TITLE_LENGTH
                val isTitleTextBlankError = s.toString().trim().isNullOrBlank()
                val isErrorButtonActivateCondition =
                    isTitleTextBlankError || isTitleTextLimitError || isDescriptionTextBlankError || isDescriptionTextLimitError

                displayTitleLimitError(isError = isTitleTextLimitError)
                displayTitleBlankError(isError = isTitleTextBlankError)
                if (isTitleTextLimitError || isTitleTextBlankError)
                    binding.etWriteThirdSubjectInput.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10_stroke_red
                    )
                else
                    binding.etWriteThirdSubjectInput.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10
                    )
                activationButtonNext(isError = isErrorButtonActivateCondition)

                binding.tvWriteThirdSubjectTextCountCurrent.text =
                    binding.etWriteThirdSubjectInput.text.length.toString()
            }
        })
    }

    private fun initDescriptionInput() {
        binding.etWriteThirdDescriptionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isDescriptionTextLimitError = s.toString().length > MAX_DESCRIPTION_LENGTH
                val isDescriptionTextBlankError = s.toString().trim().isNullOrBlank()
                val isTitleTextLimitError =
                    binding.etWriteThirdSubjectInput.text.length > MAX_TITLE_LENGTH
                val isTitleTextBlankError =
                    binding.etWriteThirdSubjectInput.text.trim().isNullOrBlank()
                val isErrorButtonActivateCondition =
                    isDescriptionTextBlankError || isDescriptionTextLimitError || isTitleTextBlankError || isTitleTextLimitError

                displayDescriptionBlankError(isDescriptionTextBlankError)
                displayDescriptionLimitError(isError = isDescriptionTextLimitError)
                if (isDescriptionTextBlankError || isDescriptionTextLimitError)
                    binding.etWriteThirdDescriptionInput.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10_stroke_red
                    )
                else
                    binding.etWriteThirdDescriptionInput.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_white_radius_10
                    )
                activationButtonNext(isError = isErrorButtonActivateCondition)

                binding.tvWriteThirdDescriptionTextCountCurrent.text =
                    binding.etWriteThirdDescriptionInput.text.length.toString()
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                with(binding.btnWriteThirdTagAdd) {
                    if (s.toString().trim().isNullOrBlank()) {
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.gray_main_C4C4C4
                            )
                        )
                        background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.background_white_radius_10_stroke_gray_line
                        )
                        backgroundTintList = null
                    } else {
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white_FFFFFF
                            )
                        )
                        background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.background_white_radius_10
                        )
                        backgroundTintList =
                            ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.line_orange_FFA077
                            )
                    }
                }
            }
        })
    }

    private fun setAddTagClickListener() {
        binding.btnWriteThirdTagAdd.setOnClickListener {
            setAddingTag()
        }
    }

    private fun setEditTextAddTagKeyClickListener() {
        binding.etWriteThirdTagInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    setAddingTag()
                    true
                }
                else -> false
            }
        }
    }

    private fun setAddingTag() {
        val originalTagText = binding.etWriteThirdTagInput.text.toString().trim()
//        val removeBlankTag = ReplaceUnicode.replaceBlankText(originalTag)

        if (originalTagText.isNotEmpty() && !binding.chipgroupWriteThirdTagListSaved.getAllChipsTagText()
                .contains(originalTagText)
        )
            setTagChipProperties(originalTagText)
        clearTagInputEditText()
        displayTagCountLimitError()
    }

    private fun setTagChipProperties(tagText: String) {
        val chip = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_write_tag_chip, null) as Chip
        with(chip) {
            setTextAppearance(R.style.style_body2_kor)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C))
            setOnCloseIconClickListener {
                binding.chipgroupWriteThirdTagListSaved.removeView(chip as View)
                displayTagCountLimitError()

            }
            ensureAccessibleTouchTarget(42.dp)
            text =
                if (tagText.length > 8) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.write_third_tag_add_error_length_limit).format(
                            MAX_TAG_LENGTH_WRITE
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    "#${tagText.substring(0, MAX_TAG_LENGTH_WRITE)}"
                } else "#$tagText"
        }
        setTagCountLimit(chip)
    }

    private fun setTagCountLimit(chip: Chip) {
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )

        if (binding.chipgroupWriteThirdTagListSaved.size < MAX_TAG_COUNT_WRITE) {
            binding.chipgroupWriteThirdTagListSaved.addView(chip, layoutParams)
        }
    }

    private fun displayTagCountLimitError() {
        with(binding) {
            val currentTagCount = chipgroupWriteThirdTagListSaved.size
            if (currentTagCount >= MAX_TAG_COUNT_WRITE) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.write_third_tag_add_error_count_limit).format(
                        MAX_TAG_COUNT_WRITE
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun clearTagInputEditText() {
        binding.etWriteThirdTagInput.setText("")
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
                tvWriteThirdSubjectTextCountCurrent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_FF0000
                    )
                )
                tvWriteThirdSubjectTextCountMax.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_FF0000
                    )
                )
            } else {
                tvWriteThirdSubjectTextCountCurrent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_FB5F1C
                    )
                )
                tvWriteThirdSubjectTextCountMax.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_000000
                    )
                )
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
                tvWriteThirdDescriptionTextCountCurrent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_FF0000
                    )
                )
                tvWriteThirdDescriptionTextCountMax.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_FF0000
                    )
                )
            } else {
                tvWriteThirdDescriptionTextCountCurrent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_FB5F1C
                    )
                )
                tvWriteThirdDescriptionTextCountMax.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black_000000
                    )
                )
            }
        }
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

    companion object {
        private const val MAX_TITLE_LENGTH = 20
        private const val MAX_DESCRIPTION_LENGTH = 200
        private const val MAX_TAG_COUNT_WRITE = 4
        private const val MAX_TAG_LENGTH_WRITE = 8
    }
}