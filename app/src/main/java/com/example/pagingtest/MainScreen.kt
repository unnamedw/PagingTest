package com.example.pagingtest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

const val mainRoute = "mainRoute"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    dataFlow: Flow<PagingData<ResponseDto.DataDto>>,
    param: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dataFlowState = dataFlow.collectAsLazyPagingItems()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                delay(3000)
                dataFlowState.refresh()
            }
        })

    LaunchedEffect(Unit) {
        param.invoke()
    }

    LaunchedEffect(dataFlowState.loadState.refresh) {
        if (dataFlowState.loadState.refresh is LoadState.NotLoading)
            isRefreshing = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(dataFlowState.itemCount) { index ->
                dataFlowState[index]?.let { PagingItem(index = index, data = it) }
            }
            if (dataFlowState.loadState.append == LoadState.Loading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
        PullRefreshIndicator(refreshing = isRefreshing, state = pullRefreshState)
    }
}