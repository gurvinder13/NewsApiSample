package com.example.newsapisample.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapisample.models.Article
import com.example.newsapisample.utils.Constants
import com.example.newsapisample.R
import kotlinx.android.synthetic.main.items_popular_news_layout.view.*

import kotlinx.android.synthetic.main.items_top_news_layout.view.ivBookmarkImage


class DetailsNewsAdapter(type: String,private val clickListener: ClickListener) : RecyclerView.Adapter<DetailsNewsAdapter.ArticleViewHolder>() {
    private var newType: String = type

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.items_popular_news_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            if (newType == Constants.HOME_NEWS) {
                ivBookmarkImage.setBackgroundResource(R.drawable.bookmark_grey)
            } else {
                ivBookmarkImage.setBackgroundResource(R.drawable.bookmark)
            }
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
        }
        holder.itemView.ivBookmarkImage.setOnClickListener {
            clickListener.onBookmarkClick(article,position)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    interface ClickListener {
        fun onBookmarkClick(article: Article, position: Int)

    }
}