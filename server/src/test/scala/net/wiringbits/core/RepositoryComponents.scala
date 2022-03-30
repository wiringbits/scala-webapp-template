package net.wiringbits.core

import net.wiringbits.repositories._
import play.api.db.Database

case class RepositoryComponents(
    database: Database,
    users: UsersRepository,
    userTokens: UserTokensRepository,
    userNotifications: UserNotificationsRepository,
    userLogs: UserLogsRepository
)
