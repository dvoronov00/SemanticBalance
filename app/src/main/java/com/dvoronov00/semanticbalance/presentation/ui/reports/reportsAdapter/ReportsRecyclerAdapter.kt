package com.dvoronov00.semanticbalance.presentation.ui.reports.reportsAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.domain.model.PaymentReport
import com.dvoronov00.semanticbalance.domain.model.Report
import com.dvoronov00.semanticbalance.domain.model.ServiceReport

class ReportsRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = arrayListOf<Report>()

    companion object {
        private const val ITEM_SERVICE = 0
        private const val ITEM_PAYMENT = 1
    }

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(report: ServiceReport){
            val reportNameTV = itemView.findViewById<TextView>(R.id.reportName)
            val reportDateTV = itemView.findViewById<TextView>(R.id.reportDate)
            val reportPriceTV = itemView.findViewById<TextView>(R.id.reportPrice)

            reportNameTV.text = report.name
            reportDateTV.text = report.date
            reportPriceTV.text = itemView.context.getString(R.string.moneyWithdraw, report.price)
        }
    }


    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(report: PaymentReport){
            val reportNameTV = itemView.findViewById<TextView>(R.id.reportName)
            val reportDateTV = itemView.findViewById<TextView>(R.id.reportDate)
            val reportPriceTV = itemView.findViewById<TextView>(R.id.reportPrice)

            reportNameTV.text = if(report.comment.isNullOrBlank()){
                report.paymentMethod
            }else{
                report.comment
            }
            reportDateTV.text = report.date
            reportPriceTV.text = itemView.context.getString(R.string.moneyReplenishment, report.paymentAmount)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_SERVICE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_service, parent, false)
                ServiceViewHolder(view)
            }
            ITEM_PAYMENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_payment, parent, false)
                PaymentViewHolder(view)
            }
            else -> throw IllegalStateException("unknown view type: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(items: List<Report>){
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearList(){
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is ServiceReport -> {
                ITEM_SERVICE
            }
            is PaymentReport -> {
                ITEM_PAYMENT
            }
            else -> throw IllegalStateException("Unknown view type by position: $position")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            ITEM_SERVICE -> {
                (holder as ServiceViewHolder).bind(items[position] as ServiceReport)
            }
            ITEM_PAYMENT -> {
                (holder as PaymentViewHolder).bind(items[position] as PaymentReport)
            }
            else -> throw IllegalStateException("unknown view type by position: $position")
        }
    }
}