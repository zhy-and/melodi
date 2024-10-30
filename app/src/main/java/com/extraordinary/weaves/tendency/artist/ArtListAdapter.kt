package com.extraordinary.weaves.tendency.artist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.RecyclerView
import com.extraordinary.weaves.tendency.detail.AppAlbumDetail
import com.extraordinary.weaves.tendency.detail.AppArtDetail
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Artist
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.dialog.showComDialog
import dora.db.builder.QueryBuilder
import dora.db.builder.WhereBuilder
import dora.db.dao.DaoFactory
import com.blankj.utilcode.util.LogUtils
import dora.util.ToastUtils


class ArtListAdapter(
    private var musicArrayList: ArrayList<Artist>, private var ctx : baseActivity, private var defaultType : Int = 0
) :
    RecyclerView.Adapter<ArtListAdapter.AreaGeneViewH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaGeneViewH {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_track_list, parent, false)
        return AreaGeneViewH(itemView)
    }

    private val musicDao = DaoFactory.getDao(Music::class.java)
    private val albumDao = DaoFactory.getDao(Album::class.java)

    override fun getItemCount(): Int = musicArrayList.size

    class AreaGeneViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicName: TextView = itemView.findViewById(R.id.music_name)
        val menus: ImageView = itemView.findViewById(R.id.menu)
        val ivLogo: ImageView = itemView.findViewById(R.id.img)

    }
    override fun onBindViewHolder(holder: AreaGeneViewH, position: Int) {
        val createBean = musicArrayList[position]
        holder.musicName.text = createBean.name
        if (defaultType == 3){
            holder.menus.setImageResource(R.mipmap.item_more)
            holder.ivLogo.setImageResource(R.mipmap.item_art_logo)
        }
        else if (defaultType == 5){
            holder.menus.setImageResource(R.mipmap.item_menu)
            holder.ivLogo.setImageResource(R.mipmap.item_art_logo)
        }
        else{
            holder.menus.setImageResource(R.mipmap.item_menu)
            holder.ivLogo.setImageResource(R.mipmap.item_logo)
        }
        holder.menus.setOnClickListener {
            if (defaultType == 3){
                goArtDet(createBean)
                return@setOnClickListener
            }
            if (ctx.isFinishing || ctx.isDestroyed){
                return@setOnClickListener
            }
           showComDialog(ctx, R.layout.item_popup_feed).apply {
                findViewById<View>(R.id.play_art)?.visibility = View.GONE
                findViewById<View>(R.id.play_delete)?.visibility = View.GONE
                findViewById<View>(R.id.play_album)?.setOnClickListener {
                    disDialog(this,ctx)
                    musicDao.select(
                        QueryBuilder.create().where(
                            WhereBuilder.create().addWhereEqualTo("artist",createBean.name)
                        )
                    ).also {
                        if (it.isNotEmpty()){
                            for (i in it){
                                LogUtils.e("name -----"+i.musicName)
                            }
                            ctx?.startActivity(Intent(ctx, AppAlbumDetail::class.java).apply {
                                putExtra("album_id", it[0].albumId)
                            })
//                            ctx?.finish()
                        }

                    }

                }
                findViewById<View>(R.id.play_now)?.setOnClickListener {
                    disDialog(this,ctx)

                    musicDao.select(
                        QueryBuilder.create().where(
                            WhereBuilder.create().addWhereEqualTo("artist",createBean.name)
                        )
                    ).apply {
                        GlobalMusApp.instance?.mediaManager?.also {
                            if (isNotEmpty()){
                                it.refreshPlaylist(this as MutableList)
                                it.playById(this[0].songId)
                            }
                        }
                    }
                    ctx?.startActivity(Intent(ctx, PlayerView::class.java))
//                    ctx?.finish()
                }
                findViewById<View>(R.id.play_fav)?.setOnClickListener {
                    disDialog(this,ctx)

                    musicDao.select(
                        QueryBuilder.create().where(
                            WhereBuilder.create().addWhereEqualTo("artist",createBean.name)
                        )
                    ).apply {
                        for (i in this){
                            saveFavorite(i,1)
                        }
                    }
                    ToastUtils.showShort("Collect successful!")

                }
            }
        }
        holder.itemView.setOnClickListener {
            holder.itemView.context.apply {
                goArtDet(createBean)

            }

        }
    }

    private fun disDialog(appCompatDialog: AppCompatDialog, ctx: baseActivity) {
        try {
            if (ctx == null || ctx.isDestroyed || ctx.isFinishing){
                return
            }
            appCompatDialog.dismiss()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun goArtDet(createBean: Artist) {
        ctx?.startActivity(Intent(ctx, AppArtDetail::class.java).apply {
            putExtra("artist", createBean.name)
        })
//        ctx?.finish()
    }

    private fun saveFavorite(music: Music, favorite: Int) {
        music.favorite = favorite
        musicDao.update(WhereBuilder.create().addWhereEqualTo("_id", music.id), music)
    }

}