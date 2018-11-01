package controllers

import javax.inject._

import play.api.i18n.I18nSupport
import play.api.mvc._

/**
  * ホーム画面Controllerクラス
  * @param components
  */
@Singleton
class HomeController @Inject()(val components: ControllerComponents)
  extends AbstractController(components) with I18nSupport {

  def index() = Action { implicit request =>
    Ok(views.html.index())
  }

}
