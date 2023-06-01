package com.omouravictor.ratesbr.presenter.stocks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.ratesbr.data.local.entity.toStockUiModel
import com.omouravictor.ratesbr.data.network.base.NetworkResultStatus
import com.omouravictor.ratesbr.data.network.hgfinanceapi.stock.toListStockEntity
import com.omouravictor.ratesbr.data.repository.StocksRepository
import com.omouravictor.ratesbr.presenter.base.UiResultStatus
import com.omouravictor.ratesbr.presenter.stocks.model.StockUiModel
import kotlinx.coroutines.launch

class StocksViewModel @ViewModelInject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {

    val stocksResult = MutableLiveData<UiResultStatus<List<StockUiModel>>>()

    init {
        getStocks()
    }

    fun getStocks() {
        viewModelScope.launch {
            stocksRepository.getRemoteStocks("stocks").collect { networkResultStatus ->
                when (networkResultStatus) {
                    is NetworkResultStatus.Success -> {
                        val remoteStocks = networkResultStatus.data.toListStockEntity()
                        stocksRepository.insertStocks(remoteStocks)
                        stocksResult.postValue(UiResultStatus.Success(remoteStocks.map { it.toStockUiModel() }))
                    }

                    is NetworkResultStatus.Error -> {
                        stocksRepository.getLocalStocks().collect { localStocks ->
                            if (localStocks.isNotEmpty())
                                stocksResult.postValue(UiResultStatus.Success(localStocks.map { it.toStockUiModel() }))
                            else
                                stocksResult.postValue(UiResultStatus.Error(networkResultStatus.message))
                        }
                    }

                    is NetworkResultStatus.Loading -> {
                        stocksResult.postValue(UiResultStatus.Loading)
                    }
                }
            }
        }
    }
}