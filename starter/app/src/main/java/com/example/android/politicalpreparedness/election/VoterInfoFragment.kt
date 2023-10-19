package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private val electionDao: ElectionDao by lazy {
        ElectionDatabase.getInstance(requireContext()).electionDao
    }
    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var viewModel: VoterInfoViewModel
    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {

        viewModel = ViewModelProvider(
            this,
            VoterInfoViewModelFactory(electionDao)
        )[VoterInfoViewModel::class.java]

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (args.argDivision.state.isNotEmpty() && args.argDivision.country.isNotEmpty()) {
            val address = "${args.argDivision.state}, ${args.argDivision.country}"

            viewModel.fetchVoterInfo(address, args.argElectionId)
            viewModel.fetchElection(args.argElectionId)
        } else {
            Toast.makeText(requireContext(), "Invalid address", Toast.LENGTH_SHORT).show()
        }
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */
        binding.buttonFollow.setOnClickListener {

            viewModel.voterInfo.value?.let { it1 ->
                if (viewModel.savedElection.value == null) {
                    viewModel.saveElection(it1.election)
                    binding.buttonFollow.setText(R.string.unfollow_election)
                } else {
                    viewModel.removeElection(it1.election)
                    binding.buttonFollow.setText(R.string.follow_election)
                }
            }
        }

        // TODO: Handle loading of URLs

        // TODO: Handle save button UI state
        // TODO: cont'd Handle save button clicks
        return binding.root
    }

    // TODO: Create method to load URL intents
}