package controllers

import com.github.t3hnar.bcrypt._
import forms.Login
import javax.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.LoginLogout
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.typedmap.TypedKey
import play.api.mvc._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * ログイン認証を行うコントローラクラス
  * @param userService
  * @param components
  * @param ec
  */
@Singleton
class AuthController @Inject()(
  val userService: UserService,
  components: ControllerComponents
)(implicit ec: ExecutionContext)
  extends AbstractController(components)
  with I18nSupport
  with AuthConfigSupport
  with LoginLogout {

  /**
    * ログイン情報の受け渡しを行うフォーム
    */
  private val loginForm: Form[Login] = Form {
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply).verifying("AuthFailed", form => authenticate(form.email, form.password).isDefined)
  }

  /**
    * ログインセッションを記憶するフォーム
    */
  private val rememberMeForm: Form[Boolean] = Form {
    "rememberme" -> boolean
  }

  /**
    * 初期表示
    * @return
    */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(
      views.html.login(loginForm, rememberMeForm.fill(request.session.get("rememberme").exists("true" ==)))
    )
  }

  /**
    * ログイン処理を行う
    * @return
    */
  def login: Action[AnyContent] = {
    Action.async { implicit request =>
      val rememberMe = rememberMeForm.bindFromRequest()
      loginForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors, rememberMe))),
        {
          login =>
            val req = request.addAttr(TypedKey[Boolean]("rememberme"), rememberMe.get)
            markLoggedIn(login.email)(req, ec) {
              Redirect(routes.HomeController.index())
                .withSession("rememberme" -> rememberMe.get.toString)
                .flashing("success" -> Messages("LoggedIn"))
            }
        }
      )
    }
  }

  /**
    * ログアウト処理を行う
    * @return
    */
  def logout: Action[AnyContent] = Action.async { implicit request =>
    markLoggedOut()(request, ec) {
      Redirect(routes.HomeController.index())
        .flashing("success" -> Messages("LoggedOut"))
        .removingFromSession("rememberme")
    }
  }

  /**
    * メールアドレスからユーザー情報を取得し入力されたパスワードが一致するかチェックする
    * @param email メールアドレス
    * @param password パスワード
    * @return ユーザー情報が存在する場合:ユーザー情報/存在しない場合:None
    */
  private def authenticate(email: String, password: String): Option[User] = {
    userService
      .findByEmail(email)
      .map { user =>
        user.flatMap { u =>
          if (password.isBcrypted(u.password))
            user
          else
            None
        }
      }
      .get
  }
}
