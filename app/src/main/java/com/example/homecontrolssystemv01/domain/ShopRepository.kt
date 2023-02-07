package com.example.homecontrolssystemv01.domain

import com.example.homecontrolssystemv01.data.database.shop.ShopDbModel
import com.example.homecontrolssystemv01.data.database.shop.TaskDbModel
import kotlinx.coroutines.flow.Flow


interface ShopRepository {

   //Public Screen
   fun getPublicShopList(): Flow<List<ShopDbModel>>
   fun putPublicShopItem(item: ShopDbModel)
   fun deletePublicShopItem(id:Int)

   //Personal Screen
   fun getPersonalShopList(): Flow<List<ShopDbModel>>
   suspend fun putPersonalShopItem(item: ShopDbModel)
   suspend fun deletePersonalShopItem(id:Int)

   //Task Screen
   fun getTaskList(): Flow<List<TaskDbModel>>
   suspend fun putTaskItem(item: TaskDbModel)
   suspend fun deleteTaskItem(id:Int)



}