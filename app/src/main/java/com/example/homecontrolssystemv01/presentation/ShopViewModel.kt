package com.example.homecontrolssystemv01.presentation

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.homecontrolssystemv01.data.database.shop.ShopDbModel
import com.example.homecontrolssystemv01.data.database.shop.TaskDbModel
import com.example.homecontrolssystemv01.data.repository.ShopRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val aplic = application

    private val repository = ShopRepositoryImpl(aplic)

    //shopList
    var shopPublicList by mutableStateOf(emptyList<ShopDbModel>())
    var shopPersonalList by mutableStateOf(emptyList<ShopDbModel>())
    var taskList by mutableStateOf(emptyList<TaskDbModel>())
    private val emptyShopList = listOf<ShopDbModel>()
    private val emptyTaskList = listOf<TaskDbModel>()
    private var jobGetPublicShopList: Job? = null
    private var jobGetPersonalShopList: Job? = null
    private var jobGetTaskList: Job? = null

    //    //Public Screen
//    fun getPublicShopListUI(): SnapshotStateList<ShopDbModel> {
//        Log.d("HCS","getPublicShopListUI")
//        return repository.getPublicShopList()
//    }
    fun getPublicShopListUI() {
        jobGetPublicShopList = viewModelScope.launch {
            repository.getPublicShopList().collect { list ->
                Log.d("HCS", "Public - launch")
                //записываем пустой лист чтобы Compouse видел изменение внутри ShopItem
                shopPublicList = emptyShopList
                shopPublicList = list.sortedWith(compareBy({ !it.enabled }, { it.groupId }))
            }
        }
    }

    fun getPersonalShopListUI() {
        jobGetPersonalShopList = viewModelScope.launch {
            repository.getPersonalShopList().collect { list ->
                Log.d("HCS", "Personal - launch")
                //записываем пустой лист чтобы Compouse видел изменение внутри ShopItem
                shopPersonalList = emptyShopList
                shopPersonalList = list.sortedWith(compareBy({ !it.enabled }, { it.groupId }))
            }
        }
    }

    fun getTaskListUI() {
        jobGetTaskList = viewModelScope.launch {
            repository.getTaskList().collect { list ->
                Log.d("HCS", "Task - launch")
                //записываем пустой лист чтобы Compouse видел изменение внутри ShopItem
                taskList = emptyTaskList
                taskList = list.sortedWith(compareBy({ !it.enabled }, { it.groupId }))
            }
        }
    }

    fun closeShopScreenUI() {
        jobGetPublicShopList?.cancel()
        jobGetPersonalShopList?.cancel()
        jobGetTaskList?.cancel()
    }

    //Public Screen
    fun putPublicShopItemUI(item: ShopDbModel) {
        repository.putPublicShopItem(item)
    }
    fun deletePublicShopItemUI(id: Int) {
        // Log.d("HCS","deletePublicShopItemUI - id = $id")
        repository.deletePublicShopItem(id)
    }

    //Personal Screen
    fun putPersonaShopItemUI(item: ShopDbModel) {
        viewModelScope.launch { repository.putPersonalShopItem(item) }
    }
    fun deletePersonaShopItemUI(id: Int) {
        viewModelScope.launch { repository.deletePersonalShopItem(id) }
    }

    //Task Screen
    fun putTaskItemUI(item: TaskDbModel) {
        viewModelScope.launch { repository.putTaskItem(item) }
    }
    fun deleteTaskItemUI(id: Int) {
        viewModelScope.launch { repository.deleteTaskItem(id) }
    }



    private val _selectedTabShop: MutableState<Int> = mutableStateOf(0)
    val selectedTabShop: State<Int> get() = _selectedTabShop
    fun selectTabShop(@StringRes tab: Int) {
        _selectedTabShop.value = tab
    }


    init {

    }


    override fun onCleared() {
        super.onCleared()
        //if (_connectSetting.cycleMode) {
        // closeConnect()
        // }
    }

    companion object {

    }


}