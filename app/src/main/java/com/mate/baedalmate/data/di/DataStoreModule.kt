package com.mate.baedalmate.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mate.baedalmate.data.repository.TokenPreferencesRepositoryImpl
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val TOKEN_PREFERENCES = "token_preferences"

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    name = TOKEN_PREFERENCES
)

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindTokenPreferencesRepository(
        tokenPreferencesRepositoryImpl: TokenPreferencesRepositoryImpl
    ): TokenPreferencesRepository

    companion object {
        @Provides
        @Singleton
        fun provideTokenDataStorePreferences(@ApplicationContext applicationContext: Context) : DataStore<Preferences> {
            return applicationContext.tokenDataStore
        }
    }
}