package com.omouravictor.ratesbr.presenter.rates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omouravictor.ratesbr.databinding.ItemListRateBinding
import com.omouravictor.ratesbr.presenter.rates.model.RateUiModel
import com.omouravictor.ratesbr.util.BrazilianFormats.currencyFormat
import com.omouravictor.ratesbr.util.BrazilianFormats.dateFormat
import com.omouravictor.ratesbr.util.BrazilianFormats.timeFormat
import com.omouravictor.ratesbr.util.Functions.setVariationOnBind
import com.omouravictor.ratesbr.util.Numbers.getRoundedDouble

class RatesAdapter(
    private val list: List<RateUiModel>,
    private val callBack: (RateUiModel) -> Unit
) : RecyclerView.Adapter<RatesAdapter.RatesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val binding =
            ItemListRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatesViewHolder(binding, callBack)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class RatesViewHolder(
        private val binding: ItemListRateBinding,
        private val callBack: (RateUiModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rate: RateUiModel) {
            binding.textViewRateCurrencyTerm.text = rate.currencyTerm
            binding.textViewRateCurrencyName.text = rate.currencyName
            setVariationOnBind(
                rate.variation,
                binding.textViewRateVariation,
                binding.imageViewRateVariation
            )
            binding.textViewRateValue.text = currencyFormat.format(
                getRoundedDouble(rate.unitaryRate)
            )
            binding.textViewDate.text = dateFormat.format(rate.rateDate)
            binding.textViewTime.text = timeFormat.format(rate.rateDate)
            itemView.setOnClickListener {
                callBack(rate)
            }
        }
    }
}