package controllers

import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test._
import play.api.test.Helpers._

/**
  * トップページコントローラのテストクラス
  */
class HomeControllerSpec extends FunSpec
  with MustMatchers
  with GuiceOneAppPerSuite {

  describe("HomeController") {
    describe("route of HomeController#index") {
      it("should be valid") {
        val result = route(app, FakeRequest(GET, routes.HomeController.index().toString)).get
        status(result) mustBe OK
      }
    }
  }
}
