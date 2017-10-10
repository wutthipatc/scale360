   var updateId;
   $(document).ready(function(){
	   if($("#addTaskBtn").length > 0){
		   addFunction.call();
	   }
	   if($("#updateTask").length > 0){
		   updateId = window.location.search.substring(window.location.search.indexOf('=')+1); 
		   updateFunction.call();
	   }
	   if(window.location.pathname == "/selectPage"){
		   selectFunction.call();
	   }
   });
    
  var addFunction = function() {
	bindAddBtn.call();
    return $.get("/tasks", function(data) {
      var temp = data.returnTasks;
      return temp.forEach(function(task) {
    	var selectButton = "<button id=select" + task.id + ">Select</button>";
    	var deleteButton = "<button id=delete" + task.id + ">Delete</button>";
    	var editButton = "<button id=edit" + task.id + ">Edit</button>";
    	var statusInput = '<label style="margin-left:5px" for="status">Status</label><input type="radio" name="status' + task.id+ '" value="Pending"/> Pending <input type="radio" name="status' + task.id+ '" value="Done"/> Done';
        $("#tasks").append($("<li>").text("subject : " + task.subject + " , detail : " + task.detail + " , status : " + task.status )).append(selectButton).append(deleteButton).append(editButton).append(statusInput);
        $(":button#select"+ task.id).click(function() {
        	$.ajax({
        		url: '/task/'+ task.id,
        		type: 'GET',
        		success: function(result){
        			window.location.replace("/selectPage?subject="+result.subject+"&detail="+result.detail+"&status="+result.status);
        		}
        	});
		});
        $(":button#delete"+ task.id).click(function(){
        	$.ajax({
        		url: '/task/'+ task.id,
        		type: 'DELETE',
        		success: function(result){
        			window.location.replace("/index");
        		}
        	});
        });
        $(":button#edit"+ task.id).click(function(){
        	updateId = task.id;
        	window.location.replace("/updatePage?id="+updateId);
        });
        $(":input[name='status"+task.id+"']").change(function(){
        	$.ajax({
        		url: '/task/'+ task.id + '/updateStatus',
        		type: 'PUT',
        		contentType: 'text/plain',
        		data: this.value,
        		success: function(result){
        			window.location.replace("/index");
        		}
        	});
        });
      });
    });
  };
  
  var updateFunction = function() {
	return $(":button#updateTask").click(function(){
				$.ajax({
	        		url: '/task/'+ updateId,
	        		type: 'PUT',
	        		data: {subject: $("input[name='subject']").val(), detail: $("input[name='detail']").val(), status: $("input[name='status']:checked").val()},
	        		success: function(result){
	        			window.location.replace("/index");
	        		}
	        	});
			});
  };
  
  var bindAddBtn = function() {
	  return $(":button#addTaskBtn").click(function() {
	      $.ajax({
	      	url: '/task',
	      	type: 'POST',
	      	data: {subject: $("input[name='subject']").val(), detail: $("input[name='detail']").val(), status: $("input[name='status']:checked").val()},
	      	success: function(result){
	      		window.location.replace("/index");
	      	}
	      });
		});
  };
  
  var selectFunction = function (){
	  var paramText = window.location.search;
	  paramText = paramText.substring(1);
	  var paramList = paramText.split("&");
	  var subject, detail, status;
	  paramList.forEach(function(item, index){
		  if(index===0){subject = item.substring(item.indexOf("=")+1);}
		  else if(index===1){detail = item.substring(item.indexOf("=")+1);}
		  else{ status = item.substring(item.indexOf("=")+1);}
	  });
	  $("#selectedHead").append($("<h2>").text("Subject : "+subject+" , Detail : "+detail+" , Status : "+status));
  };
