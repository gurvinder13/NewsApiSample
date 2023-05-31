package com.example.newsapisample.di

import android.app.Application
import androidx.room.Room
import com.example.newsapisample.api.NewsApi
import com.example.newsapisample.db.ArticleDao
import com.example.newsapisample.db.ArticleDatabase
import com.example.newsapisample.repository.NewsRepository
import com.example.newsapisample.ui.NewsViewModel
import com.example.newsapisample.utils.Constants
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelScope = module {
single { NewsViewModel(get()) }
}

val repositoryModule = module {
    fun provideUserRepository(api: NewsApi,db: ArticleDatabase): NewsRepository {
        return NewsRepository(api,db)
    }
    single { provideUserRepository(get(),get()) }
}

val databaseModule = module {

    fun provideDatabase(application: Application): ArticleDatabase {
        return Room.databaseBuilder(application, ArticleDatabase::class.java, "article_db.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


    fun provideDao(database: ArticleDatabase): ArticleDao {
        return database.getArticleDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}

val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    single { provideUserApi(get()) }
}

val netModule = module {
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    fun provideHttpClient(cache: Cache): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .cache(cache)

        return okHttpClientBuilder.build()
    }

    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
    }


    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
          //  .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    single { provideCache(androidApplication()) }
    single { provideHttpClient(get()) }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }

}