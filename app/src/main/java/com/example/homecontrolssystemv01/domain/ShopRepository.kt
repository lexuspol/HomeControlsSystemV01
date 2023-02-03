package com.example.homecontrolssystemv01.domain

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.homecontrolssystemv01.data.database.ShopDbModel
import com.example.homecontrolssystemv01.domain.model.shop.ShopItem
import kotlinx.coroutines.flow.Flow


interface ShopRepository {

   //Public Screen
   //fun getPublicShopList(): SnapshotStateList<ShopDbModel>
   fun getPublicShopList(): Flow<List<ShopDbModel>>
   fun putPublicShopItem(item: ShopDbModel)
   fun deletePublicShopItem(id:Int)

   //Personal Screen
   fun getPersonalShopList(): Flow<List<ShopDbModel>>
   suspend fun putPersonalShopItem(shopItem:ShopDbModel)
   suspend fun deletePersonalShopItem(id:Int)



}