package com.dvoronov00.semanticbalance.presentation.ui.account.servicesAdapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.domain.model.News

class NewsRecyclerAdapter : RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>() {

    private val items = arrayListOf<News>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(news: News) {
            val titleTV = itemView.findViewById<TextView>(R.id.textViewNewsTitle)
            val dateTV = itemView.findViewById<TextView>(R.id.textViewNewsDate)
            val textTV = itemView.findViewById<TextView>(R.id.textViewNewsText)

            titleTV.text = news.title
            dateTV.text = news.date
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textTV.text = Html.fromHtml(news.textHtml, Html.FROM_HTML_MODE_COMPACT)
            } else {
                textTV.text = Html.fromHtml(news.textHtml)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(items: List<News>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearList() {
        this.items.clear()
        notifyDataSetChanged()
    }
}