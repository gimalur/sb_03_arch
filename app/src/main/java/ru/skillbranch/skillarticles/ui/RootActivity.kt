package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {

    private val viewModel: ArticleViewModel by viewModels { ViewModelFactory("0") }
    private lateinit var vb: ActivityRootBinding

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vb = ActivityRootBinding.inflate(layoutInflater)

        setContentView(vb.root)
        setupToolbar()
        setupBottombar()
        setupSubmenu()

        viewModel.observeState(this) {
            renderUi(it)
            setupToolbar()
        }
        viewModel.observeNotifications(this) {
            renderNotification(it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu.findItem(R.id.action_search)
        searchView = (menuItem.actionView as SearchView)
        searchView.queryHint = "Search"

        if (viewModel.currentState.isSearch) {
            menuItem.expandActionView()
            searchView.setQuery(viewModel.currentState.searchQuery, false)
            searchView.requestFocus()
        } else {
            searchView.clearFocus()
        }

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun setupToolbar() {
        setSupportActionBar(vb.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (vb.toolbar.childCount > 2) vb.toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        (logo?.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = dpToIntPx(40)
            it.height = dpToIntPx(40)
            it.marginEnd = dpToIntPx(16)
            logo.layoutParams = it
        }
    }

    private fun renderUi(data: ArticleState) {
        vb.bottombar.binding.btnSettings.isChecked = data.isShowMenu
        if (data.isShowMenu) vb.submenu.open() else vb.submenu.close()

        vb.bottombar.binding.btnLike.isChecked = data.isLike
        vb.bottombar.binding.btnBookmark.isChecked = data.isBookmark

        vb.submenu.binding.switchMode.isChecked = data.isDarkMode
        delegate.localNightMode = if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (data.isBigText) {
            vb.tvTextContent.textSize = 18f
            vb.submenu.binding.btnTextUp.isChecked = true
            vb.submenu.binding.btnTextDown.isChecked = false
        } else {
            vb.tvTextContent.textSize = 14f
            vb.submenu.binding.btnTextUp.isChecked = false
            vb.submenu.binding.btnTextDown.isChecked = true
        }

        vb.tvTextContent.text = if (data.isLoadingContent) "Loading..." else data.content.first() as String

        vb.toolbar.title = data.title ?: "Skill Articles"
        vb.toolbar.subtitle = data.category ?: "Loading..."
        if (data.categoryIcon != null) vb.toolbar.logo = getDrawable(data.categoryIcon as Int)
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(vb.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
        when (notify) {
            is Notify.TextMessage -> Unit
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(R.color.white))
                    setActionTextColor(getColor(R.color.color_accent_dark))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }
        snackbar.show()
    }

    private fun setupBottombar() {
        vb.bottombar.binding.btnLike.setOnClickListener { viewModel.handleLike() }
        vb.bottombar.binding.btnBookmark.setOnClickListener { viewModel.handleBookmark() }
        vb.bottombar.binding.btnShare.setOnClickListener { viewModel.handleShare() }
        vb.bottombar.binding.btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
    }

    private fun setupSubmenu() {
        vb.submenu.binding.btnTextUp.setOnClickListener { viewModel.handleUpText() }
        vb.submenu.binding.btnTextDown.setOnClickListener { viewModel.handleDownText() }
        vb.submenu.binding.switchMode.setOnClickListener { viewModel.handleNightMode() }
    }

}
