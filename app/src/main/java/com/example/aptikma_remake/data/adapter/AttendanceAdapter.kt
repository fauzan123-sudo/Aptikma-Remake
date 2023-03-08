package com.example.aptikma_remake.data.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.model.AttendanceListItem
import com.example.aptikma_remake.data.model.AttendanceUser
import com.example.aptikma_remake.databinding.AttendanceItemBinding

class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AttendanceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<AttendanceListItem>() {
        override fun areItemsTheSame(
            oldItem: AttendanceListItem,
            newItem: AttendanceListItem
        ): Boolean {
            return oldItem.id_pegawai == newItem.id_pegawai
        }

        override fun areContentsTheSame(
            oldItem: AttendanceListItem,
            newItem: AttendanceListItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        AttendanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPosition = differ.currentList[position]
        with(holder) {
            binding.tanggal.text = myPosition.tanggal
            binding.jam.text = myPosition.jam

//            Custom badge

            when (myPosition.status) {
                "1" -> {
                    binding.badges.text = "Masuk"
                    binding.badges.setTextColor(Color.parseColor("#0FAF3C"))
                    binding.iconGirlBlue.setBackgroundResource(R.drawable.ic_masuk)
                }
                "2" -> {
                    binding.badges.text = "Sakit"
                    binding.badges.setTextColor(Color.parseColor("#3CC4FF"))
                    binding.iconGirlBlue.setBackgroundResource(R.drawable.ic_izin)
                }
                "3" -> {
                    binding.badges.text = "Izin"
                    binding.badges.setTextColor(Color.parseColor("#ffca2c"))
                    binding.iconGirlBlue.setBackgroundResource(R.drawable.ic_sakit)
                }
                "4" -> {
                    binding.badges.text = "Telat"
                    binding.badges.setTextColor(Color.parseColor("#FF0000"))
                    binding.iconGirlBlue.setBackgroundResource(R.drawable.ic_alpa)
                }
                "0" -> {
                    binding.badges.text = "Belum Ada Absen"
                    binding.badges.setTextColor(Color.parseColor("#ffca2c"))
                    binding.iconGirlBlue.setBackgroundResource(R.drawable.ic_gak_ada)
                }
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

}