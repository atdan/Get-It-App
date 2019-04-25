package com.example.root.getit.view_holder

import android.view.ContextMenu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.root.getit.R
import com.example.root.getit.common.Common
import com.example.root.getit.interfaces.ItemClickListener

class MyAdsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener, View.OnClickListener {

    var txtMenuName: TextView
    var price: TextView
    var location: TextView
    var imageView: ImageView
    var deleteImage: ImageView? = null

    private  var itemClickListener: ItemClickListener? = null
    

    init {

        txtMenuName = itemView.findViewById(R.id.menu_title)
        imageView = itemView.findViewById(R.id.menu_image)
        price = itemView.findViewById(R.id.menu_price)
        location = itemView.findViewById(R.id.menu_location)



        itemView.setOnCreateContextMenuListener(this)
        itemView.setOnClickListener(this)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onClick(view: View) {
        itemClickListener!!.onClick(view, adapterPosition, false)
    }

    override fun onCreateContextMenu(contextMenu: ContextMenu, view: View, contextMenuInfo: ContextMenu.ContextMenuInfo) {

        contextMenu.setHeaderTitle("Select the action")
        contextMenu.add(0, 0, adapterPosition, "")
        contextMenu.add(0, 1, adapterPosition, Common.DELETE)
    }
}
