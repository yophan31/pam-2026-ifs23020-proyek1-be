package org.delcom.module

import org.delcom.repositories.*
import org.delcom.services.AuthService
import org.delcom.services.SportEventService
import org.delcom.services.UserService
import org.koin.dsl.module

fun appModule(jwtSecret: String) = module {
    single<IUserRepository>        { UserRepository() }
    single<IRefreshTokenRepository> { RefreshTokenRepository() }
    single<ISportEventRepository>  { SportEventRepository() }

    single { AuthService(jwtSecret, get(), get()) }
    single { UserService(get(), get()) }
    single { SportEventService(get(), get()) }
}
