document.addEventListener('DOMContentLoaded',function(){
  $('.ui.dropdown').dropdown();


  // Log Out
  document.getElementById("logOutBtn").addEventListener('click', function(){
    requestData = {};
    requestData["requestType"] = "logout";

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
      alert("ERROR occurred: " + errorThrown);
    });

    request.done(function(response, textStatus, jqXHR){
      window.location.href = "/login";
    });

  },false);

  document.getElementById("newPromotionBtn").addEventListener('click', function(){
    launchNSModal(false);
  },false);

  document.getElementById("historyBtn").addEventListener('click', function(){
    var url_string = window.location.href;
    var url = new URL(url_string);
    var authToken = url.searchParams.get("authToken");
    requestData["authToken"] = authToken;

    window.location.href = "/history?authToken=" + authToken;
  },false);

  document.getElementById("contactBtn").addEventListener('click', function(){
    var url_string = window.location.href;
    var url = new URL(url_string);
    var authToken = url.searchParams.get("authToken");
    requestData["authToken"] = authToken;

    window.location.href = "/contact?authToken=" + authToken;
  },false);

  document.getElementById("leftSectionDiv").addEventListener('mouseover', function() {
    document.getElementById("leftSectionDiv").style.overflowY = "scroll";
  })
  document.getElementById("leftSectionDiv").addEventListener('mouseout', function() {
    document.getElementById("leftSectionDiv").style.overflowY = "hidden";
  })

  document.getElementById("search-input").addEventListener('input', function(){
    var leftDiv = document.getElementById("leftSectionDiv");
    var last = leftDiv.lastElementChild;
    while(last){
      if(last.tagName == "TABLE"){
        leftDiv.removeChild(last);
        last = leftDiv.lastElementChild;
      } else {
        break;
      }
    }

    var inValue = this.value;
    for(var i=0; i<json.length; i++){
      if(json[i].title.toLowerCase().includes(inValue.toLowerCase())){
        drawElementInList(json[i])
      }
    }
    console.log("------");
  })

  document.getElementById("removeBtn").addEventListener('click', function(){
    $('.ui.basic.modal.RMVS').modal('show');
  })

  document.getElementById("editBtn").addEventListener('click', function(){
    launchNSModal(true);
  })

  document.getElementById("nsFile").addEventListener('change', function(){
    if(this.files && this.files[0]){
          var reader = new FileReader();

          reader.onload = function(e){
              document.getElementById("nsImage").setAttribute("src", e.target.result);
          }

          reader.readAsDataURL(this.files[0]);
      }
  })

  drawList();

}, false);

