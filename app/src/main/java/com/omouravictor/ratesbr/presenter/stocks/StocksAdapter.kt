package com.omouravictor.ratesbr.presenter.stocks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.ratesbr.R
import com.omouravictor.ratesbr.databinding.StockItemListBinding
import com.omouravictor.ratesbr.presenter.stocks.model.StockUiModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class StocksAdapter(
    private val list: List<StockUiModel>
) : RecyclerView.Adapter<StocksAdapter.StockViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding =
            StockItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class StockViewHolder(
        private val stockItem: StockItemListBinding
    ) : RecyclerView.ViewHolder(stockItem.root) {
        fun bind(stock: StockUiModel) {
            val locale = Locale("pt", "BR")
            val dateFormatter = SimpleDateFormat("dd/MM/yy", locale)
            val timeFormatter = SimpleDateFormat("HH:mm", locale)
            val decimalFormat = DecimalFormat("#0.00#", DecimalFormatSymbols.getInstance(locale))

            "${stock.stockTerm} / ${stock.stockLocation}".also {
                stockItem.textViewStockName.text = it
            }

            "${dateFormatter.format(stock.date)}\n${timeFormatter.format(stock.date)}".also {
                stockItem.tvDateTime.text = it
            }

            "${decimalFormat.format(stock.stockVariation)}%".also {
                stockItem.textViewStockVariation.text = it
            }

            if (stock.stockVariation >= 0.0) {
                "+${decimalFormat.format(stock.stockVariation)}%".also {
                    stockItem.textViewStockVariation.text = it
                }
                stockItem.imageViewStockVariation.setImageResource(R.drawable.ic_arrow_up)
                stockItem.textViewStockVariation.setTextColor(Color.GREEN)
            } else {
                stockItem.imageViewStockVariation.setImageResource(R.drawable.ic_arrow_down)
            }
        }
    }
}