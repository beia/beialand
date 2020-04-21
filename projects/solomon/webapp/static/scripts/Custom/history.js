document.addEventListener('DOMContentLoaded',function(){
  drawOldList();


}, false);

function drawOldList(){
  requestData = {};
  requestData["requestType"] = "oldCampaigns";

  var url_string = window.location.href;
  var url = new URL(url_string);
  var authToken = url.searchParams.get("authToken");
  requestData["authToken"] = authToken;


  var request = $.ajax({
    type: "post",
    url: window.location.origin,
    contentType: "application/json",
    data: JSON.stringify(requestData),
  });

  request.fail(function(jqXHR,textStatus,errorThrown){
    alert("The following error occurred: " + errorThrown);
  });

  request.done(function(response, textStatus, jqXHR){
    if(response["oldCampaigns"] != null){
      var campaigns = response["oldCampaigns"].split(",");
      for(var i=0; i<campaigns.length; i++){
        drawOldElementInList(campaigns[i]);
      }
    }
  });
}


function drawOldElementInList(campaignID){
  //get  Campaign  Details
  requestData = {};
  requestData["requestType"] = "getCampaign";
  requestData["campaignID"] = campaignID;

  var url_string = window.location.href;
  var url = new URL(url_string);
  var authToken = url.searchParams.get("authToken");
  requestData["authToken"] = authToken;


  var request = $.ajax({
    type: "post",
    url: window.location.origin,
    contentType: "application/json",
    data: JSON.stringify(requestData),
  });

  request.fail(function(jqXHR,textStatus,errorThrown){
    alert("The following error occurred: " + errorThrown);
  });

  request.done(function(response, textStatus, jqXHR){
    if(response["campaignID"] == null){
      alert("ERROR: Campaign not found!")
    } else {
      var title = document.createElement("p");
      title.innerHTML = "<strong>" + response["title"] + "</strong>";
      var period = document.createElement("p");
      period.innerHTML = response["startDate"] + " - " + response["endDate"];
      var description = document.createElement("p");
      description.innerHTML = response["description"];

      var img = document.createElement("img");
      img.setAttribute("class", "oldImage");
      img.src = "data:*/*;base64," + response["image"];

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
      segmentDiv.appendChild(tbl);

      var rmvIcon = document.createElement("i");
      rmvIcon.setAttribute("class","close icon");
      rmvIcon.setAttribute("data-content","Remove Campaign");

      var campaignDiv = document.createElement("div");
      campaignDiv.id = response["campaignID"];
      campaignDiv.appendChild(rmvIcon);
      campaignDiv.appendChild(segmentDiv);

      document.getElementById("segments-zone").appendChild(campaignDiv);
      $('i').popup();
    }
  });


}
