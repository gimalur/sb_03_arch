package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String): IArticleViewModel, BaseViewModel<ArticleState>(ArticleState()) {

    private val repository = ArticleRepository
    private var menuIsShown: Boolean = false

    init {
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                author = article.author,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format(),
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }
    }

    override fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleUpText() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isBigText = true))
    }

    override fun handleDownText() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isBigText = false))
    }

    override fun handleBookmark() {
        val info = currentState.toArticlePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        val msg = if (currentState.isBookmark) "Add to bookmark" else "Remove from bookmark"
        notify(Notify.TextMessage(msg))
    }

    override fun handleLike() {
        val isLiked = currentState.isLike
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }
        toggleLike()

        val msg = if (!isLiked) {
            Notify.TextMessage("Mark is liked")
        } else {
            Notify.ActionMessage(
                "Don't like it anymore",
                "No, still like it",
                toggleLike
            )
        }
        notify(msg)
    }

    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    override fun handleToggleMenu() {
        updateState { state ->
            state.copy(isShowMenu = !state.isShowMenu).also { menuIsShown = !state.isShowMenu }
        }
    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch) }
    }

    override fun handleSearch(query: String?) {
        updateState { it.copy(searchQuery = query) }
    }


}

data class ArticleState(
    val isAuth: Boolean = false,
    val isLoadingContent: Boolean = true,
    val isLoadingReviews: Boolean = true,
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false,
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val searchResults: List<Pair<Int, Int>> = emptyList(), //start and end positions
    val searchPosition: Int = 0,
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<Any> = emptyList(),
    val reviews: List<Any> = emptyList(),



)
