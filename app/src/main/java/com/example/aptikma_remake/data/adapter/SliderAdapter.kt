package com.example.aptikma_remake.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.model.BeritaAcaraResponseItem
import com.example.aptikma_remake.util.Constans.IMAGE_URL
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class SliderAdapter :
    SliderViewAdapter<SliderAdapter.VH>() {
//    private var mSliderItems = ArrayList<BeritaAcaraResponseItem>()
    private var mSliderItems = mutableListOf<BeritaAcaraResponseItem>()


    fun renewItems(sliderItems: ArrayList<BeritaAcaraResponseItem>) {
        this.mSliderItems = sliderItems.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_item, null)
        return VH(inflate)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        Picasso.get().load(IMAGE_URL + mSliderItems[position].image).fit()
            .into(viewHolder.imageView)
//        viewHolder.title.text = mSliderItems[position].title
//        viewHolder.title.textSize = 18F
//        viewHolder.title.setTextColor(Color.BLACK)
//        Glide.with(viewHolder.itemView).load(mSliderItems[position].image).fitCenter()
//                .into(viewHolder.imageView)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class VH(itemView: View) : ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
//        var title: TextView = itemView.findViewById(R.id.tv_auto_image_slider)
    }
}

//class SliderAdapter :
//    SliderViewAdapter<SliderAdapter.SliderViewHolder>() {
//
//    var list = emptyList<String>()
//
//    override fun getCount() = list.size
//
//    override fun onCreateViewHolder(parent: ViewGroup?): SliderViewHolder {
//        val inflate: View =
//            LayoutInflater.from(parent!!.context).inflate(R.layout.image_slider_item, null)
//        return SliderViewHolder(inflate)
//    }
//
//    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
//        if (viewHolder != null) {
////            viewHolder.title.text = list[position]
////            viewHolder.title.setText(sliderItem.getDescription())
////            viewHolder.textViewDescription.setTextSize(16)
////            viewHolder.textViewDescription.setTextColor(Color.BLACK)
//            Glide.with(viewHolder.itemView).load(list[position]).fitCenter()
//                .into(viewHolder.imageView)
//        }
//    }
//
//    class SliderViewHolder(itemView: View?) : ViewHolder(itemView) {
//        var imageView: ImageView = itemView!!.findViewById(R.id.iv_auto_image_slider)
//        var title :TextView      = itemView!!.findViewById(R.id.tv_auto_image_slider)
//    }
//
//    fun renewItems(sliderItems: ArrayList<String>) {
//        mSliderItems = sliderItems
//        notifyDataSetChanged()
//    }
//
//}

//class SliderAdapter : RecyclerView.Adapter<SliderAdapter.ViewHolder>() {
//
//    class ViewHolder(val binding: ImageSliderItemBinding) : RecyclerView.ViewHolder(binding.root)
//
//    private val differCallback = object : DiffUtil.ItemCallback<BeritaAcaraResponseItem>() {
//        override fun areItemsTheSame(
//            oldItem: BeritaAcaraResponseItem,
//            newItem: BeritaAcaraResponseItem
//        ): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(
//            oldItem: BeritaAcaraResponseItem,
//            newItem: BeritaAcaraResponseItem
//        ): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    val differ = AsyncListDiffer(this, differCallback)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
////        LayoutInflater.from(parent.context).inflate(R.layout.image_slider_item, parent, false)
//        val binding = ImageSliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val dataPosition = differ.currentList[position]
//        with(holder) {
//            with(dataPosition) {
//                binding.tvAutoImageSlider.text = this.title
//
////                viewHolder.textViewDescription.setText(sliderItem.getDescription())
////                viewHolder.textViewDescription.setTextSize(16)
////                viewHolder.textViewDescription.setTextColor(Color.BLACK)
//                Glide.with(holder.itemView.context)
//                    .load(this.image)
//                    .fitCenter()
//                    .into(binding.ivAutoImageSlider)
//
//            }
//        }
//    }
//
//    override fun getItemCount() = differ.currentList.size
//
//}