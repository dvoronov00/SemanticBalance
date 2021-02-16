package com.dvoronov00.semanticbalance.presentation.ui.account.servicesAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.domain.model.Service

class ServicesRecyclerAdapter : RecyclerView.Adapter<ServicesRecyclerAdapter.ViewHolder>() {

    private val items = arrayListOf<Service>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(service: Service) {
            val nameTV = itemView.findViewById<TextView>(R.id.textViewServiceName)
            val priceTV = itemView.findViewById<TextView>(R.id.textViewServicePrice)

            nameTV.text = service.name
            priceTV.text =
                itemView.context.getString(R.string.moneyInRoublesPerMonth, service.price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(items: List<Service>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearList() {
        this.items.clear()
        notifyDataSetChanged()
    }
}