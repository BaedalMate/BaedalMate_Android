package com.mate.baedalmate.presentation.fragment.location

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentLocationCertificationBinding
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.presentation.adapter.write.WriteSecondDormitorySpinnerAdapter
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class LocationCertificationFragment : Fragment() {
    private var binding by autoCleared<FragmentLocationCertificationBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private lateinit var locationManager: LocationManager
    private lateinit var spinnerAdapter: WriteSecondDormitorySpinnerAdapter
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationCertificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDormitorySpinner()
        setDormitoryLocation()
        checkUserLocationServiceEnabled()
        setLocationChangeClickListener()
        observeChangeSpinnerSelectedItem()
        observeLocationChange()
        initMap()
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedList: List<String> = permissions.filter {
                !it.value
            }.map {
                it.key
            }

            when {
                deniedList.isNotEmpty() -> {
                    val map = deniedList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED"
                    }
                    map["DENIED"]?.let {
                        // request denied , request again
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.location_certification_error_message_permission_denied_first),
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            locationPermissions,
                            1000
                        )
                    }
                    map["EXPLAINED"]?.let {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.location_certification_error_message_permission_denied_second),
                            Toast.LENGTH_SHORT
                        ).show()
                        //request denied ,send to settings
                    }

                }
                else -> {
                    //All request are permitted
                    setUserCurrentLocation()
                }
            }
        }

    private fun initDormitorySpinner() {
        val items = resources.getStringArray(R.array.dormitory_list)
        spinnerAdapter = WriteSecondDormitorySpinnerAdapter(
            requireContext(),
            R.layout.item_spinner_dormitory_list,
            items.toMutableList()
        )
        binding.spinnerLocationCertificationUserLocationInputDormitory.adapter = spinnerAdapter
    }

    private fun setDormitoryLocationMarker() {
        setLocationMarker(DORMITORY_SUNGLIM_LOCATION)
        setLocationMarker(DORMITORY_KB_LOCATION)
        setLocationMarker(DORMITORY_BURAM_LOCATION)
        setLocationMarker(DORMITORY_NURI_LOCATION)
        setLocationMarker(DORMITORY_SULIM_LOCATION)
    }

    private fun checkUserLocationServiceEnabled() {
        if (::locationManager.isInitialized.not()) {
            locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnable) {
            permissionLauncher.launch(locationPermissions)
        }
    }

    private fun observeChangeSpinnerSelectedItem() {
        binding.spinnerLocationCertificationUserLocationInputDormitory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    checkPermitRangeInside()

                    // 선택한 기숙사가 허용하는 범위를 보여줄 수 있도록 설정하는 부분
                    /*
                    mapView.removeAllCircles()
                    when (binding.spinnerLocationCertificationUserLocationInputDormitory.getItemAtPosition(
                        position
                    )) {
                        getString(R.string.nuri) -> {
                            addPermitRangeCircle(DORMITORY_NURI_LOCATION)
                        }
                        getString(R.string.sunglim) -> {
                            addPermitRangeCircle(DORMITORY_SUNGLIM_LOCATION)
                        }
                        getString(R.string.kb) -> {
                            addPermitRangeCircle(DORMITORY_KB_LOCATION)
                        }
                        getString(R.string.buram) -> {
                            addPermitRangeCircle(DORMITORY_BURAM_LOCATION)
                        }
                        getString(R.string.sulim) -> {
                            addPermitRangeCircle(DORMITORY_SULIM_LOCATION)
                        }
                    }
                     */
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setLocationChangeClickListener() {
        binding.btnLocationCertificationChange.setOnDebounceClickListener {
            val newDormitory =
                when (binding.spinnerLocationCertificationUserLocationInputDormitory.selectedItem) {
                    getString(R.string.sunglim) -> Dormitory.SUNGLIM
                    getString(R.string.kb) -> Dormitory.KB
                    getString(R.string.buram) -> Dormitory.BURAM
                    getString(R.string.nuri) -> Dormitory.NURI
                    getString(R.string.sulim) -> Dormitory.SULIM
                    else -> Dormitory.SUNGLIM
                }
            memberViewModel.requestChangeUserDormitory(newDormitory = newDormitory)
        }
    }

    private fun observeLocationChange() {
        memberViewModel.isDormitoryChangeSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true)
                findNavController().navigateUp()
        }
    }

    private fun initMap() {
        mapView = MapView(requireActivity())
        val mapViewContainer = binding.layoutLocationCertificationUserMap
        mapViewContainer.addView(mapView)
        mapView.setZoomLevel(1, true)
        setDormitoryLocationMarker()
    }

    private fun setLocationMarker(locationName: Location) {
        val marker = MapPOIItem()
        val mapPoint = MapPoint.mapPointWithGeoCoord(locationName.latitude, locationName.longitude)
        marker.tag = 1
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.RedPin // 기본으로 제공하는 BluePin 마커 모양.
        marker.selectedMarkerType =
            MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.isShowDisclosureButtonOnCalloutBalloon = false

        when (locationName) {
            DORMITORY_SUNGLIM_LOCATION -> {
                marker.itemName = getString(R.string.sunglim)
            }
            DORMITORY_KB_LOCATION -> {
                marker.itemName = getString(R.string.kb)
            }
            DORMITORY_BURAM_LOCATION -> {
                marker.itemName = getString(R.string.buram)
            }
            DORMITORY_NURI_LOCATION -> {
                marker.itemName = getString(R.string.nuri)
            }
            DORMITORY_SULIM_LOCATION -> {
                marker.itemName = getString(R.string.sulim)
            }
        }
        mapView.addPOIItem(marker)
    }

    private fun addPermitRangeCircle(locationName: Location) {
        val permitRangeCircle = MapCircle(
            MapPoint.mapPointWithGeoCoord(
                locationName.latitude, locationName.longitude
            ),  // center
            PERMIT_LOCATION_DISTANCE.toInt(),  // radius
            Color.argb(128, 255, 0, 0),  // strokeColor
            Color.argb(128, 0, 255, 0) // fillColor
        )
        mapView.addCircle(permitRangeCircle)
    }

    private fun setUserCurrentLocation() {
        mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        mapView.setCurrentLocationEventListener(object : MapView.CurrentLocationEventListener {
            override fun onCurrentLocationUpdate(p0: MapView?, mapPoint: MapPoint?, p2: Float) {
                mapView.setMapCenterPoint(mapPoint, true)
                currentLocation.latitude = mapPoint!!.mapPointGeoCoord.latitude
                currentLocation.longitude = mapPoint!!.mapPointGeoCoord.longitude
                checkPermitRangeInside()
            }

            override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}
            override fun onCurrentLocationUpdateFailed(p0: MapView?) {}
            override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}
        })
    }

    private fun checkPermitRangeInside() {
        var isPermitChangeLocation = false

        when (binding.spinnerLocationCertificationUserLocationInputDormitory.selectedItem) {
            getString(R.string.nuri) -> {
                isPermitChangeLocation =
                    currentLocation.distanceTo(DORMITORY_NURI_LOCATION) <= PERMIT_LOCATION_DISTANCE
            }
            getString(R.string.sunglim) -> {
                isPermitChangeLocation =
                    currentLocation.distanceTo(DORMITORY_SUNGLIM_LOCATION) <= PERMIT_LOCATION_DISTANCE
            }
            getString(R.string.kb) -> {
                isPermitChangeLocation =
                    currentLocation.distanceTo(DORMITORY_KB_LOCATION) <= PERMIT_LOCATION_DISTANCE
            }
            getString(R.string.buram) -> {
                isPermitChangeLocation =
                    currentLocation.distanceTo(DORMITORY_BURAM_LOCATION) <= PERMIT_LOCATION_DISTANCE
            }
            getString(R.string.sulim) -> {
                isPermitChangeLocation =
                    currentLocation.distanceTo(DORMITORY_SULIM_LOCATION) <= PERMIT_LOCATION_DISTANCE
            }
            else -> {
                isPermitChangeLocation = false
            }
        }

        binding.btnLocationCertificationChange.isEnabled = isPermitChangeLocation
        binding.tvLocationCertificationUserErrorMessage.visibility =
            if (isPermitChangeLocation) View.INVISIBLE else View.VISIBLE
        binding.tvLocationCertificationUserErrorMessage.text =
            getString(R.string.location_certification_error_message_location_out).format(binding.spinnerLocationCertificationUserLocationInputDormitory.selectedItem)
    }

    companion object {
        val currentLocation = Location(LocationManager.NETWORK_PROVIDER)
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val DORMITORY_SUNGLIM_LOCATION = Location(LocationManager.NETWORK_PROVIDER)
        val DORMITORY_KB_LOCATION = Location(LocationManager.NETWORK_PROVIDER)
        val DORMITORY_BURAM_LOCATION = Location(LocationManager.NETWORK_PROVIDER)
        val DORMITORY_NURI_LOCATION = Location(LocationManager.NETWORK_PROVIDER)
        val DORMITORY_SULIM_LOCATION = Location(LocationManager.NETWORK_PROVIDER)
        const val PERMIT_LOCATION_DISTANCE = 200f

        fun setDormitoryLocation() {
            DORMITORY_SUNGLIM_LOCATION.longitude = 127.07597
            DORMITORY_SUNGLIM_LOCATION.latitude = 37.63556
            DORMITORY_KB_LOCATION.longitude = 127.076126
            DORMITORY_KB_LOCATION.latitude = 37.635994
            DORMITORY_BURAM_LOCATION.longitude = 127.07612
            DORMITORY_BURAM_LOCATION.latitude = 37.63636
            DORMITORY_NURI_LOCATION.longitude = 127.07709
            DORMITORY_NURI_LOCATION.latitude = 37.634426
            DORMITORY_SULIM_LOCATION.longitude = 127.078285
            DORMITORY_SULIM_LOCATION.latitude = 37.636044
        }
    }
}