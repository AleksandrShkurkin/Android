package com.example.lr6

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter (private val context: Context?, private val items: List<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
        val nameTextView: TextView = view.findViewById(R.id.item_name)
        val priceTextView: TextView = view.findViewById(R.id.item_price)
        val button: Button = view.findViewById(R.id.button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Item = items[position]
        holder.nameTextView.text = item.name
        holder.priceTextView.text = item.price.toString()
        holder.checkBox.isChecked = false

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.selected = isChecked
        }

        holder.button.setOnClickListener {
            val message = if(item.selected)
            {
                "${item.name} is selected"
            }
            else
            {
                "${item.name} is NOT selected"
            }
            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int
    {
        println(items.size)
        return items.size
    }
}