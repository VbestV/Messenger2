package com.example.messenger.models

import android.annotation.SuppressLint
import com.example.messenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat

class ChatToItem(val text: String, val user: User?, val time: Long): Item<ViewHolder>(){
    @SuppressLint("SimpleDateFormat")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        val transfTime = SimpleDateFormat("dd MMM, hh:mm")
        val date_string = transfTime.format(time)
        viewHolder.itemView.timeTo.text = date_string
        val uri = user!!.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}