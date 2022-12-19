package com.aakash.imageandvideopicker.videofilter.adapter

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aakash.imageandvideopicker.R
import com.aakash.imageandvideopicker.baseadapter.RvBaseAdpter
import com.aakash.imageandvideopicker.videofilter.listener.RvFilterListenr
import com.aakash.imageandvideopicker.videofilter.model.FilterModelOption
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.filterlist_itemview.view.*

class VIdeoFilterOption(val context: Context,val list:List<FilterModelOption>,
val listenr: RvFilterListenr) : RvBaseAdpter() {


    override val returnContext: Context
        get() =context
    override val getLayoutId: Int
        get() = R.layout.filterlist_itemview

    override fun getSize(): Int {
        return list.size
    }
    override fun bindView(view: View, position: Int) {
        val filter_name=view.findViewById<TextView>(R.id.filter_name)
        val  filter_image=view.findViewById<ImageView>(R.id.filter_image)

        filter_name.setText(list.get(position).name)
        Glide.with(context).load(Uri.parse(list.get(position).imgUri))
            .error(R.drawable.filterimage)
            .into(filter_image)

         view.setOnClickListener {
           listenr.onClickFilter(position)
        }
    }


}