package com.example.homecontrolssystemv01.domain

import com.example.homecontrolssystemv01.data.database.ShopDbModel
import kotlinx.coroutines.flow.Flow


interface ShopRepository {

   //Public Screen
   fun getPublicShopList(): Flow<List<ShopDbModel>>
   fun putPublicShopItem(item: ShopDbModel)
   fun deletePublicShopItem(id:Int)

   //Personal Screen
   fun getPersonalShopList(): Flow<List<ShopDbModel>>
   suspend fun putPersonalShopItem(shopItem:ShopDbModel)
   suspend fun deletePersonalShopItem(id:Int)



}