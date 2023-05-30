package com.example.newsapisample.Listener

import com.example.newsapisample.models.Article


interface ItemClickListener {
    fun onBookmarkClick(article: Article, position: Int)
    fun onItemsClick(article: Article, position: Int)

}
