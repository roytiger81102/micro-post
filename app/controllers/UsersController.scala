package controllers

import javax.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.AuthenticationElement
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.UserService

/**
  * ユーザー情報操作コントローラクラス
  * @param userService
  * @param components
  */
@Singleton
class UsersController @Inject()(val userService: UserService, components: ControllerComponents)
  extends AbstractController(components)
  with I18nSupport
  with AuthConfigSupport
  with AuthenticationElement {

  /**
    * ユーザー情報一覧表示
    * @return
    */
  def index: Action[AnyContent] = StackAction { implicit request =>
    userService.findAll
      .map { users =>
        Ok(views.html.users.index(loggedIn, users))
      }
      .recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.UsersController.index())
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  /**
    * 指定IDのユーザー情報表示
    * @param id ユーザーID
    * @return
    */
  def show(id: Long): Action[AnyContent] = StackAction { implicit request =>
    userService
      .findById(id)
      .map { userOpt =>
        userOpt.map { user =>
          Ok(views.html.users.show(loggedIn, user))
        }.get
      }
      .recover {
        case e: Exception =>
          Logger.error(s"occured error", e)
          Redirect(routes.UsersController.index())
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

}
