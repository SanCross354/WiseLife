package com.example.wise_life

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wise_life.MenuAdapter.MenuViewHolder

class MenuAdapter(private val menus: ArrayList<MenuHelperClass>,
                  private val menuClickListener: MenuClickListener) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diet_menu_cards, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuHelperClass = menus[position]

        // Log statements for debugging
        Log.d("MenuAdapter", "Name: " + menuHelperClass.getName())

        // Set text only if the TextView is not null
        if (holder.name != null) {
            holder.name!!.text = menuHelperClass.getName()
        }

        // Set text only if the TextView is not null
        if (holder.calorie != null) {
            holder.calorie!!.text = menuHelperClass.getCalorie()
        }

        // Set text only if the TextView is not null
        if (holder.carb != null) {
            holder.carb!!.text = menuHelperClass.getCarb()
        }

        // Set text only if the TextView is not null
        if (holder.protein != null) {
            holder.protein!!.text = menuHelperClass.getProtein()
        }

        // Set text only if the TextView is not null
        if (holder.fat != null) {
            holder.fat!!.text = menuHelperClass.getFat()
        }
        if (holder.image != null) {
            holder.image!!.setImageResource(menuHelperClass.getImage())
        }

        // Set click listener for the add button
        holder.itemView.findViewById<View>(R.id.btn_addMenuBreakfast).setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedItem = menus[position]
                menuClickListener.onMenuAddClicked(
                    clickedItem
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = itemView.findViewById(R.id.imageMenuOptional)
        var name: TextView? = itemView.findViewById(R.id.menuName)
        var calorie: TextView? = itemView.findViewById(R.id.calorie)
        var carb: TextView? = itemView.findViewById(R.id.carbs)
        var protein: TextView? = itemView.findViewById(R.id.protein)
        var fat: TextView? = itemView.findViewById(R.id.fat)
    }

    interface MenuClickListener {
        fun onMenuAddClicked(menu: MenuHelperClass)
    }

}