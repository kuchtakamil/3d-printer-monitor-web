package monitor

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

object Main {
  val wsUrl = "ws://localhost:9000/ws"
  val ws = new dom.WebSocket(wsUrl)

  val nameVar = Var(initial = "0")

  ws.onmessage = { (event: dom.MessageEvent) =>
    val number = event.data.toString.toDouble
    nameVar.update(_ => s"Received: $number")
  }
  val rootElement = div(
    div(
      h1("3D printer monitor"),
      child.text <-- nameVar.signal
    )
  )

  val containerNode = dom.document.querySelector("#app")

  render(containerNode, rootElement)
}
