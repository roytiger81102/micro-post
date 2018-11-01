package service
import models.User
import scalikejdbc.DBSession

import scala.util.Try

/**
  * ユーザー情報サービスクラス
  */
class UserServiceImpl extends UserService {

  /**
    * ユーザー情報を登録する
    * @param user ユーザー情報
    * @param dbSession DBセッションオブジェクト
    * @return 成功時:Success(AUTO_INCREMENTによるID値)/失敗時:Failure
    */
  def create(user: User)(implicit dbSession: DBSession): Try[Long] = Try {
    User.create(user)
  }

  /**
    * メールアドレスからユーザー情報を検索取得する
    * @param email メールアドレス
    * @param dbSession DBセッションオブジェクト
    * @return 成功時:Success(ユーザー情報)/失敗時:Failure
    */
  def findByEmail(email: String)(implicit dbSession: DBSession): Try[Option[User]] = Try {
    User.where('email -> email).apply().headOption
  }
}
