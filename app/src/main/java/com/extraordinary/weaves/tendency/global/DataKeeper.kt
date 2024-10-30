package com.extraordinary.weaves.tendency.global

import com.extraordinary.weaves.tendency.bean.GeDanData
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

object DataKeeper {

    fun loadGeDanData(bean: GeDanData): ArrayList<GeDanData> {
        val createMmkv = MMKV.defaultMMKV()
        val dataBean = ArrayList<GeDanData>()
        val size = createMmkv.getInt(gedan_name + "size", 0)
        for (i in 0 until size) {
            if (createMmkv.getString(gedan_name + i, null) != null) {
                try {
                    dataBean.add(
                        Gson().fromJson(
                            createMmkv.getString(gedan_name + i, null),
                            bean.javaClass
                        ) as GeDanData
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return dataBean
    }



    fun createGeDanData(danData: List<GeDanData?>?) {
        val genemkAreaMmkv = MMKV.defaultMMKV()
        if (danData.isNullOrEmpty()) {

            genemkAreaMmkv.putInt(gedan_name + "size", 0)
            val size = genemkAreaMmkv.getInt(gedan_name + "size", 0)
            for (i in 0 until size) {
                if (genemkAreaMmkv.getString(gedan_name + i, null) != null) {
                    genemkAreaMmkv.remove(gedan_name + i)
                }
            }
        } else {
            genemkAreaMmkv.putInt(gedan_name + "size", danData.size)
            for (i in danData.indices) {
                genemkAreaMmkv.remove(gedan_name + i)
                genemkAreaMmkv.remove(Gson().toJson(danData[i]))
                genemkAreaMmkv.putString(gedan_name + i, Gson().toJson(danData[i]))
            }
        }
    }
    const val name = "normal_name"
    const val gedan_name = "ge_dan_name"

}