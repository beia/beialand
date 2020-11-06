document.addEventListener('DOMContentLoaded',function(){
  drawOldList();
}, false);


function drawOldList(){
  var request = $.ajax({
    type: "get",
    url: "/solomon/retailer/oldCampaigns"
  });

  request.fail(function(jqXHR,textStatus,errorThrown){
    alert(jqXHR.responseText);
  });

  request.done(function(response, textStatus, jqXHR){
    if(response.length > 0){
      for(var i=0; i<response.length; i++){
        drawOldElementInList(response[i]);
      }
    } else {
      alert("You have 0 expired campaigns!");
      window.location.replace("/solomon/dashboard");
    }
  });
}


function drawOldElementInList(campaign){
    var title = document.createElement("p");
    title.innerHTML = "<strong>" + campaign["title"] + "</strong>";
    var period = document.createElement("p");
    period.innerHTML = campaign["startDate"].substring(0,10) + " <-> " + campaign["endDate"].substring(0,10);
    var description = document.createElement("p");
    description.innerHTML = campaign["description"];

    var img = document.createElement("img");
    img.setAttribute("class", "oldImage");
    img.src = "data:*/*;base64," + campaign["image"];

    var leftTD =  document.createElement("td");
    leftTD.setAttribute("class","left-part");
    leftTD.appendChild(title);
    leftTD.appendChild(period);
    leftTD.appendChild(description);
    var rightTD =  document.createElement("td");
    rightTD.setAttribute("class","right-part");
    rightTD.appendChild(img);
    var tbRow = document.createElement("tr");
    tbRow.appendChild(leftTD);
    tbRow.appendChild(rightTD);
    var tbl = document.createElement("table");
    tbl.appendChild(tbRow);

    var segmentDiv = document.createElement("div");
    segmentDiv.setAttribute("class", "ui segment myBox");
    segmentDiv.setAttribute("onclick", "getStatisticsForCampaign("+campaign["id"]+",'"+campaign["startDate"]+"')");
    segmentDiv.appendChild(tbl);

    var rmvIcon = document.createElement("i");
    rmvIcon.setAttribute("class","close icon");
    rmvIcon.setAttribute("data-content","Remove Campaign");
    rmvIcon.addEventListener('click', function(){
      launchRMVModal(campaign["id"]);
    })

    var campaignDiv = document.createElement("div");
    campaignDiv.id = campaign["id"];
    campaignDiv.appendChild(rmvIcon);
    campaignDiv.appendChild(segmentDiv);

    document.getElementById("segments-zone").appendChild(campaignDiv);
    $('i').popup();
}

function launchRMVModal(campaignID) {
  document.getElementById("itemID").value = campaignID;
  $('.ui.basic.modal.RMVS').modal('show');
}

function removeOldCampaign() {
  var campaignID = document.getElementById("itemID").value;
  var requestUrl = "/solomon/retailer/campaigns/" + campaignID;

  var request = $.ajax({
    type: "delete",
    url: requestUrl
  });

  request.fail(function(jqXHR,textStatus,errorThrown){
    alert(jqXHR.responseText);
  });

  request.done(function(response, textStatus, jqXHR){
    var campaignElement = document.getElementById(campaignID);
    campaignElement.parentNode.removeChild(campaignElement);


    if(document.getElementById("segments-zone").childNodes.length == 1){
      alert("You have 0 expired campaigns!");
      window.location.replace("/solomon/dashboard");
    }
  });
}

var json = {}
function getStatisticsForCampaign(campaignId, startDate){
    if(document.getElementById("viewStatisticsTable").hidden == false
        && document.getElementById("itemID").value == campaignId){
            document.getElementById("viewStatisticsTable").hidden = true;
            return;
    }

    document.getElementById("itemID").value = campaignId;
    json[campaignId] = {};
    json[campaignId]["startDate"] = startDate;

    var statsTable = document.getElementById("viewStatisticsTable");
    statsTable.parentNode.removeChild(statsTable);
    document.getElementById(campaignId).appendChild(statsTable);

    getStatistics();
}
