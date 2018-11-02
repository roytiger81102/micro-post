package controllers

import jp.t2v.lab.play2.auth.{AuthenticityToken, CookieTokenAccessor}
import play.api.libs.typedmap.TypedKey
import play.api.mvc.{Cookie, RequestHeader, Result}

/**
  * ログインセッションを保存するトークンアクセサ
  * @param maxAge
  */
class RememberMeTokenAccessor(maxAge: Int) extends CookieTokenAccessor() {

  // remembermeフラグ: true の場合、トークンを指定した有効期間で保存する。false の場合はブラウザが開いている間のみ有効とする
  override def put(token: AuthenticityToken)(result: Result)(implicit request: RequestHeader): Result = {
    val remember = request.attrs.get(TypedKey[Boolean]("rememberme")).getOrElse(false) ||
      request.session.get("rememberme").exists("true" ==)
    val _maxAge = if (remember) Some(maxAge) else None
    val c = Cookie(
      cookieName,
      sign(token),
      _maxAge,
      cookiePathOption,
      cookieDomainOption,
      cookieSecureOption,
      cookieHttpOnlyOption
    )
    result.withCookies(c)
  }

}
