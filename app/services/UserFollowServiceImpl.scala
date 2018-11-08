package services

import javax.inject.Singleton
import models.{PagedItems, User, UserFollow}
import scalikejdbc._
import skinny.Pagination

import scala.util.Try

/**
  * ユーザーフォローサービスクラス
  */
@Singleton
class UserFollowServiceImpl extends UserFollowService {

  /**
    * フォロー情報を登録する
    * @param userFollow ユーザーフォロー情報
    * @param session DBセッションオブジェクト
    * @return UserFollow.id
    */
  override def create(userFollow: UserFollow)(implicit session: DBSession): Try[Long] = Try {
    UserFollow.create(userFollow)
  }

  /**
    * フォロー情報をリスト取得する
    * @param userId ユーザーID
    * @param session DBセッションオブジェクト
    * @return フォロー情報リスト
    */
  override def findById(userId: Long)(implicit session: DBSession): Try[List[UserFollow]] = Try {
    UserFollow.where('userId -> userId).apply()
  }

  /**
    * フォロー情報を1件取得する
    * @param followId フォローユーザーID
    * @param session DBセッションオブジェクト
    * @return フォロー情報
    */
  override def findByFollowId(followId: Long)(implicit session: DBSession): Try[Option[UserFollow]] = Try {
    UserFollow.where('followId -> followId).apply().headOption
  }

  /**
    * フォロワーー情報を取得する
    * @param pagination ページ割り情報
    * @param userId ユーザーID
    * @param session DBセッションオブジェクト
    * @return フォロワー情報
    */
  override def findFollowersByUserId(pagination: Pagination, userId: Long)(
    implicit session: DBSession
  ): Try[PagedItems[User]] =  {
    countByFollowId(userId).map { size =>
      PagedItems(pagination, size,
        UserFollow.allAssociations
          .findAllByWithLimitOffset(
            sqls.eq(UserFollow.defaultAlias.followId, userId),
            pagination.limit,
            pagination.offset,
            Seq(UserFollow.defaultAlias.id.desc)
          )
          .map(_.user.get)
      )
    }
  }

  /**
    * フォロー情報を取得する
    * @param pagination ページ割り情報
    * @param userId ユーザーID
    * @param session DBセッションオブジェクト
    * @return フォロー情報
    */
  override def findFollowingsByUserId(pagination: Pagination, userId: Long)(
    implicit session: DBSession
  ): Try[PagedItems[User]] = {
    countByUserId(userId).map { size  =>
      PagedItems(pagination, size,
        UserFollow.allAssociations
          .findAllByWithLimitOffset(
            sqls.eq(UserFollow.defaultAlias.userId, userId),
            pagination.limit,
            pagination.offset,
            Seq(UserFollow.defaultAlias.id.desc)
          )
          .map(_.followUser.get)
      )
    }
  }

  /**
    * フォローユーザー数を取得する
    * @param userId ユーザーID
    * @param dbSession DBセッションオブジェクト
    * @return フォローユーザー数
    */
  override def countByUserId(userId: Long)(implicit dbSession: DBSession): Try[Long] = Try {
    UserFollow.allAssociations.countBy(sqls.eq(UserFollow.defaultAlias.userId, userId))
  }

  /**
    * フォロワーユーザー数を取得する
    * @param userId ユーザーID
    * @param dbSession DBセッションオブジェクト
    * @return フォロワーユーザー数
    */
  override def countByFollowId(userId: Long)(implicit dbSession: DBSession): Try[Long] = Try {
    UserFollow.allAssociations.countBy(sqls.eq(UserFollow.defaultAlias.followId, userId))
  }

  /**
    * フォロー情報を削除する
    * @param userId ユーザーID
    * @param followId フォローユーザーID
    * @param dbSession
    * @return
    */
  override def deleteBy(userId: Long, followId: Long)(implicit dbSession: DBSession): Try[Int] = Try {
    val c     = UserFollow.column
    val count = UserFollow.countBy(sqls.eq(c.userId, userId).and.eq(c.followId, followId))
    if (count == 1) {
      UserFollow.deleteBy(
        sqls
          .eq(UserFollow.column.userId, userId)
          .and(sqls.eq(UserFollow.column.followId, followId))
      )
    } else 0
  }

}
