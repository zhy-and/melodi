package com.extraordinary.weaves.tendency.tracks

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.RecyclerView
import com.extraordinary.weaves.tendency.bean.GeDanData
import com.extraordinary.weaves.tendency.detail.AppAlbumDetail
import com.extraordinary.weaves.tendency.detail.AppArtDetail
import com.extraordinary.weaves.tendency.R
import com.extraordinary.weaves.tendency.application.GlobalMusApp
import com.extraordinary.weaves.tendency.baseActivity
import com.extraordinary.weaves.tendency.bean.Music
import com.extraordinary.weaves.tendency.player.PlayerView
import com.extraordinary.weaves.tendency.dialog.showComDialog
import com.extraordinary.weaves.tendency.global.DataKeeper.createGeDanData
import com.extraordinary.weaves.tendency.global.DataKeeper.loadGeDanData
import dora.db.builder.QueryBuilder
import dora.db.builder.WhereBuilder
import dora.db.dao.DaoFactory
import com.blankj.utilcode.util.LogUtils
import com.extraordinary.interstings.studios.utils.albumDetInter
import com.extraordinary.interstings.studios.utils.artistDetInter
import com.extraordinary.interstings.studios.utils.playViewsInter
import dora.util.ToastUtils

//5 gedan 4 playlist 3 search 2 normal
class TrackListAdapter(
    private var musicArrayList: ArrayList<Music>,
    private var type: Int,
    private var base: baseActivity, private  val callback : RefreshPlayView? = null,private  val geDanId : Long = -1L
) :
    RecyclerView.Adapter<TrackListAdapter.AreaGeneViewH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaGeneViewH {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_track_list, parent, false)
        return AreaGeneViewH(itemView)
    }

    override fun getItemCount(): Int = musicArrayList.size

    class AreaGeneViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicName: TextView = itemView.findViewById(R.id.music_name)
        val menu: ImageView = itemView.findViewById(R.id.menu)

    }
    override fun onBindViewHolder(holder: AreaGeneViewH, position: Int) {
        try {
            val createBean = musicArrayList[position]
            holder.musicName.text = createBean.musicName
            if (type == 3){
                holder.menu.setImageResource(R.mipmap.item_play)
            }else{
                holder.menu.setImageResource(R.mipmap.item_menu)
            }
            holder.menu.setOnClickListener {
                if (type == 3){
                    try {
                        GlobalMusApp.instance?.mediaManager?.apply {
                            playById(createBean.songId)
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                    base?.startInterActivity(Intent(base, PlayerView::class.java), playViewsInter())
//                base?.finish()
                    return@setOnClickListener
                }
                if (base.isDestroyed || base.isFinishing){
                    return@setOnClickListener
                }
                try {
                    showComDialog(base, R.layout.item_popup_feed).apply {
                        if (type != 4){
                            findViewById<View>(R.id.play_delete)?.visibility = View.GONE
                        }
                        if (type == 5 && geDanId != -1L){
                            findViewById<View>(R.id.play_delete)?.visibility = View.VISIBLE
                            findViewById<TextView>(R.id.pop_txt)?.text = "Remove from this playlist"
                        }
                        findViewById<View>(R.id.play_art)?.setOnClickListener {
                            disDialog(this,base)
                            musicDao.select(
                                QueryBuilder.create().where(
                                    WhereBuilder.create().addWhereEqualTo("album_id",createBean.albumId)
                                )
                            ).also {
                                if (it.isNotEmpty()){
                                    base?.startInterActivity(Intent(base, AppArtDetail::class.java).apply {
                                        putExtra("artist", it[0].artist)
                                    }, artistDetInter())
//                            base?.finish()
                                }
                            }
                        }
                        if (base != null && !base.isFinishing && !base.isDestroyed){
                            if (base.localClassName.contains("AppArtDetail")){
                                findViewById<View>(R.id.play_art)?.visibility = View.GONE
                            }
                            if (base.localClassName.contains("AppAlbumDetail")){
                                findViewById<View>(R.id.play_album)?.visibility = View.GONE
                            }
                        }
                        findViewById<View>(R.id.play_album)?.setOnClickListener {
                            disDialog(this,base)
                            musicDao.select(
                                QueryBuilder.create().where(
                                    WhereBuilder.create().addWhereEqualTo("artist",createBean.artist)
                                )
                            ).also {
                                if (it.isNotEmpty()){
                                    for (i in it){
                                        LogUtils.e("name -----"+i.musicName)
                                    }
                                    base?.startInterActivity(Intent(base, AppAlbumDetail::class.java).apply {
                                        putExtra("album_id", it[0].albumId)
                                    }, albumDetInter())
                                    base?.finish()
                                }

                            }
                        }
                        findViewById<View>(R.id.play_now)?.setOnClickListener {
                            disDialog(this,base)
                            try {
                                GlobalMusApp.instance?.mediaManager?.also {
                                    if (createBean != null){
                                        it.refreshPlaylist(musicArrayList)
                                        it.playById(createBean.songId)
                                    }
                                }
                            }catch (e : Exception){
                                e.printStackTrace()
                            }

                            base?.startInterActivity(Intent(base, PlayerView::class.java), playViewsInter())
                            base?.finish()
//                 base?.finish()
                        }
                        findViewById<View>(R.id.play_fav)?.setOnClickListener {
                            disDialog(this,base)
                            saveFavorite(musicArrayList[position],1)
                            ToastUtils.showShort("Collect successful!")
                        }

                        findViewById<View>(R.id.play_delete)?.setOnClickListener {
                            try {
                                disDialog(this,base)
                                ToastUtils.showShort("Remove successful!")
                                musicArrayList.remove(createBean)
                                notifyDataSetChanged()
                                if(type == 5){
                                    if (musicArrayList.isEmpty()){
                                        callback?.refresh()
                                    }
                                    var oldGeDanListData = loadGeDanData(GeDanData())
                                    for (index in oldGeDanListData.indices){
                                        if (oldGeDanListData[index].geDanId == geDanId){
                                            oldGeDanListData[index].musicList = musicArrayList
                                            break
                                        }
                                    }
                                    createGeDanData(oldGeDanListData)
                                    return@setOnClickListener
                                }

                            }catch (e : Exception){
                                e.printStackTrace()
                            }

//                    GlobalMusApp.instance?.mediaManager?.apply {
//                        refreshPlaylist(musicArrayList)
//                        if (curMusic?.songId == createBean.songId && (position) < musicArrayList.size){
//                            setCurMusic(musicArrayList[position])
//                            playById(musicArrayList[position].songId)
//                            callback?.refresh()
//                        }
//
//                    }
                        }
                    }

                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            holder.itemView.setOnClickListener {
                try {
                    GlobalMusApp.instance?.mediaManager?.apply {
                        refreshPlaylist(musicArrayList)
                        playById(createBean.songId)
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                }

                base?.startInterActivity(Intent(base, PlayerView::class.java), playViewsInter())
                base?.finish()
//            base?.finish()

            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    interface RefreshPlayView{
        fun refresh()
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
    private val musicDao = DaoFactory.getDao(Music::class.java)
    private fun saveFavorite(music: Music, favorite: Int) {
        music.favorite = favorite
        musicDao.update(WhereBuilder.create().addWhereEqualTo("_id", music.id), music)
    }

}