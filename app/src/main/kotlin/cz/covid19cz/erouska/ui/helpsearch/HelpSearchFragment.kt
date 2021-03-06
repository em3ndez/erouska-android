package cz.covid19cz.erouska.ui.helpsearch

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.databinding.ObservableList
import androidx.lifecycle.Observer
import cz.covid19cz.erouska.R
import cz.covid19cz.erouska.databinding.FragmentHelpSearchBinding
import cz.covid19cz.erouska.ext.attachKeyboardController
import cz.covid19cz.erouska.ext.show
import cz.covid19cz.erouska.ui.base.BaseFragment
import cz.covid19cz.erouska.ui.helpsearch.data.SearchableQuestion
import cz.covid19cz.erouska.utils.Markdown
import cz.covid19cz.erouska.utils.showOrHide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_help_search.*
import kotlinx.android.synthetic.main.search_toolbar.*
import javax.inject.Inject

@AndroidEntryPoint
class HelpSearchFragment : BaseFragment<FragmentHelpSearchBinding, HelpSearchVM>(
    R.layout.fragment_help_search,
    HelpSearchVM::class
) {

    @Inject
    internal lateinit var markdown: Markdown

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                goBack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fillQuestions()

        viewModel.queryData.observe(this,
            Observer {
                when {
                    it.isEmpty() -> empty_text_view.text = ""
                    it.length < viewModel.minQueryLength -> empty_text_view.setText(R.string.help_type_more)
                    else -> empty_text_view.setText(R.string.help_no_results)
                }
            })

        viewModel.searchEmpty.observe(this,
            Observer { isEmpty ->
                help_categories.showOrHide(!isEmpty)
                empty_text_view.showOrHide(isEmpty)
                empty_image_view.showOrHide(isEmpty)
            })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableUpInToolbar(true, IconType.UP)

        // Associate searchable configuration with the SearchView
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        activity?.toolbar_search_view?.apply {

            attachKeyboardController()
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    viewModel.searchQuery(query)
                    return true
                }

            })

            setOnCloseListener {
                goBack()
                true
            }

            show()
            requestFocus()

        }

    }

    override fun onBackPressed(): Boolean {
        goBack()
        return true
    }

    private fun removeSearchViewCallbacks() {
        activity?.toolbar_search_view?.apply {
            setOnCloseListener(null)
            setOnQueryTextListener(null)
        }
    }

    private fun goBack() {
        removeSearchViewCallbacks()
        collapseSearchView()
        navController().navigateUp()
    }

    private fun collapseSearchView() {
        activity?.toolbar_search_view?.apply {
            setQuery("", false)
        }
    }

}
