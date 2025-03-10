package com.extraordinary.weaves.tendency

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.player.PlayerView


class FavAdapter(
    private var musicArrayList: ArrayList<Music>, private var ctx : baseActivity
) :
    RecyclerView.Adapter<FavAdapter.AreaGeneViewH>() {

    var string = arrayListOf(R.mipmap.recent_one,R.mipmap.recent_tw,R.mipmap.recent_th,
        R.mipmap.recent_fo, R.mipmap.recent_fv)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaGeneViewH {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_fav_list, parent, false)
        return AreaGeneViewH(itemView)
    }

    override fun getItemCount(): Int = musicArrayList.size

    class AreaGeneViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicName: TextView = itemView.findViewById(R.id.music_name)
        val artlistName: TextView = itemView.findViewById(R.id.artist)
        val collect: ImageView = itemView.findViewById(R.id.collect_iv)
        val back: RelativeLayout = itemView.findViewById(R.id.iv_one)

    }
    override fun onBindViewHolder(holder: AreaGeneViewH, position: Int) {
        val createBean = musicArrayList[position]
        holder.musicName.text = createBean.musicName
        holder.artlistName.text = createBean.artist
        if (createBean.favorite == 1){
            holder.collect.setImageResource(R.mipmap.home_fav_se)
        }else{
            holder.collect.setImageResource(R.mipmap.home_fav_un)
        }
        if (position < string.size){
            holder.back.setBackgroundResource(string[position])
        }
        holder.itemView.setOnClickListener {
            try {
                GlobalMusApp.instance?.mediaManager?.apply {
                    refreshPlaylist(musicArrayList)
                    playById(createBean.songId)
                }
                ctx?.startInterActivity(Intent(ctx, PlayerView::class.java), playViewsInter(),false)
            }catch (e : Exception){
                ctx?.startInterActivity(Intent(ctx, PlayerView::class.java), playViewsInter(),false)
                e.printStackTrace()
            }

        }
    }

}