package monitor

import com.raquo.airstream.state.Var
import org.scalajs.dom
import com.raquo.laminar.api.L._
import io.circe._
import io.circe.parser._
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import org.scalajs.dom

import scala.language.postfixOps

sealed trait SimValue {
  val updatedOn: String
  val value: Int
  val status: String
}
case class CarriageSpeed(updatedOn: String, value: Int, status: String)  extends SimValue
case class BedTemperature(updatedOn: String, value: Int, status: String) extends SimValue

object Main {
  def main(args: Array[String]): Unit = {

    val wsUrl = "ws://127.0.0.1:9000/simvalue"
    val ws    = new dom.WebSocket(wsUrl)

    val carriageSpeedVar: Var[CarriageSpeed] = Var(
      CarriageSpeed(
        updatedOn = "Now",
        value = 0,
        status = "stopped",
      )
    )

    val bedTemperatureValueVar: Var[BedTemperature] = Var(
      BedTemperature(
        updatedOn = "Now",
        value = 0,
        status = "stopped",
      )
    )

    implicit lazy val simValDecoder: Decoder[SimValue] = deriveDecoder

    ws.onmessage = { (event: dom.MessageEvent) =>
      ws.send("ping")

      val simValue: Either[Error, SimValue] = decode[SimValue](event.data.toString)
      simValue match {
        case Right(cs: CarriageSpeed)  => carriageSpeedVar.update(_ => cs)
        case Right(bt: BedTemperature) => bedTemperatureValueVar.update(_ => bt)
        case Left(err)                 => println(s"Decoding failed: $err")
      }
    }

    val rootElement = div(
      cls("container"),
      div(
        cls("data-row"),
        h5("Carriage Speed"),
        div(cls("value"), child.text <-- carriageSpeedVar.signal.map(v => v.value + " m/s")),
        div(cls("value"), child.text <-- carriageSpeedVar.signal.map(v => v.status)),
        div(cls("value"), child.text <-- carriageSpeedVar.signal.map(v => v.updatedOn)),
      ),
      div(
        cls("data-row"),
        h5("Bed Temperature"),
        div(cls("value"), child.text <-- bedTemperatureValueVar.signal.map(v => v.value + " ℃")),
        div(cls("value"), child.text <-- bedTemperatureValueVar.signal.map(v => v.status)),
        div(cls("value"), child.text <-- bedTemperatureValueVar.signal.map(v => v.updatedOn)),
      ),
    )

    val containerNode = dom.document.querySelector("#app")

    render(containerNode, rootElement)
  }
}
