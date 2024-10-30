package com.extraordinary.weaves.tendency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.extraordinary.weaves.tendency.bean.Music
import dora.db.dao.DaoFactory

// 4 playlist 3 search 2 normal
class AddListAdapter(
    private var musicArrayList: ArrayList<Music>,
    private var type: Int,
    private var base: baseActivity, private  val callback : RefreshPlayView? = null
) :
    RecyclerView.Adapter<AddListAdapter.AreaGeneViewH>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AreaGeneViewH {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_play_list, parent, false)
        return AreaGeneViewH(itemView)
    }
//    fun upCreateData(createBeansList: ArrayList<Music>?) {
//        if (createBeansList != null) {
//            musicArrayList.clear()
//            musicArrayList.addAll(createBeansList)
//            notifyDataSetChanged()
//        }
//    }

        fun upCreateData(isSelect: Boolean) {
            if (musicArrayList.isNotEmpty()){
                for (i in musicArrayList){
                    i.isDeleteStatus = isSelect
                }
                notifyDataSetChanged()
            }
        }
    override fun getItemCount(): Int = musicArrayList.size

    class AreaGeneViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicName: TextView = itemView.findViewById(R.id.text)
        val menu: ImageView = itemView.findViewById(R.id.delete_iv)

    }
    override fun onBindViewHolder(holder: AreaGeneViewH, position: Int) {
        val createBean = musicArrayList[position]
        if (createBean.isDeleteStatus){
            holder.menu.setImageResource(R.mipmap.ivg_select)
        }else{
            holder.menu.setImageResource(R.mipmap.ivg_un_select)
        }
        holder.musicName.text = createBean.musicName

        holder.menu.setOnClickListener {
            selectItem(createBean, holder)
        }
        holder.itemView.setOnClickListener {
            selectItem(createBean, holder)
        }
    }

    private fun selectItem(
        createBean: Music,
        holder: AreaGeneViewH
    ) {
        createBean.isDeleteStatus = !createBean.isDeleteStatus
        if (createBean.isDeleteStatus) {
            holder.menu.setImageResource(R.mipmap.ivg_select)
        } else {
            holder.menu.setImageResource(R.mipmap.ivg_un_select)
        }
    }

    fun getCreateData(): ArrayList<Music> {
        return musicArrayList
    }

    interface RefreshPlayView{
        fun refresh()
    }

    private val musicDao = DaoFactory.getDao(Music::class.java)


}