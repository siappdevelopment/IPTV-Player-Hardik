package com.iptvplayer.xtreamiptv.myiptvpro.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.dao.ChannelDao
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.dao.FavoriteDao
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.dao.PlaylistDao
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.dao.RecentDao
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.ChannelEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.FavoriteEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.PlaylistEntity
import com.iptvplayer.xtreamiptv.myiptvpro.data.local.entity.RecentEntity

@Database(
    entities = [
        PlaylistEntity::class,
        ChannelEntity::class,
        FavoriteEntity::class,
        RecentEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun channelDao(): ChannelDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao

    companion object {
        private const val DB_NAME = "iptv.db"

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME).build()
    }
}