function launchNSModal(edit){
  $('.ui.basic.modal.NS').modal('show');

  document.getElementById("nsEditSave").value = edit;
  $('#rangestart').calendar({
    type: 'date',
    endCalendar: $('#rangeend')
  });
  $('#rangeend').calendar({
    type: 'date',
    startCalendar: $('#rangestart')
  });

  if(edit){
    var campaignID = document.getElementById("itemID").value;
      document.getElementById("nsTitle").value = json[campaignID].title;
      $('#nsCategory').dropdown('set selected',json[campaignID].category);
      //document.getElementById("nsCategory").value = json[campaignID].category;
      document.getElementById("nsDescription").value = json[campaignID].description;
      document.getElementById("nsImage").src = json[campaignID].image;

      $("#rangestart").calendar('set date',json[campaignID]["startDate"].replace(/\//g,"-"));
      $("#rangeend").calendar('set date',json[campaignID]["endDate"].replace(/\//g,"-"));
  } else {
    document.getElementById("nsTitle").value = "";
    $('#nsCategory').dropdown('set selected','');
    //document.getElementById("nsCategory").value = "";
    document.getElementById("nsDescription").value = "";
    document.getElementById("nsImage").src = "";
    $("#rangestart").calendar('set date',"");
    $("#rangeend").calendar('set date',"");
  }
}

function nsModalSave(){
  if(document.getElementById("nsTitle").value == ""){
    alert("Title field is empty!")
    return;
  }
  //if(document.getElementById("nsCategory").value == ""){
  if($('#nsCategory').dropdown('get value') == ""){
    alert("Category field is empty!")
    return;
  }
  if(document.getElementById("nsDescription").value == ""){
    alert("Description field is empty!")
    return;
  }
  if(document.getElementById("nsImage").getAttribute("src") == ""){
    alert("Image field is empty!")
    return;
  }
  if($("#rangestart").calendar('get date') == null){
    alert("Date field is empty!")
    return;
  }
  if($("#rangeend").calendar('get date') == null){
    alert("Date field is empty!")
    return;
  }

  var edit = document.getElementById("nsEditSave").value;

  requestData = {};
  if(edit == "true"){
    requestData["requestType"] = "updateCampaign";
    requestData["campaignID"] = document.getElementById("itemID").value;
  } else {
    requestData["requestType"] = "addCampaign";
  }

  var url_string = window.location.href;
  var url = new URL(url_string);
  var authToken = url.searchParams.get("authToken");
  requestData["authToken"] = authToken;

  requestData["title"] = document.getElementById("nsTitle").value;
  requestData["category"] = $('#nsCategory').dropdown('get value');
  requestData["description"] = document.getElementById("nsDescription").value;
  requestData["startDate"] = $("#rangestart").calendar('get date').toISOString().substring(0,10).replace(/-/g,"/");
  requestData["endDate"] = $("#rangeend").calendar('get date').toISOString().substring(0,10).replace(/-/g,"/");
  requestData["image"] = document.getElementById("nsImage").src.split(",")[1];

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
    if(response["success"] == false){
      alert("ERROR: Campaign not registered!")
    } else {
      if(edit  == "true"){
        var campaignID = document.getElementById("itemID").value;
        var tab = document.getElementById(campaignID);
        tab.parentNode.removeChild(tab);

        delete json[campaignID];

        drawElementInList(campaignID);
      } else {
        drawElementInList(response["campaignID"]);
      }

    }
  });
}

function rmvModalYes(){
  var campaignID = document.getElementById("itemID").value;

  requestData = {};
  requestData["requestType"] = "removeCampaign";
  requestData["campaignID"] = campaignID ;

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
    if(response["success"] == false){
      alert("ERROR: Campaign not removed!");
    } else {
      var tab = document.getElementById(campaignID);
      tab.parentNode.removeChild(tab);

      delete json[campaignID];

      document.getElementById("rightTitle").innerHTML = "";
      document.getElementById("rightPeriod").innerHTML = "";
      document.getElementById("rightDescription").innerHTML = "";
      //document.getElementById("rightViewed").innerHTML = "Aceasta promotie a fost vizualizata de " + json[i].viewed + " persoane";
      document.getElementById("rightImage").src ="";
      document.getElementById("itemID").value = "";
      document.getElementById("buttonsDiv").hidden = true;
    }
  });

}

function drawRightSide(campaignID){
  if(json[campaignID] == null)
    return;


  document.getElementById("rightTitle").innerHTML = json[campaignID].title;
  document.getElementById("rightCategory").innerHTML = "Category: " + json[campaignID].category;
  document.getElementById("rightPeriod").innerHTML = json[campaignID].startDate + " - " + json[campaignID].endDate;
  document.getElementById("rightDescription").innerHTML = json[campaignID].description;
  //document.getElementById("rightViewed").innerHTML = "Aceasta promotie a fost vizualizata de " + json[i].viewed + " persoane";
  document.getElementById("rightImage").src = json[campaignID].image;
  document.getElementById("itemID").value = json[campaignID].campaignID;

  document.getElementById("buttonsDiv").hidden = false;

  getStatistics();
}

function drawList(){
  requestData = {};
  requestData["requestType"] = "campaigns";

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
    if(response["campaigns"] != null){
      var campaigns = response["campaigns"].split(",");
      for(var i=0; i<campaigns.length; i++){
        drawElementInList(campaigns[i]);
      }
    }
  });
}

