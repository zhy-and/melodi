package com.extraordinary.weaves.tendency

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.extraordinary.interstings.studios.utils.geDanDetInter
import com.extraordinary.weaves.tendency.bean.GeDanData
import com.extraordinary.weaves.tendency.detail.AppGeDanDetail


class GeDanListAdapter(
    private var musicArrayList:ArrayList<GeDanData>, private  val ctx : baseActivity
) :
    RecyclerView.Adapter<GeDanListAdapter.AreaGeneViewH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaGeneViewH {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gedan_list, parent, false)
        return AreaGeneViewH(itemView)
    }
    fun upCreateData(createBeansList: ArrayList<GeDanData>?) {
        if (createBeansList != null) {
            musicArrayList.clear()
            musicArrayList = createBeansList
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int = musicArrayList.size

    class AreaGeneViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicName: TextView = itemView.findViewById(R.id.names)
        val view: View = itemView.findViewById(R.id.moon)
    }
    override fun onBindViewHolder(holder: AreaGeneViewH, position: Int) {
        when(position%4){
            0 -> {
                holder.view.setBackgroundResource(R.mipmap.play_list_home_ten)
            }
            1 -> {
                holder.view.setBackgroundResource(R.mipmap.play_list_home_eight)
            }
            2 -> {
                holder.view.setBackgroundResource(R.mipmap.play_list_home_eleven)
            }
            3 -> {
                holder.view.setBackgroundResource(R.mipmap.play_list_home_nine)
            }

        }
        val createBean = musicArrayList[position]
        holder.musicName.text = createBean.geDanName
        holder.itemView.setOnClickListener {
            ctx?.startInterActivity(Intent(ctx, AppGeDanDetail::class.java).apply {
                putExtra("det_type",1)
                putExtra("gedan_id",createBean.geDanId)
            }, geDanDetInter())
        }
    }

}