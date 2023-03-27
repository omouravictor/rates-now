package com.omouravictor.ratesbr.data.network.hgbrasil.stock

import com.google.gson.annotations.SerializedName
import com.omouravictor.ratesbr.data.local.entity.StockEntity
import java.util.*

data class NetworkStocksResponse(
    @SerializedName("by")
    val sourceBy: String,

    @SerializedName("valid_key")
    val sourceValidKey: Boolean,

    @SerializedName("results")
    val sourceResultStocks: NetworkStocksResultsResponse,

    @SerializedName("execution_time")
    val sourceExecutionTime: Double,

    @SerializedName("from_cache")
    val from_cache: Boolean
)

fun NetworkStocksResponse.toListStockEntity(): List<StockEntity> {
    val list: MutableList<StockEntity> = mutableListOf()
    val date = Date()

    sourceResultStocks.resultsStocks.forEach {
        list.add(
            StockEntity(
                it.key,
                it.value.requestStockName,
                it.value.requestStockLocation,
                it.value.requestStockPoints,
                it.value.requestStockVariation,
                date
            )
        )
    }

    return list
}