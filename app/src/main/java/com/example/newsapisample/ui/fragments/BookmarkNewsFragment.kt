package com.example.newsapisample.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapisample.Listener.ItemClickListener
import com.example.newsapisample.R
import com.example.newsapisample.adapters.PopularNewsAdapter
import com.example.newsapisample.models.Article
import com.example.newsapisample.ui.NewsViewModel
import com.example.newsapisample.utils.Constants.Companion.BOOKMARK_NEWS
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_bookmark_news.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookmarkNewsFragment : Fragment(R.layout.fragment_bookmark_news), ItemClickListener {

    private lateinit var popularNewsAdapter: PopularNewsAdapter
    private val viewModel by viewModel<NewsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        iv_back.setOnClickListener {
            activity?.onBackPressed()
        }
        iv_search.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", null)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_searchNewsFragment,
                bundle
            )
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, { articles ->
            popularNewsAdapter.differ.submitList(articles)
        })
    }

    override fun onBookmarkClick(article: Article, position: Int) {
        bookmarkAlert(article)
    }

    override fun onItemsClick(article: Article, position: Int) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }

        findNavController().navigate(
            R.id.action_savedNewsFragment_to_articleFragment,
            bundle
        )
    }

    private fun setupRecyclerView() {
        popularNewsAdapter = PopularNewsAdapter(BOOKMARK_NEWS, this)
        rvSavedNews.apply {
            adapter = popularNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun bookmarkAlert(article: Article) {
        val titleView = TextView(context)
        titleView.text = getString(R.string.str_alert)
        titleView.gravity = Gravity.START
        titleView.setPadding(25, 20, 20, 20)
        titleView.textSize = 15f
        titleView.typeface = Typeface.DEFAULT_BOLD
        titleView.setBackgroundColor(ContextCompat.getColor(requireContext(),
            R.color.white))
        titleView.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.black))
        val ad = AlertDialog.Builder(requireContext()).create()
        ad.setCustomTitle(titleView)
        ad.setCancelable(false)
        ad.setMessage(getString(R.string.bookmark_alert_note))
        ad.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.ok)
        ) { _, _ ->
            viewModel.deleteArticle(article)
            Snackbar.make(requireView(), getString(R.string.successfully_deleted_bookmark), Snackbar.LENGTH_SHORT)
                .show()
        }
        ad.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        ad.show()

        val messageView = ad.findViewById<TextView>(android.R.id.message)
        if (messageView != null) {
            messageView.gravity = Gravity.START
        }
        val buttonOK: Button = ad.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonOK.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.black))
        val negative: Button = ad.getButton(DialogInterface.BUTTON_NEGATIVE)
        negative.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.black))
    }
}