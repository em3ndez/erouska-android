package cz.covid19cz.erouska.ui.exposure

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.navArgs
import cz.covid19cz.erouska.AppConfig
import cz.covid19cz.erouska.R
import cz.covid19cz.erouska.databinding.FragmentExposureBinding
import cz.covid19cz.erouska.ext.hide
import cz.covid19cz.erouska.ext.show
import cz.covid19cz.erouska.ui.base.BaseFragment
import cz.covid19cz.erouska.ui.exposure.event.ExposuresCommandEvent
import cz.covid19cz.erouska.ui.exposurehelp.entity.ExposureHelpType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_exposure.*

@AndroidEntryPoint
class ExposureFragment : BaseFragment<FragmentExposureBinding, ExposureVM>(
    R.layout.fragment_exposure,
    ExposureVM::class
) {

    val args: ExposureFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableUpInToolbar(true, IconType.CLOSE)

        activity?.title = AppConfig.exposureUITitle

        symptoms_text.text = AppConfig.symptomsUITitle
        spread_text.text = AppConfig.spreadPreventionUITitle
        earlier_exposures_text.text = AppConfig.recentExposuresUITitle
        symptoms_content.text = AppConfig.riskyEncountersWithSymptoms
        no_symptoms_content.text = AppConfig.riskyEncountersWithoutSymptoms

        viewModel.checkExposures(args.demo)

        subscribe(ExposuresCommandEvent::class) {
            when (it.command) {
                ExposuresCommandEvent.Command.NO_RECENT_EXPOSURES -> onNoRecentExposures()
                ExposuresCommandEvent.Command.RECENT_EXPOSURE -> onRecentExposures()
            }
        }

        setupListeners()
    }

    private fun setupListeners() {
        symptoms_container.setOnClickListener { navigate(ExposureFragmentDirections.actionNavExposureToNavExposureHelp(ExposureHelpType.SYMPTOMS)) }
        spread_prevention_container.setOnClickListener { navigate(ExposureFragmentDirections.actionNavExposureToNavExposureHelp(ExposureHelpType.PREVENTION)) }
        earlier_exposures_container.setOnClickListener { navigate(R.id.action_nav_dashboard_to_nav_recent_exposures) }
    }

    private fun onNoRecentExposures() {
        no_exposures_header.text = AppConfig.noEncounterHeader
        no_exposures_body.text = AppConfig.noEncounterBody

        no_exposures_group.show()
        exposures_group.hide()
    }

    private fun onRecentExposures() {
        exposures_group.show()
        no_exposures_group.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.exposure, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
