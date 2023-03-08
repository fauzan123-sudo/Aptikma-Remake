package com.example.aptikma_remake.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aptikma_remake.data.model.DetailPotongan
import com.example.aptikma_remake.databinding.ItemPotonganBinding
import java.text.NumberFormat
import java.util.*

class AdapterPotongan: RecyclerView.Adapter<AdapterPotongan.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPotonganBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<DetailPotongan>() {
        override fun areItemsTheSame(
            oldItem: DetailPotongan,
            newItem: DetailPotongan
        ): Boolean {
            return oldItem.nama_potongan == newItem.nama_potongan
        }

        override fun areContentsTheSame(
            oldItem: DetailPotongan,
            newItem: DetailPotongan
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemPotonganBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPosition = differ.currentList[position]
        with(holder) {
            binding.namaPotongan.text = myPosition.nama_potongan.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            val value = myPosition.nilai_potongan.toInt()
            val myNumber = NumberFormat.getNumberInstance(Locale.US)
                .format(value)
                .replace(",", ".")
            binding.nominalPotongan.text = "Rp. $myNumber"
        }
    }

    override fun getItemCount() = differ.currentList.size

}