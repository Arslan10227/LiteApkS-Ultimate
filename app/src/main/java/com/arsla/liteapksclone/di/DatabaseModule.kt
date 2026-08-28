package com.arsla.liteapksclone.di

import android.content.Context
import androidx.room.Room
import com.arsla.liteapksclone.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "liteapks.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun providePostDao(db: AppDatabase) = db.postDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase) = db.categoryDao()

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase) = db.searchHistoryDao()

    @Provides
    fun provideDownloadDao(db: AppDatabase) = db.downloadDao()
}
