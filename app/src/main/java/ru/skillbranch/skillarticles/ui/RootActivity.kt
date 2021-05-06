package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {

    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        return
        setupToolbar()
        setupBottombar()
        setupSubmenu()

        val vmFactory = ViewModelFactory("0")
        viewModel = ViewModelProviders.of(this, vmFactory).get(ArticleViewModel::class.java)
        viewModel.observeState(this) {
            renderUi(it)
            setupToolbar()
        }
        viewModel.observeNotifications(this) {
            renderNotification(it)
        }

     }

    private fun setupToolbar() {
        TODO()
    }

    private fun renderUi(data: ArticleState) {
        TODO()
    }

    private fun renderNotification(notify: Notify) {
        TODO()
    }

    private fun setupBottombar() {
        TODO()
    }

    private fun setupSubmenu() {
        TODO()
    }

}
