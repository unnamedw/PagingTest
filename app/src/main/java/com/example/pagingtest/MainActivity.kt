package com.example.pagingtest

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.pagingtest.ui.theme.PagingTestTheme

class MainActivity : ComponentActivity() {

    private val pageSize = 10

    private val dataFlow = Pager(
        PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize
        )
    ) {
        TestPagingSource(perPage = pageSize)
    }.flow
        .cachedIn(lifecycleScope)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            PagingTestTheme {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = splashRoute
                ) {
                    composable(splashRoute) {
                        SplashScreen(navController)
                    }

                    composable(mainRoute) {
                        val lambda: () -> Unit = it.arguments?.getSerializable("lambda") as () -> Unit

                        MainScreen(
                            param = lambda,
                            dataFlow = dataFlow,
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun PagingItem(
    index: Int,
    data: ResponseDto.DataDto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            color = Color.Black,
            text = "[$index] ${data.displayName}"
        )
    }
}