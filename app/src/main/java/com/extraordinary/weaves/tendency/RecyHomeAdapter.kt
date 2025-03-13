package com.extraordinary.weaves.tendency

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.weaves.tendency.dialog.event_one_random_home
import com.extraordinary.weaves.tendency.dialog.event_one_styles_home
import com.extraordinary.weaves.tendency.dialog.logMusicEvents
import com.extraordinary.weaves.tendency.player.PlayerView


class RecyHomeAdapter(
    private var musicArrayList: ArrayList<Int>,private  var ctx: baseActivity
) :
    RecyclerView.Adapter<RecyHomeAdapter.AreaGeneViewH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaGeneViewH {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return AreaGeneViewH(itemView)
    }

    override fun getItemCount(): Int = musicArrayList.size

    class AreaGeneViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.img)

    }
    override fun onBindViewHolder(holder: AreaGeneViewH, position: Int) {
            Glide.with(ctx).load(musicArrayList[position]).into(holder.logo)
        holder.itemView.setOnClickListener {
            ctx?.startInterActivity(Intent(ctx, PlayerView::class.java).apply {
                putExtra("play",true)
            }, playViewsInter(),false)
            logMusicEvents(event_one_random_home)
        }
    }


}