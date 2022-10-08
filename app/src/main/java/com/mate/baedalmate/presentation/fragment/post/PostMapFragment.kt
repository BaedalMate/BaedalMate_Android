package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.RoundDialogFragment
import com.mate.baedalmate.databinding.FragmentPostMapBinding
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class PostMapFragment : RoundDialogFragment() {
    private var binding by autoCleared<FragmentPostMapBinding>()
    private val args by navArgs<PostMapFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogHeightSizeRatio = 0.6f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCloseClickListener()
        initKakaoMap()
    }

    private fun initCloseClickListener() {
        binding.btnPostMapClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initKakaoMap() {
        val mapView = MapView(requireActivity())
        val mapViewContainer = binding.layoutPostMap
        val mapPoint = MapPoint.mapPointWithGeoCoord(args.y.toDouble(), args.x.toDouble())
        mapViewContainer.addView(mapView)
        mapView.setMapCenterPoint(mapPoint, true)
        mapView.setZoomLevel(1, true)

        val marker = MapPOIItem()
        marker.itemName = args.name
        marker.tag = 1
        marker.mapPoint = mapPoint

        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageResourceId = R.drawable.ic_map_locator_marker_navy
        marker.isCustomImageAutoscale = true
        marker.setCustomImageAnchor(0.5f, 1.0f)
        marker.isShowDisclosureButtonOnCalloutBalloon = false
//        marker.markerType = MapPOIItem.MarkerType.RedPin // 기본으로 제공하는 BluePin 마커 모양.
//        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker)
    }
}