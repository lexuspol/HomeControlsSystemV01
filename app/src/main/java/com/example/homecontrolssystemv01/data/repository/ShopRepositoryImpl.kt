package com.example.homecontrolssystemv01.data.repository

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.homecontrolssystemv01.data.database.ShopDatabase
import com.example.homecontrolssystemv01.data.database.ShopDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.domain.ShopRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ShopRepositoryImpl (private val application: Application): ShopRepository {

    private val shopDao = ShopDatabase.getInstance(application).shopDao()
    private  val mapper = DataMapper()

    private val myRef = Firebase.database(FIREBASE_URL)//.getReference(FIREBASE_PATH)
    private var shopListMSL = mutableStateListOf<ShopDbModel>()

    private var shopPublicListFlow = flowOf(listOf<ShopDbModel>())

    private var addShopListener = false

    private val shopEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

         //   Log.d("HCS","shopEventListener")

            val data = snapshot.getValue<List<ShopDbModel?>>()
            val list = mutableListOf<ShopDbModel>()



            shopListMSL.clear()

            if (data != null){
                data.forEach {item->
                    if (item != null){
                        shopListMSL.add(item)
                        list.add(item)
                    }
                }

                shopPublicListFlow.map {
                    it + list
                }

                        // shopListMSL = list

                shopListMSL.sortBy { it.groupId }
                shopListMSL.sortBy { !it.enabled }

            }else {
                shopListMSL.clear()
            }
           // Log.d("HCS","shop = $data")
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("HCS_FIREBASE_ERROR", "Failed to read shop value.", error.toException())
        }
    }

    private fun getList():Flow<List<ShopDbModel>>{
        return callbackFlow {

            val ref = myRef.getReference(FIREBASE_PATH_SHOP)
            Log.d("HCS","ref.addValueEventListener")

            val listener = ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.map { ds ->
                        ds.getValue(ShopDbModel::class.java)
                    }
                    trySend(items.filterNotNull())
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
            awaitClose {
                ref.removeEventListener(listener)
                Log.d("HCS","ref.removeEventListener")
            }

        }
    }
    override fun getPublicShopList():Flow<List<ShopDbModel>>{
        return getList()
    }

    override fun putPublicShopItem(item: ShopDbModel) {
        myRef.getReference(FIREBASE_PATH_SHOP).child(item.itemId.toString()).setValue(item)
    }

    override fun deletePublicShopItem(id: Int) {
        myRef.getReference(FIREBASE_PATH_SHOP).child(id.toString()).removeValue()
    }

    override suspend fun putPersonalShopItem(shopItem: ShopDbModel) {
        try {
            shopDao.insertShopItem(shopItem)
        }catch (e:Exception){
            Log.d("HCS_putDataSetting","Error Data Base")
        }
    }

    override suspend fun deletePersonalShopItem(id: Int) {
        try {
            shopDao.deleteShopItem(id)
        }catch (e:Exception){
            Log.d("HCS_putDataSetting","Error Data Base")
        }

    }

    override fun getPersonalShopList(): Flow<List<ShopDbModel>> {

//        val list = shopDao.getShopListMSL().map { list->
//            list.map { mapper.mapShopItemToEntity(it) }
//             }

        return shopDao.getShopList()
    }

    init {
    }

    companion object{
        const val FIREBASE_URL =
            "https://homesystemcontrolv01-default-rtdb.asia-southeast1.firebasedatabase.app"
        const val FIREBASE_PATH_SHOP = "shop"
    }

}