function drawElementInList(campaignID){
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
      var table = document.createElement("table");
      table.setAttribute("class","ui table borderless");
      table.id = response["campaignID"];

      table.innerHTML = "<tr> \
                          <td class='collapsing'> \
                            <img src= 'data:*/*;base64," + response["image"] + "' class='image thumb-im'> \
                          </td> \
                          <td><strong>" + response["title"] + "</strong><br>" + response["startDate"] + " - " + response["endDate"] + "</td> \
                        </tr>"

      document.getElementById("leftSectionDiv").appendChild(table);

      table.addEventListener('mouseover', function() {
        this.classList.replace("borderless","greyBack");
      })
      table.addEventListener('mouseout', function() {
        this.classList.replace("greyBack","borderless");
      })
      table.addEventListener('click', function(){
        drawRightSide(this.id);
      })

      json[response["campaignID"]] = {};
      json[response["campaignID"]]["campaignID"] = response["campaignID"];
      json[response["campaignID"]]["title"] = response["title"];
      json[response["campaignID"]]["category"] = response["category"];
      json[response["campaignID"]]["description"] = response["description"];
      json[response["campaignID"]]["startDate"] = response["startDate"];
      json[response["campaignID"]]["endDate"] = response["endDate"];
      json[response["campaignID"]]["image"] = "data:*/*;base64," + response["image"];

      drawRightSide(response["campaignID"]);
    }
  });


}

var json = {};

