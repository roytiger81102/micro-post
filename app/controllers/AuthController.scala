package controllers

import com.github.t3hnar.bcrypt._
import forms.Login
import javax.inject.Inject
import jp.t2v.lab.play2.auth.LoginLogout
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.UserService

import scala.concurrent.ExecutionContext

@Singleton
class AuthController @Inject()(
  val userService: UserService,
  components: ControllerComponents
  )(implicit ec: ExecutionContext)
  extends AbstractController(components)
  with I18nSupport
  with AuthConfigSupport
  with LoginLogout {

  private val loginForm: Form[Login] = Form {
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply).verifying("AuthFailed", form => authenticate(form.email, form.password).isDefined)
  }

  def login: Action[AnyContent] = ???

  def logout: Action[AnyContent] = ???

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
