

function launchRLSModal(id){
    $('#nrID')[0].value = id;
    $('.ui.basic.modal.RLS').modal('show');

    $('#nrMin').on('input',function(){ checkInput("#nrMin"); });
    $('#nrMax').on('input',function(){ checkInput("#nrMax"); });
    $('#nrEsco').on('input',function(){ checkInput("#nrEsco"); });
    $('#nrClient').on('input',function(){ checkInput("#nrClient"); });
}

function checkInput(elemID){
  var value = $(elemID)[0].value;
  if("0123456789".indexOf(value.substring(value.length-1)) < 0){
      $(elemID)[0].value = value.substring(0, value.length-1);
  }
}


function nrModalCancel(){
    $('#nrName')[0].value = "";
    $('#nrMin')[0].value = "";
    $('#nrMax')[0].value = "";
    $('#nrEsco')[0].value = "";
    $('#nrClient')[0].value = "";
}

function nrModalAdd(){
    var scenarioID = $('#nrID')[0].value;
    var name = $('#nrName')[0].value;
    var min = $('#nrMin')[0].value;
    var max = $('#nrMax')[0].value;
    var esco = $('#nrEsco')[0].value;
    var client = $('#nrClient')[0].value;

    if(name == "" || min == "" || max == "" || esco == "" || client == ""){
      alert("One of the fields is empty!!!");
      return;
    }

    var request = $.ajax({
      type: "post",
      url: "/newRule",
      contentType: "application/json",
      data: JSON.stringify({ "scenarioID": scenarioID,
                             "name": name,
                             "min" : min,
                             "max": max,
                             "esco": esco,
                             "client": client
                           }),

    });

    request.fail(function(jqXHR,textStatus,errorThrown){
      alert("The following error occurred: " + errorThrown);
    });

    request.done(function(response, textStatus, jqXHR){
      if(response.substring(0,5) == "rowid"){
        addNewRule(name,min,max,esco,client,scenarioID,response.substring(6));
      }
    });

}










function addNewRule(name, min, max, esco, client, scenarioID, ruleID){
    var htmlStr = "<div id='rule_" + ruleID + "'> <i  class='close icon pointed'  data-content='Remove " + name + "' onclick='launchRMVRModal("+ruleID+");'></i> " +
                "<strong>" + name + "</strong>" +
                "- If the savings are between " +
                min + "% and " + max + "% then " +
                esco + "% sharing ESCO and " +
                client + "% sharing client.</div>"

    $('#rulesCol_' + scenarioID).append(htmlStr);
    $('i').popup({on: 'hover'});
}



function getRules(){
  var request = $.ajax({
                          type: "get",
                          url: "/getRules",
                      });

  request.fail(function(jqXHR,textStatus,errorThrown){
    alert("The following error occurred: " + errorThrown);
  });

  request.done(function(response, textStatus, jqXHR){
    var resultSet = JSON.parse(response);

    for (var key in resultSet){
      var row = resultSet[key];
      addNewRule(row['RuleName'],
                     row['RuleMin'],
                     row['RuleMax'],
                     row['RuleEsco'],
                     row['RuleClient'],
                     row['ScenarioID'],
                     row['RuleID']);

    }

  });
}


function launchRMVRModal(id,Sid){
    $('#rmvrID')[0].value = id;
    $('#rmvrSID')[0].value = Sid;
    $('.ui.basic.modal.RMVR').modal('show');
}

function rmvrModalYes(){
    var id = $('#rmvrID')[0].value;
    var Sid = $('#rmvrSID')[0].value;

    var request = $.ajax({
      type: "post",
      url: "/removeRule",
      contentType: "application/json",
      data: JSON.stringify({ "RuleID": id,
                              "ScenarioID": Sid
                            })
    });


    request.fail(function(jqXHR,textStatus,errorThrown){
      alert("The following error occurred: " + errorThrown);
    });

    request.done(function(response, textStatus, jqXHR){
      var element = $('#rule_' + id)[0];
      element.remove();
    });
}