// var json = [
//   {
//     "id":0,
//     "title": "Electric scooter 3500 lei",
//     "im": "scooter.jpg",
//     "period": "01.01.2020 - 29.02.2020",
//     "desc": "SUPERIOR PERFORMANCE - The electric scooter features an upgraded 350 Watt motor, boasting an 18. 6 MPH max speed and a range of 17 miles, tackles steep 15% hills with ease. \
//             ONE-STEP FOLD DESIGN - With 1-second foot-actuated folding, the electric scooter can be quickly collapsed. After folding, the MAX scooter can be carried one-handedly, making it a perfect commute companion.\
//             SAFETY AND COMFORT - Braking is smooth and secure. The excellent braking system makes the brake respond more quickly and improves the safety when in use. The front shock absorber provides maximum rider comfort.\
//             RIDING HANDILY - Simply connect MAX electric scooter via Bluetooth and use the APP to check the scooter before every use, active cruise control and view other riding statistics, available on iOS and Android.\
//             UNIQUE AND HUMANIZE - The adult electric scooter is equipped with wider foot anti-slip pedal for larger feet support, dual headlights for safe night riding, and clear LED display for relieved riding. ",
//     "viewed": 153
//   },{
//     "id":1,
//     "title": "Iphone 11 pro 5500 lei",
//     "im": "iphone.jpg",
//     "period": "01.01.2020 - 15.02.2020",
//     "desc": "Carrier - This phone is locked to simple Mobile from Tracfone, which means this Device can only be used on the Simple Mobile wireless network.\
//             Plans - simple Mobile offers a variety of coverage plans, including 30-day unlimited Talk, text & data. No Activation Fees, No Credit Checks, and no Hassles On a Nationwide Lightning-fast Network. For more information or plan options, please visit the Simple Mobile website.\
//             Activation - You’ll receive a simple Mobile SIM kit with this iPhone. Follow the instructions to get service activated with the simple Mobile plan of your choice.\
//             5. 8-inch Super Retina XDR OLED display\
//             Water and dust resistant (4 meters for up to 30 minutes, IP68)\
//             Triple-camera system with 12MP Ultra wide, wide, and telephoto cameras; night mode, Portrait mode, and 4K video up to 60fps\
//             12MP True Depth front Camera with Portrait mode, 4K video, and slo-mo\
//             Face ID for secure authentication and Apple Pay\
//             A13 Bionic chip with third-generation Neural Engine\
//             Fast Charge with 18W adapter included",
//     "viewed": 271
//   },{
//     "id":2,
//     "im": "samsung.jpg",
//     "title": "Samsung Galaxy Note 10 Plus 5500 lei",
//     "period": "01.01.2020 - 15.02.2020",
//     "desc": "HSDPA 850 / 900 / 1700(AWS) / 1900 / 2100 - 4G LTE: band 1(2100), 2(1900), 3(1800), 4(1700/2100), 5(850), 7(2600), 8(900), 17(700), 20(800), 28(700) B38(2600), B39(1900), B40(2300), B41(2500) > (ensure to check compatibility with your carrier before purchase) - Hybrid Dual SIM (Nano-SIM, dual stand-by)\
//             6.8 Quad HD+ Dynamic AMOLED 1440 x 3040 pixels, 19:9 ratio (~498 ppi density), Corning Gorilla Glass - IP68 dust/water proof - Fingerprint (under display, ultrasonic) - Stylus Pen (Bluetooth integration) - Dolby Atmos/AKG sound - Wireless charging: 20W (Charging Pad required*) - Fast battery charging - Reverse wireless charging between devices - Face recognition (unlock)\
//             Quad Camera (Rear):12.2MP + 12MP telephoto + 16MP ultrawide (f/2.2, 12mm 1.0µm) + 0.3MP TOF - Front Camera:10 megapixels f/2.2 (aperture) - Video:4K UHD (3840x2160 pixels) 60 fps - OIS (2-axis): Optical stabilization - HDR photo/video on both cameras\
//             256GB + 12GB RAM - microSD, up to 1 TB - 64bit Samsung Exynos 9 Octa-core 9825 (7nm) - Non-removable Li-Ion 4300 mAh battery\
//             Unlocked cellphones are compatible with most of the GSM carriers ( Like T-Mobile or AT&T ) but please be aware that are not compatible with CDMA carriers ( Like Sprint or Verizon Wireless for example )",
//     "viewed": 280
//   },{
//     "id":3,
//     "title": "LG Smart TV 4000 lei",
//     "im": "lg.jpg",
//     "period": "01.01.2020 - 15.02.2020",
//     "desc": "QUAD CORE PROCESSOR: improves images, action and color, reducing distracing video noise and motion blur, enhancing sharpness and portraying accurate colors\
//             LG THINQ, ALEXA AND GOOGLE ASSISTANT: LG TVs are the first to integrate both the Google Assistant and Alexa with LG ThinQ AI technology\
//             4K ACTIVE HDR: 4K resolution TV detail and more lifelike picture with scene by scene picture adjustment; Supports HDR formats, HDR10 and HLG\
//             IPS 4K: wide viewing angle keeps you enthralled with spectacular realism displaying almost 100 percent color accuracy, even from a 60 degree angle\
//             ACCESS AI BASED ENTERTAINMENT WITH WEBOS: There's a lot to love about LG’s award winning, easy to use platform\
//             AIRPLAY 2: with AirPlay 2 built in, you can effortlessly cast anthing from Apple iPhone or laptop to your LG AI TV",
//     "viewed": 321
//   },{
//     "id":4,
//     "title": "Harry Potter - book 50 lei",
//     "im": "hp.jpg",
//     "period": "01.01.2020 - 20.03.2020",
//     "desc": "Say you've spent the first 10 years of your life sleeping under the stairs of a family who loathes you. Then, in an absurd, magical twist of fate you find yourself surrounded by wizards, a caged snowy owl, a phoenix-feather wand, and jellybeans that come in every flavor, including strawberry, curry, grass, and sardine. Not only that, but you discover that you are a wizard yourself! This is exactly what happens to young Harry Potter in J.K. Rowling's enchanting, funny debut novel,\
//             Harry Potter and the Sorcerer's Stone. In the nonmagic human world--the world of Muggles --Harry is a nobody, treated like dirt by the aunt and uncle who begrudgingly inherited him when his parents were killed by the evil Voldemort. But in the world of wizards, small, skinny Harry is famous as a survivor of the wizard who tried to kill him. He is left only with a lightning-bolt scar on his forehead, curiously refined sensibilities, and a host of mysterious powers to remind him that\
//             he's quite, yes, altogether different from his aunt, uncle, and spoiled, piglike cousin Dudley. \
//             A mysterious letter, delivered by the friendly giant Hagrid, wrenches Harry from his dreary, Muggle-ridden existence: We are pleased to inform you that you have been accepted at Hogwarts School of Witchcraft and Wizardry. Of course, Uncle Vernon yells most unpleasantly, I AM NOT PAYING FOR SOME CRACKPOT OLD FOOL TO TEACH HIM MAGIC TRICKS! Soon enough, however, Harry finds himself at Hogwarts with his owl Hedwig... and that's where the real adventure--humorous, haunting, and \
//             suspenseful--begins. Harry Potter and the Sorcerer's Stone, first published in England as Harry Potter and the Philosopher's Stone, continues to win major awards in England. So far it has won the National Book Award, the Smarties Prize, the Children's Book Award, and is short-listed for the Carnegie Medal, the U.K. version of the Newbery Medal. This magical, gripping, brilliant book--a future classic to be sure--will leave kids clamoring for Harry Potter and the Chamber of Secrets and Harry Potter and the Prisoner of Azkaban. (Ages 8 to 13) --Karin Snelson ",
//     "viewed": 131
//   }
// ];
