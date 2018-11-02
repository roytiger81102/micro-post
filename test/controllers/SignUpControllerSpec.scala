package controllers

import org.scalatest.{FunSpec, MustMatchers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import scalikejdbc.PlayModule
import services.{MockUserService, UserService}

/**
  * サインアップコントローラのテストクラス
  */
class SignUpControllerSpec extends FunSpec
  with MustMatchers
  with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .disable[PlayModule]
      .overrides(bind[UserService].to[MockUserService])
      .build()

  describe("SignUpController") {
    describe("route of SignUpController#index") {
      it("should be valid") {
        val result = route(app, FakeRequest(GET, routes.SignUpController.index().toString)).get
        status(result) mustBe OK
      }
    }
  }
}
