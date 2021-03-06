package services
import javax.inject.Singleton
import models.{MicroPost, PagedItems, UserFollow}
import scalikejdbc._
import skinny.Pagination

import scala.util.Try

/**
  * 投稿機能サービスクラス
  */
@Singleton
class MicroPostServiceImpl extends MicroPostService {

  /**
    * 投稿する
    * @param microPost 投稿情報
    * @param dbSession DBセッションオブジェクト
    * @return MicroPost.id
    */
  override def create(microPost: MicroPost)(implicit dbSession: DBSession): Try[Long] = Try {
    MicroPost.create(microPost)
  }

  /**
    * 投稿を削除する
    * @param microPostId
    * @param dbSession
    * @return
    */
  override def deleteById(microPostId: Long)(implicit dbSession: DBSession): Try[Int] = Try {
    MicroPost.deleteById(microPostId)
  }

  /**
    * 指定ユーザーの投稿を取得する
    * @param pagination ページ割情報
    * @param userId ユーザーID
    * @param dbSession DBセッションオブジェクト
    * @return
    */
  override def findByUserId(pagination: Pagination, userId: Long)(
    implicit dbSession: DBSession
  ): Try[PagedItems[MicroPost]] =
    countBy(userId).map { size =>
      PagedItems(pagination, size, findAllByWithLimitOffset(Seq(userId))(pagination))
    }

  /**
    * 指定ユーザーの投稿件数を取得する
    * @param userId ユーザーID
    * @param dbSession DBセッションオブジェクト
    * @return 投稿件数
    */
  override def countBy(userId: Long)(implicit dbSession: DBSession): Try[Long] = Try {
    MicroPost.countBy(sqls.eq(MicroPost.defaultAlias.userId, userId))
  }

  /**
    * タイムライン(自身+フォロワーの投稿)を取得する
    * @param pagination ページ割り情報
    * @param userId ユーザーID
    * @param dbSession DBセッションオブジェクト
    * @return 投稿
    */
  override def findAllByWithLimitOffset(pagination: Pagination, userId: Long)(
    implicit dbSession: DBSession
  ): Try[PagedItems[MicroPost]] = Try {
    val followingIds = UserFollow.findAllBy(sqls.eq(UserFollow.defaultAlias.userId, userId)).map(_.followId)
    val size = MicroPost.countBy(sqls.in(MicroPost.defaultAlias.userId, userId +: followingIds))
    PagedItems(pagination, size, findAllByWithLimitOffset(userId +: followingIds)(pagination))
  }

  private def findAllByWithLimitOffset(userIds: Seq[Long])(pagination: Pagination)(
    implicit dbSession: DBSession
  ): Seq[MicroPost] = MicroPost.findAllByWithLimitOffset(
    sqls.in(MicroPost.defaultAlias.userId, userIds),
    pagination.limit,
    pagination.offset,
    Seq(MicroPost.defaultAlias.id.desc)
  )
}
