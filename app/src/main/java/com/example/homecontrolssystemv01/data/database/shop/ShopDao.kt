package com.example.homecontrolssystemv01.data.database.shop

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ShopDao {
//    @Query("SELECT * FROM shop_list")
//    fun getShopList(): Flow<MutableList<ShopDbModel>>

    @Query("SELECT * FROM shop_list")
    fun getShopList(): Flow<MutableList<ShopDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopItem(shopItem: ShopDbModel)

    @Query("DELETE FROM shop_list WHERE itemId=:itemId")
    suspend fun deleteShopItem(itemId:Int)

    @Query("SELECT * FROM task_list")
    fun getTaskList(): Flow<MutableList<TaskDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(item: TaskDbModel)

    @Query("DELETE FROM task_list WHERE itemId=:itemId")
    suspend fun deleteTaskItem(itemId:Int)

}