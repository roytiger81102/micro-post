import com.google.inject.AbstractModule
import service.{UserService, UserServiceImpl}

/**
  * DIの設定を行うクラス
  */
class AppModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
  }
}
