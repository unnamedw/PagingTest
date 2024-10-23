package com.example.pagingtest

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState

class TestPagingSource(private val perPage: Int): PagingSource<String, ResponseDto.DataDto>() {
    var prevKey: String? = null

    override fun getRefreshKey(state: PagingState<String, ResponseDto.DataDto>): String? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey ?: anchorPage?.nextKey
//        }
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPageIndex = state.pages.indexOf(state.closestPageToPosition(anchorPosition))
            val refreshKey = state.pages.getOrNull(anchorPageIndex + 1)?.prevKey ?: "*"
            Log.d("TestLog", "anchorPosition: ${state.anchorPosition} anchorPageIndex: $anchorPageIndex getRefreshKey: $refreshKey")

            refreshKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ResponseDto.DataDto> {
        try {
            val nextCursor = params.key ?: "*"
            Log.d("TestLog", "nextCursor: $nextCursor")
            val response = service.getList(perPage = perPage, cursor = nextCursor)
            Log.d("TestLog", "cursor: ${response.meta.nextCursor}")
            return LoadResult.Page(
                data = response.results,
                prevKey = prevKey ?: "*",
                nextKey = response.meta.nextCursor
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(throwable = e)
        }
    }

}