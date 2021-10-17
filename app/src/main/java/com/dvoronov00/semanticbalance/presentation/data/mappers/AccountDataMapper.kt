package com.dvoronov00.semanticbalance.presentation.data.mappers

import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.data.extension.calculateExistingDays
import com.dvoronov00.semanticbalance.domain.ResourceManager
import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.presentation.ui.account.model.AccountData
import javax.inject.Inject

class AccountDataMapper @Inject constructor(
    private val resourceManager: ResourceManager,
) {
    fun map(account: Account): AccountData {
        return AccountData(
            id = resourceManager.getString(R.string.accountId, account.id),
            balance = resourceManager.getString(R.string.moneyInRoubles, account.balance),
            tariffName = account.tariffName.orEmpty(),
            subscriptionFee = resourceManager.getString(R.string.moneyInRoublesPerMonth, account.subscriptionFee),
            state = account.state.orEmpty(),
            serviceList = account.services,
            daysExisting = mapDaysExisting(account)
        )
    }

    private fun mapDaysExisting(account: Account): AccountData.DaysExisting {
        val calculatedDays = account.calculateExistingDays()
        return if (calculatedDays > 0) {
            AccountData.DaysExisting.SomeDaysExisting(
                resourceManager.getQuantityString(
                    R.plurals.afterDays,
                    calculatedDays
                )
            )
        } else {
            AccountData.DaysExisting.ZeroDaysExisting(
                if (account.state.equals(resourceManager.getString(R.string.state_unlocked)))
                    resourceManager.getString(R.string.hintLowBalance)
                else resourceManager.getString(R.string.hintNoBalance)
            )
        }
    }
}