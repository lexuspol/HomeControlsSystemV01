package com.example.homecontrolssystemv01.presentation.screen.shop

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.presentation.ShopViewModel

@Composable
fun ShopScreen(
    viewModel: ShopViewModel,
    pressOnBack: () -> Unit = {}
) {

    LaunchedEffect(Unit) {
        viewModel.getPublicShopListUI()
        viewModel.getPersonalShopListUI()
    }

    val selectedTab = ShopScreenTab.getTabFromResource(viewModel.selectedTabShop.value)
    val tabs = ShopScreenTab.values()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primarySurface,
        bottomBar = {
            BottomNavigation(
            ) {
                tabs.forEach { tab ->
                    val color = LocalContentColor.current
                    BottomNavigationItem(
                        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                        label = { Text(text = stringResource(tab.title), color = Color.White) },
                        selected = tab == selectedTab,
                        onClick = { viewModel.selectTabShop(tab.title) },
                        //selectedContentColor = MaterialTheme.colors.background,
                        //unselectedContentColor = color,
                    )
                }
            }
        },
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .fillMaxHeight()

        Crossfade(selectedTab) { destination ->
            when (destination) {
                ShopScreenTab.PUBLIC -> ShopItemsScreen(
                    route = NavShopScreen.ShopPublicScreen.route,
                    shopList = viewModel.shopPublicList,
                    putItem = { viewModel.putPublicShopItemUI(it) },
                    deleteItem = { viewModel.deletePublicShopItemUI(it) }
                ) { pressOnBack() }

                ShopScreenTab.PERSONAL -> ShopItemsScreen(
                    route = NavShopScreen.ShopPersonalScreen.route,
                    shopList = viewModel.shopPersonalList,
                    putItem = { viewModel.putPersonaShopItemUI(it) },
                    deleteItem = { viewModel.deletePersonaShopItemUI(it) }
                ) { pressOnBack() }

                ShopScreenTab.NOTES -> ShopNotesScreen()
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun TestPreviewShop() {

}

enum class ShopScreenTab(
    @StringRes val title: Int,
    val icon: ImageVector
) {
    PUBLIC(R.string.menu_shop_public, Icons.Filled.ShoppingCart),
    PERSONAL(R.string.menu_shop_personal, Icons.Filled.Person),
    NOTES(R.string.menu_shop_notes, Icons.Filled.Done);

    companion object {
        fun getTabFromResource(@StringRes resource: Int): ShopScreenTab {
            return when (resource) {
                R.string.menu_shop_notes -> NOTES
                R.string.menu_shop_personal -> PERSONAL
                else -> PUBLIC
            }
        }
    }
}

sealed class NavShopScreen(val route: String) {
    object ShopPublicScreen : NavShopScreen("ShopPublicScreen")
    object ShopPersonalScreen : NavShopScreen("ShopPersonalScree")
}