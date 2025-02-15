package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepositoryImpl
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.Locale
import kotlin.collections.ArrayList

class DetailFragment : Fragment() {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var viewModel: RepresentativeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

//        val database = ElectionDatabase.getInstance(requireContext())
        val repository = RepositoryImpl()
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[RepresentativeViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        var stored : ArrayList<Representative>? = null
         if(Build.VERSION.SDK_INT >= 33){
             stored = savedInstanceState?.getParcelableArrayList("representatives", Representative::class.java)
        } else {
             stored = savedInstanceState?.getParcelableArrayList("representatives")
         }

        savedInstanceState?.getInt("motionLayoutCurrentState")
            ?.let { binding.motionLayout.transitionToState(it) }

        val adapter = RepresentativeListAdapter()
        binding.representativeRecyclerView.adapter = adapter
        if(stored != null) {
            viewModel.representatives.value=stored
        }

        viewModel.representatives.observe(viewLifecycleOwner) {
            viewModel.representatives_stored= it as ArrayList<Representative>
            adapter.submitList(it)
        }

        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.state.value = binding.state.selectedItem as String
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.state.value = binding.state.selectedItem as String
            }
        }

        binding.buttonLocation.setOnClickListener{
            if(checkLocationPermissions()){
                getLocation()
            }
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.getRepresentativesFromAddress()
            hideKeyboard()
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("onSaveInstanceState", "onSaveInstanceState: ${viewModel.representatives_stored}")
        outState.putParcelableArrayList("representatives",ArrayList<Parcelable>(viewModel.representatives_stored))
        outState.putInt("motionLayoutCurrentState",binding.motionLayout.currentState)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireContext())
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val address = geoCodeLocation(it)
                viewModel.useLocation(address)
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Timber.tag("geoCodeLocation").i("geoCodeLocation: %s", address)
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            ?.first() ?: Address("", "", "", "", "")
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}