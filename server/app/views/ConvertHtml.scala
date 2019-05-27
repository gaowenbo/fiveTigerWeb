package views

import controllers.routes

object ConvertHtml {
    def getHtml =
      {
        var js = scalajs.html.scripts("client", routes.Assets.versioned(_).toString, name => getClass.getResource(s"/public/$name") != null)
        "<!DOCTYPE html>" +
          s"""<html>
            <body>
              <div>旧地址已废弃</div>

              <div id="playground"></div>
              ${js}
            </body>
          </html>"""
      }

}
