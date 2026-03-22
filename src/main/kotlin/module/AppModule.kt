package org.olahraga.module

import org.olahraga.repositories.*
import org.olahraga.services.AuthService
import org.olahraga.services.SportEventService
import org.olahraga.services.UserService
import org.koin.dsl.module

fun appModule(jwtSecret: String) = module {
    single<IUserRepository>        { UserRepository() }
    single<IRefreshTokenRepository> { RefreshTokenRepository() }
    single<ISportEventRepository>  { SportEventRepository() }

    single { AuthService(jwtSecret, get(), get()) }
    single { UserService(get(), get()) }
    single { SportEventService(get(), get()) }
}
