package ru.skillbranch.skillarticles.ui.custom.behavior

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar

class ArticleSubmenuBehavior : CoordinatorLayout.Behavior<ArticleSubmenu>() {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        return dependency is Bottombar
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        return  if (child.isOpen && dependency is Bottombar && dependency.translationY > 0) {
            animate(child, dependency)
            true
        } else {
            false
        }
    }

    private fun animate(child: ArticleSubmenu, dependency: View) {
        val factor = dependency.translationY / dependency.height
        child.translationX = (child.width + child.marginRight) * factor
    }
}
