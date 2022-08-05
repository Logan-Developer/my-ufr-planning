package fr.loganH.myufrplanning.ui.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import fr.loganH.myufrplanning.api.SednaService
import fr.loganH.myufrplanning.data.datasource.PlanningRemoteDataSource
import fr.loganH.myufrplanning.data.repository.PlanningRepository
import fr.loganH.myufrplanning.databinding.FragmentOverviewBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 * It displays the planning according to the user preferences.
 */
class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null

    // view model
    private val viewModel: OverviewViewModel by lazy {
        OverviewViewModel(PlanningRepository(PlanningRemoteDataSource(SednaService.create())))
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeContainerOverview.setOnRefreshListener {
            viewModel.refresh()
        }

        // setup recycler view
        binding.recyclerViewOverview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOverview.adapter = OverviewAdapter(emptyList())

        // setup view model
        viewModel.planning.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.recyclerViewOverview.visibility = View.VISIBLE
                binding.recyclerViewOverview.adapter = OverviewAdapter(it)
                binding.swipeContainerOverview.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}