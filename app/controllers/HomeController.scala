package controllers

import javax.inject._
import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.UserService

/**
  * トップページコントローラクラス
  * @param components
  */
@Singleton
class HomeController @Inject()(val userService: UserService, components: ControllerComponents)
  extends AbstractController(components)
  with I18nSupport
  with AuthConfigSupport
  with OptionalAuthElement {

  /**
    * 初期表示
    * @return
    */
  def index() = StackAction { implicit request =>
    Ok(views.html.index(loggedIn))
  }

}
