package service

import models.User
import scalikejdbc.{AutoSession, DBSession}

import scala.util.Try

/**
  * ユーザー情報トレイト
  */
trait UserService {

  def create(user: User)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def findByEmail(email: String)(implicit dbSession: DBSession = AutoSession): Try[Option[User]]
}
