package models

import play.api.libs.json.Json

case class Task(subject: String, detail: String, status: String)

object Task {
  implicit val taskFormat = Json.format[Task]
}