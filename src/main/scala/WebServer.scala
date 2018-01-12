import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._

object WebServer {
  private[this] val Port = 8072

  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      pathPrefix("") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>pls post</h1>"))
        } ~
        post {
          formFieldSeq { fields => // will unmarshal JSON to JsValue
            println(s"-->>\n${fields.map{ case (k, v) => s"$k=$v"}.mkString("\n")}")
            complete(HttpEntity(ContentTypes.`application/json`,
              """
                |"response_type": "ephemeral",
                |"text": "done! @thanhbv"
              """.stripMargin))
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", Port)

    def shutdown(): Unit = {
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done
    }

    sys.addShutdownHook(shutdown())

    println(s"Server online at 0.0.0.0:$Port/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    shutdown()
  }
}
