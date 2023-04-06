package com.omouravictor.ratesbr.framework.di

import com.omouravictor.ratesbr.data.local.AppDataBase
import com.omouravictor.ratesbr.data.network.ApiService
import com.omouravictor.ratesbr.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideRatesLocalRepository(
        dataBase: AppDataBase,
        apiService: ApiService
    ): RatesRepository = RatesRepository(dataBase.rateDao(), apiService)

    @Singleton
    @Provides
    fun provideBitCoinsLocalRepository(
        dataBase: AppDataBase,
        apiService: ApiService
    ): BitCoinsRepository = BitCoinsRepository(dataBase.bitCoinDao(), apiService)

    @Singleton
    @Provides
    fun provideStocksLocalRepository(
        dataBase: AppDataBase,
        apiService: ApiService
    ): StocksRepository = StocksRepository(dataBase.stockDao(), apiService)

}