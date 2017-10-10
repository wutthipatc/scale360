package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.{ Task, DB, ReturnTasks, ReturnTask }
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action {
    Ok(views.html.index())
  }

  val taskForm: Form[Task] = Form {
    mapping(
      "subject" -> text, "detail" -> text, "status" -> text)(Task.apply)(Task.unapply)
  }

  def addTask = Action { implicit request =>
    val formTask = taskForm.bindFromRequest()
    if (formTask == None) Status(400)
    else {
      val task = formTask.get
      if (!("Pending" equals task.status) && !("Done" equals task.status)) Status(400)
      else {
        DB.save(task)
        Status(201)
      }
    }
  }

  def getTasks = Action { implicit request =>
    var returnTaskBuffer = new ListBuffer[ReturnTask]()
    val tasks = DB.query[Task].order("id", false).fetch()
    tasks.foreach(task => {
      val returnTask = ReturnTask(task.id, task.subject, task.detail, task.status)
      returnTaskBuffer += returnTask
    })

    val retList = returnTaskBuffer.toList
    Ok(Json.toJson(ReturnTasks(retList)))
  }

  def deleteTask(id: Long) = Action {
    try {
      val taskToDelete = DB.fetchById[Task](id)
      DB.delete(taskToDelete)
      Status(204)
    } catch {
      case ex: NoSuchElementException => Status(404)
    }
  }

  def updateTask(id: Long) = Action { implicit request =>
    val formTask = taskForm.bindFromRequest()
    if (formTask == None) Status(400)
    else {
      val updateData = formTask.get
      if (!("Pending" == updateData.status) && !("Done" == updateData.status)) Status(400)
      else {
        val taskOption: Option[Task] = DB.query[Task]
          .whereEqual("id", id)
          .fetchOne()
          .map(b => b.copy(updateData.subject, updateData.detail, updateData.status))
          .map(DB.save)
        if (taskOption.isEmpty) Status(404)
        else Status(204)
      }
    }
  }

  def showUpdatePage() = Action {
    Ok(views.html.update())
  }

  def updateStatus(id: Long) = Action { implicit request =>
    val status: String = request.body.asText.get
    if (!(status.equals("Pending") || status.equals("Done"))) Status(400)
    else {
      val taskOption: Option[Task] = DB.query[Task]
        .whereEqual("id", id).fetchOne()
        .map(a => a.copy(status = status))
        .map(DB.save)
      if (taskOption.isEmpty) Status(404)
      else Status(204)
    }
  }

  def showSelectPage() = Action {
    Ok(views.html.select())
  }

  def getTask(id: Long) = Action {
    try {
      val task = DB.fetchById[Task](id)
      Ok(Json.toJson(task))
    } catch {
      case ex: NoSuchElementException => Status(404)
    }
  }

}
