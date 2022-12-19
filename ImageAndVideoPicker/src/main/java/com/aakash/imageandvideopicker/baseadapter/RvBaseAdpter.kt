package com.aakash.imageandvideopicker.baseadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

abstract class RvBaseAdpter : RecyclerView.Adapter<RvBaseAdpter.MyHolder>() {
abstract val returnContext:Context
abstract val getLayoutId:Int
abstract fun getSize() :Int
abstract fun bindView(view: View,position:Int)


    override fun onBindViewHolder(holder: MyHolder, position: Int) {
         bindView(holder.view,position)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        return MyHolder(LayoutInflater.from(returnContext).inflate(getLayoutId,parent,false))
    }

    override fun getItemCount(): Int {
        return getSize()
    }




    class MyHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
      val view:View

        init {
          this.view=itemView
        }

    }
}