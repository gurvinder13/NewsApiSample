package com.example.newsapisample.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapisample.Listener.ItemClickListener
import com.example.newsapisample.R
import com.example.newsapisample.adapters.PopularNewsAdapter
import com.example.newsapisample.models.Article
import com.example.newsapisample.ui.NewsViewModel
import com.example.newsapisample.utils.Constants
import com.example.newsapisample.utils.Constants.Companion.CONSTANT_0
import com.example.newsapisample.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchNewsFragment : Fragment(R.layout.fragment_search_news), ItemClickListener {

    private lateinit var popularNewsAdapter: PopularNewsAdapter
    private val viewModel by viewModel<NewsViewModel>()

    private val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        iv_back.setOnClickListener {
            activity?.onBackPressed()
        }
        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        popularNewsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            rvSearchNews.setPadding(CONSTANT_0, CONSTANT_0, CONSTANT_0, CONSTANT_0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error accrued: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= CONSTANT_0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        popularNewsAdapter = PopularNewsAdapter(Constants.HOME_NEWS, this)
        rvSearchNews.apply {
            adapter = popularNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    override fun onBookmarkClick(article: Article, position: Int) {
        viewModel.saveArticle(article)
        Snackbar.make(view!!, getString(R.string.bookmark_success), Snackbar.LENGTH_SHORT).show()
    }

    override fun onItemsClick(article: Article, position: Int) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }

        findNavController().navigate(
            R.id.action_searchNewsFragment_to_articleFragment,
            bundle
        )
    }
}