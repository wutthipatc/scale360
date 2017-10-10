package models

import play.api.libs.json.Json

case class ReturnTasks(returnTasks: List[ReturnTask])

object ReturnTasks {
  implicit val returnTasksFormat = Json.format[ReturnTasks]
}

