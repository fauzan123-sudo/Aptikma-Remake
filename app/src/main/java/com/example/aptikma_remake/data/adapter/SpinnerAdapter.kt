package com.example.aptikma_remake.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.model.SpinnerList

class SpinnerAdapter(context: Context, val list: List<SpinnerList>) :
    ArrayAdapter<SpinnerList>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner, parent, false)

        val item = list[position]
        val textView = view.findViewById<TextView>(R.id.permissions)

        if (position == 0) {
            textView.text = item.nama
            textView.setTextColor(ContextCompat.getColor(context, R.color._grey)) // Set grey color
        } else {
            textView.text = item.nama
            textView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            ) // Set default color
        }

        return view
    }

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val binding: ItemSpinnerBinding = if (convertView == null) {
//            val inflater = LayoutInflater.from(context)
//            ItemSpinnerBinding.inflate(inflater, parent, false)
//        } else {
//            ItemSpinnerBinding.bind(convertView)
//        }
//        val listItem = getItem(position)
//        binding.permissions.text = listItem?.nama
//        if (position == 0) {
//            binding.permissions.setTextColor(Color.GRAY)
//        } else {
//            binding.permissions.setTextColor(Color.RED)
//        }
//        return binding.root
//    }
//
//    override fun isEnabled(position: Int): Boolean {
//        return position != 0
//    }
//
//    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return getView(position, convertView, parent)
//    }
}




