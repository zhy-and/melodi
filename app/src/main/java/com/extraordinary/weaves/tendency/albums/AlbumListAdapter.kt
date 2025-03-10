package com.extraordinary.weaves.tendency.albums

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.RecyclerView
import com.extraordinary.interstings.studios.utils.albumDetInter
import com.extraordinary.interstings.studios.utils.artistDetInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Album
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.detail.AppAlbumDetail
import com.extraordinary.weaves.tendency.detail.AppArtDetail
import com.extraordinary.weaves.tendency.dialog.showComDialog
import com.extraordinary.weaves.tendency.player.PlayerView
import dora.db.builder.QueryBuilder
import dora.db.builder.WhereBuilder
import dora.db.dao.DaoFactory
import dora.util.ToastUtils


class AlbumListAdapter(
    private var musicArrayList: ArrayList<Album>, private  var ctx: baseActivity, private var defaultType : Int = 0
) :
    RecyclerView.Adapter<AlbumListAdapter.AreaGeneViewH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaGeneViewH {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_album_list, parent, false)
        return AreaGeneViewH(itemView)
    }

    private val musicDao = DaoFactory.getDao(Music::class.java)

    override fun getItemCount(): Int = musicArrayList.size

    class AreaGeneViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicName: TextView = itemView.findViewById(R.id.music_name)
        val artlistName: TextView = itemView.findViewById(R.id.artist)
        val menus: ImageView = itemView.findViewById(R.id.menu)

    }
    override fun onBindViewHolder(holder: AreaGeneViewH, position: Int) {
        val createBean = musicArrayList[position]
        holder.musicName.text = createBean.album_name
        val art_det = musicDao.select(
                QueryBuilder.create().where(
                    WhereBuilder.create().addWhereEqualTo("_id", createBean.id)
                )
            )
        if (art_det.isNotEmpty()){
            holder.artlistName.text = art_det[0].artist
        }
        if (defaultType == 3){
            holder.menus.setImageResource(R.mipmap.item_more)
        }else{
            holder.menus.setImageResource(R.mipmap.item_menu)
        }
        holder.menus.setOnClickListener {
            if (defaultType == 3){
                goAlbumDet(createBean)
                return@setOnClickListener
            }
            if (checkFinish()) return@setOnClickListener
            try {
                showComDialog(ctx, R.layout.item_popup_feed).apply {
                    findViewById<View>(R.id.play_album)?.visibility = View.GONE
                    findViewById<View>(R.id.play_delete)?.visibility = View.GONE
                    findViewById<View>(R.id.play_art)?.setOnClickListener {
                        disDialog(this,ctx)
                        musicDao.select(
                            QueryBuilder.create().where(
                                WhereBuilder.create().addWhereEqualTo("album_id",createBean.album_id)
                            )
                        ).also {
                            if (it.isNotEmpty()){
                                ctx?.startInterActivity(Intent(ctx, AppArtDetail::class.java).apply {
                                    putExtra("artist", it[0].artist)
                                }, artistDetInter())
//                            ctx?.finish()
                            }

                        }
                    }
                    findViewById<View>(R.id.play_now)?.setOnClickListener {
                        disDialog(this, ctx)

                        musicDao.select(
                            QueryBuilder.create().where(
                                WhereBuilder.create().addWhereEqualTo("album_id",createBean.album_id)
                            )
                        ).apply {
                            try {
                                GlobalMusApp.instance?.mediaManager?.also {
                                    if (isNotEmpty()){
                                        it.refreshPlaylist(this as MutableList)
                                        it.playById(this[0].songId)
                                    }
                                }
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                        ctx?.startInterActivity(Intent(ctx, PlayerView::class.java), playViewsInter())

//                    ctx?.finish()
                    }
                    findViewById<View>(R.id.play_fav)?.setOnClickListener {
                        disDialog(this, ctx)

                        musicDao.select(
                            QueryBuilder.create().where(
                                WhereBuilder.create().addWhereEqualTo("album_id",createBean.album_id)
                            )
                        ).apply {
                            for (i in this){
                                saveFavorite(i,1)
                            }
                        }
                        ToastUtils.showShort("Collect successful！")
                    }
                }
            }catch (e : Exception){
                e.printStackTrace()
            }

        }


        holder.itemView.setOnClickListener {
            holder.itemView.context.apply {
                goAlbumDet(createBean)
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

    private fun checkFinish(): Boolean {
        if (ctx == null || ctx.isFinishing || ctx.isDestroyed) {
            return true
        }
        return false
    }

    private fun goAlbumDet(createBean: Album) {
        ctx?.startInterActivity(Intent(ctx, AppAlbumDetail::class.java).apply {
            putExtra("album_id", createBean.album_id)
//            putExtra("album_name", createBean.album_name)
        }, albumDetInter())
//        ctx?.finish()
    }

    private fun saveFavorite(music: Music, favorite: Int) {
        music.favorite = favorite
        musicDao.update(WhereBuilder.create().addWhereEqualTo("_id", music.id), music)
    }

}