package com.omouravictor.ratesbr.presenter.rates

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.ratesbr.data.local.entity.toRateUiModel
import com.omouravictor.ratesbr.data.network.base.NetworkResultStatus
import com.omouravictor.ratesbr.data.network.hgfinanceapi.rates.toListRateEntity
import com.omouravictor.ratesbr.data.repository.RatesRepository
import com.omouravictor.ratesbr.presenter.base.UiResultState
import com.omouravictor.ratesbr.presenter.rates.model.RateUiModel
import kotlinx.coroutines.launch

class RatesViewModel @ViewModelInject constructor(
    private val ratesRepository: RatesRepository
) : ViewModel() {

    val ratesResult = MutableLiveData<UiResultState<List<RateUiModel>>>()
    private val currencies = "USD,EUR,JPY,GBP,CAD,AUD,ARS,CNY"

    init {
        getRates()
    }

    fun getRates() {
        viewModelScope.launch {
            ratesRepository.getRemoteRates(currencies).collect { networkResultStatus ->
                when (networkResultStatus) {
                    is NetworkResultStatus.Success -> {
                        val remoteRates = networkResultStatus.data.toListRateEntity()
                        ratesRepository.insertRates(remoteRates)
                        ratesResult.postValue(UiResultState.Success(remoteRates.map { it.toRateUiModel() }))
                    }

                    is NetworkResultStatus.Error -> {
                        ratesRepository.getLocalRates().collect { localRates ->
                            if (localRates.isNotEmpty())
                                ratesResult.postValue(UiResultState.Success(localRates.map { it.toRateUiModel() }))
                            else
                                ratesResult.postValue(UiResultState.Error(networkResultStatus.e))
                        }
                    }

                    is NetworkResultStatus.Loading -> {
                        ratesResult.postValue(UiResultState.Loading)
                    }
                }
            }
        }
    }
}