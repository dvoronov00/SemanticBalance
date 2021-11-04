package com.dvoronov00.semanticbalance.data

import android.content.Context
import com.dvoronov00.semanticbalance.domain.ResourceManager
import timber.log.Timber

class ResourceManagerImpl(
    private val context: Context
) : ResourceManager {
    override fun getString(resId: Int, vararg params: Any?): String = runCatching {
            context.getString(resId, *params)
        }.fold(
            onSuccess = {
                it
            },
            onFailure = {
                Timber.w(it)
                ""
            }
        )

    override fun getQuantityString(pluralsId: Int, quantity: Int): String =
        context.resources.getQuantityString(pluralsId, quantity, quantity)
}

