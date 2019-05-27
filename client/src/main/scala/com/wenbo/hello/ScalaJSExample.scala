package com.wenbo.hello

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{Element, HTMLTextAreaElement}
import com.wenbo.chat.ChatPage
import scala.scalajs.js.timers.setTimeout

object ScalaJSExample {

  def main(args: Array[String]): Unit = {
    var m = document.location.search
    document.location.href = "http://192.168.1.21:8090" + m
    var route = (document.location.href.substring(document.location.protocol.size + 2 + document.location.host.size))
    if (route == "/" || route == "" || route.startsWith("/?")) {
      //创建一个标签
      var input = dom.document.createElement("textarea").asInstanceOf[HTMLTextAreaElement]
      dom.document.body.appendChild(input)

      var button = dom.document.createElement("button")
      button.innerHTML = "批量数据库"
      var changeText = (e: Event) => {
        var value = input.value
        var realDb = "zhiyunshan_trade_order"
        //      var result = (1 to 32).map(i => {
        //        value.replaceAll("`[a-zA-Z0-9_]+`\\.", "`" + realDb + i + "`.")
        //      }).mkString(",")
        var result = (1 to 32).map(i => {
          value.replaceAll("pay", realDb + "" + i)
        }).mkString(",")
        input.value = result
      }
      button.addEventListener("click", changeText);
      dom.document.body.appendChild(button)
      //connectWS
    } else if (route == "/convert" || route.startsWith("/convert?")) {
      println(route)
      ScalaJSExample2.run
    } else if (route == "/chatPage" || route.startsWith("/chatPage?")) {
      println(route)
      ChatPage.run
    }
  }


}
