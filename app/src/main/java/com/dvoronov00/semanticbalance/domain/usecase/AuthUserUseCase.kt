package com.dvoronov00.semanticbalance.domain.usecase

import com.dvoronov00.semanticbalance.domain.model.User
import com.dvoronov00.semanticbalance.domain.repository.AuthRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AuthUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
) {

    fun getAuthorizedUser(): Observable<User> {
        return storageRepository.getUser().switchMap { storageUser ->
            if (storageUser.isSessionExpired()) {
                authRepository.auth(storageUser.username, storageUser.password).flatMap { newUser ->
                    storageRepository.saveUser(newUser)
                    Observable.just(newUser)
                }
            } else {
                Observable.just(storageUser)
            }
        }
    }
}





