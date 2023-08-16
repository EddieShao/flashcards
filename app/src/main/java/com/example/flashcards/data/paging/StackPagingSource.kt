package com.example.flashcards.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Stack

class StackPagingSource(
    var query: () -> String,
    var order: () -> String
) : PagingSource<Int, Stack>() {
    override fun getRefreshKey(state: PagingState<Int, Stack>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stack> {
        val page = params.key ?: 0
        return try {
            val entities = Database.instance.stackDao().pageStacks(
                query(),
                order(),
                params.loadSize,
                page * params.loadSize
            )
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}