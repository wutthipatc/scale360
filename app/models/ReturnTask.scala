package models

import play.api.libs.json.Json

case class ReturnTask(id: Long, subject: String, detail: String, status: String)

object ReturnTask {
  implicit val returnTaskFormat = Json.format[ReturnTask]
}