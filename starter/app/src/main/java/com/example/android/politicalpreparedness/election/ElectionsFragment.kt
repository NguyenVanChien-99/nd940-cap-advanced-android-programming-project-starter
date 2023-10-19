package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    private val electionDao: ElectionDao by lazy {
        ElectionDatabase.getInstance(requireContext()).electionDao
    }
    private lateinit var viewModel: ElectionsViewModel
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View? {
        viewModel = ViewModelProvider(
            this,
            ElectionsViewModelFactory(electionDao)
        )[ElectionsViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)


        val upcomingAdapter = ElectionListAdapter(ElectionListener {
            viewModel.navigateToElectionDetail(it)
        })
        val savedAdapter = ElectionListAdapter(ElectionListener {
            viewModel.navigateToElectionDetail(it)
        })

        binding.upcomingElections.adapter = upcomingAdapter
        binding.savedElections.adapter = savedAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let {
                upcomingAdapter.submitList(it.elections)
            }
        }

        viewModel.savedElections.observe(viewLifecycleOwner) {
            it?.let {
                savedAdapter.submitList(it)
            }
        }

        viewModel.selectedElection.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        it.id,
                        it.division
                    )
                )
                viewModel.finishNavigateToElectionDetail()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchElections()
    }
}