package com.omouravictor.br_inco.rates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.omouravictor.br_inco.data.local.entity.RateEntity
import com.omouravictor.br_inco.data.local.entity.toRateUiModel
import com.omouravictor.br_inco.data.network.base.NetworkResultStatus
import com.omouravictor.br_inco.data.network.hgfinanceapi.rates.ApiRatesResponse
import com.omouravictor.br_inco.data.network.hgfinanceapi.rates.ApiRatesResultsItemResponse
import com.omouravictor.br_inco.data.network.hgfinanceapi.rates.ApiRatesResultsResponse
import com.omouravictor.br_inco.data.network.hgfinanceapi.rates.toRatesUiModelList
import com.omouravictor.br_inco.data.repository.RatesRepository
import com.omouravictor.br_inco.presenter.base.DataSource
import com.omouravictor.br_inco.presenter.base.UiResultStatus
import com.omouravictor.br_inco.presenter.rates.RatesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

@ExperimentalCoroutinesApi
class RatesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var ratesRepository: RatesRepository

    private lateinit var viewModel: RatesViewModel
    private val testDispatchers = TestDispatchers()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatchers.standardTestDispatcher)
        viewModel = RatesViewModel(ratesRepository, testDispatchers)
    }

    @Test
    fun `when getRates is called, should fetch REMOTE rates successfully`() = runTest {
        val mockApiRatesResponse = ApiRatesResponse(
            ApiRatesResultsResponse(
                linkedMapOf(
                    "USD" to ApiRatesResultsItemResponse(5.15, 0.125),
                    "EUR" to ApiRatesResultsItemResponse(6.15, 0.225)
                )
            ),
            Date()
        )

        `when`(ratesRepository.getRemoteRates(anyString()))
            .thenReturn(flowOf(NetworkResultStatus.Success(mockApiRatesResponse)))

        viewModel.getRates()

        testDispatchers.standardTestDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            viewModel.ratesResult.value,
            UiResultStatus.Success(
                Pair(mockApiRatesResponse.toRatesUiModelList(), DataSource.NETWORK)
            )
        )
    }

    @Test
    fun `when getRates is called, should fetch LOCAL rates successfully`() = runTest {
        val mockRateDate = Date()
        val mockLocalRates = listOf(
            RateEntity("USD", 5.15, 0.125, mockRateDate),
            RateEntity("EUR", 6.15, 0.225, mockRateDate)
        )

        `when`(ratesRepository.getRemoteRates(anyString()))
            .thenReturn(flowOf(NetworkResultStatus.Error("Error Message")))

        `when`(ratesRepository.getLocalRates())
            .thenReturn(mockLocalRates)

        viewModel.getRates()

        testDispatchers.standardTestDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            viewModel.ratesResult.value,
            UiResultStatus.Success(
                Pair(mockLocalRates.map { it.toRateUiModel() }, DataSource.LOCAL)
            )
        )
    }

    @Test
    fun `when getRates is called, should fetch error`() = runTest {
        val mockErrorMsg = "Error Message"

        `when`(ratesRepository.getRemoteRates(anyString()))
            .thenReturn(flowOf(NetworkResultStatus.Error(mockErrorMsg)))

        `when`(ratesRepository.getLocalRates())
            .thenReturn(emptyList())

        viewModel.getRates()

        testDispatchers.standardTestDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            viewModel.ratesResult.value,
            UiResultStatus.Error(mockErrorMsg)
        )
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }
}