function removeDataset(id){

  var request = $.ajax({
    type: "post",
    url: "/datasets/removeDataset",
    contentType: "application/json",
    data: JSON.stringify({ "datasetID": id    })
  });

  request.fail(function(jqXHR,textStatus,errorThrown){
    alert("The following error occurred: " + errorThrown);
  });

  request.done(function(response, textStatus, jqXHR){
    var elem = document.getElementById("dataset_" + id);
    elem.parentNode.removeChild(elem);
  });


}

function launchNDSModal(){
  $('#ndsAddBtn')[0].setAttribute("style","");
  $('#ndsSaveBtn')[0].setAttribute("style","display:none;");
  $('.ui.basic.modal.NDS').modal({
    onApprove: function(){
      return false;
    }
  }).modal('show');
}

function launchNDSEditModal(id){
  $('#ndsAddBtn')[0].setAttribute("style","display:none;");
  $('#ndsSaveBtn')[0].setAttribute("style","");
  $('#ndsID')[0].value = id;

  var request = $.ajax({
    type: "get",
    url: "/datasets/getDataset",
    data: {
             "datasetID": id
           }
  });


  request.fail(function(jqXHR,textStatus,errorThrown){
    alert("The following error occurred: " + errorThrown);
  });

  request.done(function(response, textStatus, jqXHR){
      resp = JSON.parse(response);
      $('#ndsName')[0].value = resp["DatasetName"];
      $('#ndsSensor')[0].value = resp["SensorID"];
      $('#ndsDescription')[0].value = resp["DatasetDescription"];

      $('.ui.basic.modal.NDS').modal({
        onApprove: function(){
          return false;
        }
      }).modal('show');
  });

}

function ndsModalCancel(){
    $('#ndsName')[0].value = "";
    $('#ndsSensor')[0].value = "";
    $('#ndsDescription')[0].value = "";
}

function ndsModalAdd(){
  var name = document.getElementById("ndsName").value;
  if(name.length == 0){
    alert("Please fill all the required fields!");
    return;
  }
  var sensor = document.getElementById("ndsSensor").value;
  var description = document.getElementById("ndsDescription").value;

  var request = $.ajax({
    type: "post",
    url: "/datasets/newDataset",
    contentType: "application/json",
    data: JSON.stringify({ "DatasetName": name,
                           "SensorID": sensor,
                           "DatasetDescription" : description
                         })

  });

  request.fail(function(jqXHR,textStatus,errorThrown){
    alert("The following error occurred: " + errorThrown);
  });

  request.done(function(response, textStatus, jqXHR){
    if(response.substring(0,5) == "rowid"){

      addNewDataset(response.substring(6), name, sensor, description);

      $('#ndsName')[0].value = "";
      $('#ndsSensor')[0].value = "";
      $('#ndsDescription')[0].value = "";
      $('.ui.basic.modal.NDS').modal('hide');
    }
  });
}

function ndsModalSave(){
      var id = $('#ndsID')[0].value;
      var name = $('#ndsName')[0].value;
      var sensor = $('#ndsSensor')[0].value;
      var descr = $('#ndsDescription')[0].value;

      var request = $.ajax({
        type: "post",
        url: "/datasets/updatetDataset",
        contentType: "application/json",
        data: JSON.stringify({ "DatasetID": id,
                               "DatasetName": name,
                               "DatasetDescription" : descr,
                               "SensorID": sensor
                             }),

      });

      request.fail(function(jqXHR,textStatus,errorThrown){
        alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
        $('#ndsName')[0].value = "";
        $('#ndsSensor')[0].value = "";
        $('#ndsDescription')[0].value = "";
        $('.ui.basic.modal.NDS').modal('hide');

        var text =        "<i  class='close icon pointed'  data-content='Remove Dataset' onclick='removeDataset(" + id + ")'></i> " +
                          "<i  class='edit icon pointed'  data-content='Edit Dataset' onclick='launchNDSEditModal(" + id + ")'></i> "


        var iconsDiv = document.createElement("div");
        iconsDiv.setAttribute("align","right");
        iconsDiv.setAttribute("style","'width: 100%;'");
        iconsDiv.innerHTML = text;

        var segmDiv = document.createElement("div");
        segmDiv.setAttribute("class","ui segment");
        segmDiv.setAttribute("style","height: 100%;");
        segmDiv.appendChild(iconsDiv);
        segmDiv.innerHTML =  segmDiv.innerHTML +  "<strong>" + name + "</strong><br> " +
          "Libcitisim sensor: " + sensor + "<br> " + descr;

        var divDs = document.createElement("div");
        divDs.setAttribute("class","column");
        divDs.setAttribute("style","padding:1rem;")
        divDs.id = "dataset_" + id;
        divDs.appendChild(segmDiv);

        var elem = document.getElementById("dataset_" + id);
        elem.parentNode.replaceChild(divDs, elem);

      });

}

function addNewDataset(id, name, sensor, description){
  var text =        "<i  class='close icon pointed'  data-content='Remove Dataset' onclick='removeDataset(" + id + ")'></i> " +
                    "<i  class='edit icon pointed'  data-content='Edit Dataset' onclick='launchNDSEditModal(" + id + ")'></i> "


  var iconsDiv = document.createElement("div");
  iconsDiv.setAttribute("align","right");
  iconsDiv.setAttribute("style","'width: 100%;'");
  iconsDiv.innerHTML = text;

  var segmDiv = document.createElement("div");
  segmDiv.setAttribute("class","ui segment");
  segmDiv.setAttribute("style","height: 100%;");
  segmDiv.appendChild(iconsDiv);
  segmDiv.innerHTML =  segmDiv.innerHTML +  "<strong>" + name + "</strong><br> " +
    "Libcitisim sensor: " + sensor + "<br> " + description;

  var divDs = document.createElement("div");
  divDs.setAttribute("class","column");
  divDs.setAttribute("style","padding:1rem;")
  divDs.id = "dataset_" + id;
  divDs.appendChild(segmDiv);

  var impExpText = "<i  class='upload icon pointed'  data-content='Export Data' onclick='exportData(" + id + ")'></i> " +
                   "<i  class='download icon pointed'  data-content='Import Data' onclick='launchIMPModal(" + id + ")'></i> "
  var impExpDiv = document.createElement("div");
  impExpDiv.setAttribute("align","right");
  impExpDiv.setAttribute("style","'width: 100%;'");
  impExpDiv.innerHTML = impExpText;
  segmDiv.appendChild(impExpDiv);

  var beforeThis = document.getElementById("newDatasetBtn");
  beforeThis.parentNode.insertBefore(divDs, beforeThis);


}

function exportData(datasetID){
  window.location = "/datasets/export?datasetID=" + datasetID
}


function launchIMPModal(datasetID){
  $('#impID')[0].value = datasetID;
  $('.ui.basic.modal.IMP').modal('show');
}




function initElements(){
  $('.ui.dropdown').dropdown();
  $('i').popup({on: 'hover'});
  $('div').popup({on: 'hover'});
}



window.onload = initElements;
