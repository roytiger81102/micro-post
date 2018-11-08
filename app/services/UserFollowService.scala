package services

import models.{PagedItems, User, UserFollow}
import scalikejdbc.{AutoSession, DBSession}
import skinny.Pagination

import scala.util.Try

/**
  * ユーザーフォロートレイト
  */
trait UserFollowService {

  def create(userFollow: UserFollow)(implicit session: DBSession = AutoSession): Try[Long]

  def findById(userId: Long)(implicit  session: DBSession = AutoSession): Try[List[UserFollow]]

  def findByFollowId(followId: Long)(implicit session: DBSession = AutoSession): Try[Option[UserFollow]]

  def findFollowersByUserId(pagination: Pagination, userId: Long)(
    implicit session: DBSession = AutoSession
  ): Try[PagedItems[User]]

  def findFollowingsByUserId(pagination: Pagination, userId: Long)(
    implicit session: DBSession = AutoSession
  ): Try[PagedItems[User]]

  def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def countByFollowId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def deleteBy(userId: Long, followId: Long)(implicit  dbSession: DBSession = AutoSession): Try[Int]

}
