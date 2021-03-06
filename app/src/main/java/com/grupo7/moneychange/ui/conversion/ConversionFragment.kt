package com.grupo7.moneychange.ui.conversion

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.grupo7.moneychange.R
import com.grupo7.moneychange.databinding.ConversionFragmentBinding
import com.grupo7.moneychange.ui.adapters.IRecyclerViewAdapter
import com.grupo7.moneychange.ui.common.PermissionRequester
import com.grupo7.moneychange.ui.entitiesUi.HistoryItem
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.scope.viewModel

class ConversionFragment : Fragment() {

    private val conversionViewModel: ConversionViewModel by currentScope.viewModel(this)
    private lateinit var dataBindingView: ConversionFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBindingView = ConversionFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = conversionViewModel
        }
        return dataBindingView.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataBindingView.lifecycleOwner = this.viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            PermissionRequester(it, Manifest.permission.ACCESS_COARSE_LOCATION).request {
                conversionViewModel.updateLocation()
            }
        }
        initListAdapter()
    }

    private fun initListAdapter() {
        val viewModel = dataBindingView.viewModel
        viewModel?.let {
            val adapter = IRecyclerViewAdapter(conversionViewModel::clickDataUp, ::navigationDetailConversionFragment)
            dataBindingView.recyclerView.adapter = adapter
        }
    }

    private fun navigationDetailConversionFragment(item: HistoryItem) {
        val bundle = Bundle().apply { putSerializable("history", item) }
        view?.findNavController()?.navigate(R.id.action_conversionFragment_to_detailConversionFragment, bundle)
    }
}