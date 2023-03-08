package com.example.aptikma_remake.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aptikma_remake.data.model.DetailTunjangan
import com.example.aptikma_remake.databinding.ItemTunjanganBinding
import java.text.NumberFormat
import java.util.*

class AdapterTunjangan : RecyclerView.Adapter<AdapterTunjangan.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTunjanganBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<DetailTunjangan>() {
        override fun areItemsTheSame(
            oldItem: DetailTunjangan,
            newItem: DetailTunjangan
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DetailTunjangan,
            newItem: DetailTunjangan
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(

        ItemTunjanganBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPosition = differ.currentList[position]
        with(holder) {
            binding.namaTunjangan.text = myPosition.nama_tunjangan.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            val value = myPosition.nominal.toInt()
            val myNumber = NumberFormat.getNumberInstance(Locale.US)
                .format(value)
                .replace(",", ".")
            binding.nominalTunjangan.text = "Rp. $myNumber"
        }
    }

    override fun getItemCount() = differ.currentList.size

}