document.addEventListener('DOMContentLoaded',function(){
    $('.ui.dropdown').dropdown();

    requestParkingData();
    setDaysTable();

    document.getElementById("day1").addEventListener("mouseenter", function() {toggleColorEnter("day1");}, false);
    document.getElementById("day1").addEventListener("mouseleave", function() {toggleColorLeave("day1");}, false);
    document.getElementById("day1").addEventListener("click", function() {toggleColorClick("day1",6);}, false);

    document.getElementById("day2").addEventListener("mouseenter", function() {toggleColorEnter("day2");}, false);
    document.getElementById("day2").addEventListener("mouseleave", function() {toggleColorLeave("day2");}, false);
    document.getElementById("day2").addEventListener("click", function() {toggleColorClick("day2",5);}, false);

    document.getElementById("day3").addEventListener("mouseenter", function() {toggleColorEnter("day3");}, false);
    document.getElementById("day3").addEventListener("mouseleave", function() {toggleColorLeave("day3");}, false);
    document.getElementById("day3").addEventListener("click", function() {toggleColorClick("day3",4);}, false);

    document.getElementById("day4").addEventListener("mouseenter", function() {toggleColorEnter("day4");}, false);
    document.getElementById("day4").addEventListener("mouseleave", function() {toggleColorLeave("day4");}, false);
    document.getElementById("day4").addEventListener("click", function() {toggleColorClick("day4",3);}, false);

    document.getElementById("day5").addEventListener("mouseenter", function() {toggleColorEnter("day5");}, false);
    document.getElementById("day5").addEventListener("mouseleave", function() {toggleColorLeave("day5");}, false);
    document.getElementById("day5").addEventListener("click", function() {toggleColorClick("day5",2);}, false);

    document.getElementById("day6").addEventListener("mouseenter", function() {toggleColorEnter("day6");}, false);
    document.getElementById("day6").addEventListener("mouseleave", function() {toggleColorLeave("day6");}, false);
    document.getElementById("day6").addEventListener("click", function() {toggleColorClick("day6",1);}, false);

    document.getElementById("day7").addEventListener("mouseenter", function() {toggleColorEnter("day7");}, false);
    document.getElementById("day7").addEventListener("mouseleave", function() {toggleColorLeave("day7");}, false);
    document.getElementById("day7").addEventListener("click", function() {toggleColorClick("day7",0);}, false);

}, false);

var malls = {}

function toggleColorEnter(elemID){
    if(document.getElementById(elemID).style.backgroundColor != "gray"){
        document.getElementById(elemID).style.backgroundColor = "darkgray";
    }
}
function toggleColorLeave(elemID){
    if(document.getElementById(elemID).style.backgroundColor != "gray"){
        document.getElementById(elemID).style.backgroundColor = "lightgray";
    }
}
function toggleColorClick(elemID, daysNum) {
    if(document.getElementById(elemID).style.backgroundColor != "gray"){
        for(var j=1; j<=7; j++){
            document.getElementById("day" + j).style.backgroundColor = "lightgray";
        }
        document.getElementById(elemID).style.backgroundColor = "gray";

        var d = new Date();
        d.setDate(d.getDate()-daysNum);
        var dateStr = d.toISOString().substring(0,10);

        computeChartDatasets(dateStr, document.getElementById("mallIndexInput").value, document.getElementById("parkingSpaceIndexInput").value);
    }
}

function setDaysTable() {
    var d = new Date();
    d.setDate(d.getDate()-6);
    for(var i=1; i<=7; i++){
        var inH = "";
        if(d.getDay() == 1)
            inH = inH + "Monday <br>"
        else if(d.getDay() == 2)
            inH = inH + "Tuesday <br>"
        else if(d.getDay() == 3)
            inH = inH + "Wednesday <br>"
        else if(d.getDay() == 4)
            inH = inH + "Thursday <br>"
        else if(d.getDay() == 5)
            inH = inH + "Friday <br>"
        else if(d.getDay() == 6)
            inH = inH + "Saturday <br>"
        else if(d.getDay() == 0)
            inH = inH + "Sunday <br>"

        inH = inH + d.toISOString().substring(0,10);
        document.getElementById("day"+i).innerHTML=inH;
        d.setDate(d.getDate()+1);
    }
}



function requestParkingData() {
    var request = $.ajax({
        xhrFields: {
            withCredentials: true
        },
        headers: {
            'Authorization': 'Basic ' + btoa('admin:solomon')
        },
        type: "get",
        url: "/mobileApp/getMalls"
    });

    request.fail(function(jqXHR,textStatus,errorThrown){
        alert(jqXHR.responseText);
    });

    request.done(function(response, textStatus, jqXHR){
        var logOutRequest = $.ajax({
            type: "get",
            url: "/solomon/logout"
        });
        logOutRequest.fail(function(jqXHR,textStatus,errorThrown){  });
        logOutRequest.done(function(response, textStatus, jqXHR){  });

        malls = response;
        for(var i=0; i<response.length; i++){
            var mall =  document.createElement("div");
            mall.id = response[i]["id"];
            mall.setAttribute("class","item");
            mall.setAttribute("data-value",response[i]["id"]);
            mall.innerText = response[i]["name"];

            document.getElementById("mallSelectMenu").appendChild(mall);
        }
    });
}

function displayMall(){
    document.getElementById("map-container").innerHTML = "<div id=\"map-area\"></div>";
    var mallId = document.getElementById("mallSelection").value;
    var index = 0;
    for(var i=0; i<malls.length; i++){
        if(malls[i]["id"] == mallId){
            index = i;
            break;
        }
    }

    var mymap = L.map('map-area').setView([malls[index]["latitude"], malls[index]["longitude"]], 16);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        maxZoom: 20,
        maxNativeZoom:18
    }).addTo(mymap);

    mymap.dragging.disable();
    mymap.scrollWheelZoom.disable();
    mymap.touchZoom.disable();
    mymap.doubleClickZoom.disable();

    if(malls[index]["parkingSpaces"].length == 0){
        var circle = L.circle([malls[index]["latitude"], malls[index]["longitude"]], {
            color: '#892ae8',
            fillColor: '#a86de3',
            fillOpacity: 0.5,
            radius: 30
        }).addTo(mymap);
        circle.bindTooltip(malls[index]["name"]).openTooltip();
    } else {
        for(var i=0; i<malls[index]["parkingSpaces"].length; i++){
            if(malls[index]["parkingSpaces"][i]["parkingData"][malls[index]["parkingSpaces"][i]["parkingData"].length-1].status == "FREE") {
            //if(Math.floor(Math.random() * 10) >= 5) {
                var color = 'green';
                var fillColor = '#44fc69';
            } else {
                var color = 'red';
                var fillColor = '#f03';
            }

            var circle = L.circle([malls[index]["parkingSpaces"][i]["latitude"],malls[index]["parkingSpaces"][i]["longitude"]], {
                color: color,
                fillColor: fillColor,
                fillOpacity: 0.5,
                radius: 1,
                mallIndex: index,
                parkingSpaceIndex: i
            }).addTo(mymap).on("click",function(e) {
                displayParkingSpace(this.options.mallIndex, this.options.parkingSpaceIndex);
            });

            if(malls[index]["id"] == 1){
                var imageUrl = '/webResources/images/parkingLotBeia.png',
                    imageBounds = [[44.39573,26.10251], [44.39548,26.102925]];
                L.imageOverlay(imageUrl, imageBounds).addTo(mymap);
            }
        }
    }

    document.getElementById("statistics-container").hidden = true;
}

function displayParkingSpace(mallIndex, psIndex){
    if(document.getElementById("statistics-container").hidden == false
        && document.getElementById("mallIndexInput").value == mallIndex
            && document.getElementById("parkingSpaceIndexInput").value == psIndex){
        document.getElementById("statistics-container").hidden = true;
        return;
    }

    document.getElementById("statistics-container").hidden = false;
    document.getElementById("mallIndexInput").value = mallIndex;
    document.getElementById("parkingSpaceIndexInput").value = psIndex;

    document.getElementById("day7").style.backgroundColor = "lightgray";
    document.getElementById("day7").click();
}

function computeChartDatasets(dateStr, mall, parkSpace){
    var psData = malls[mall]["parkingSpaces"][parkSpace]["parkingData"];
    var ts = []
    for(var i=0; i< psData.length; i++){
        if(psData[i].date.substr(0,10) == dateStr){
            var tsItem = {}
            if(psData[i].status == "FREE")
                tsItem["status"] = 1;
            else
                tsItem["status"] = 0;
            tsItem["time"] = psData[i].date.substr(11,5);

            ts.push(tsItem);
        }
    }

    // sortez ts crescator in functie de time
    ts.sort((a,b) => (a.time > b.time) ? 1 : ((b.time > a.time) ? -1 : 0));

    var labels = [];
    var free = [];
    var occupied = [];
    var tsIndex = 0;
    var status = -1;

    var hours=0;
    var minutes=0;
    while(hours<24) {
        var lf = 0;
        var loc = 0;
        var timeLabel = ("0" + hours).slice(-2) + ":" + ("0" + minutes).slice(-2);

        if(ts.length > 0 && timeLabel > ts[tsIndex].time) {
            while(1){
                if(timeLabel < ts[tsIndex].time)
                    break;
                else
                    status = ts[tsIndex].status

                if(tsIndex == ts.length -1){
                    break;
                }
                tsIndex = tsIndex + 1;
            }
        }
        if(status == -1){
            lf = 0;
            loc = 0;
        } else {
            lf = 0 + parseInt(status);
            loc = 1 - parseInt(status);
        }

        var d = new Date();
        if(dateStr == d.toISOString().substring(0,10) && timeLabel> (("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2))){
            lf = 0;
            loc = 0;
        }

        labels.push(timeLabel);
        free.push(lf);
        occupied.push(loc);

        minutes=minutes+5;
        if(minutes==60) {
            hours = hours+1;
            minutes = 0;
        }
    }

    generateChart(labels, free, occupied);
}

function generateChart(labels, free, occupied){
    if(window.myChart)
        window.myChart.destroy();

    var ctx = document.getElementById('canvas').getContext('2d');
    var config = {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                pointRadius: 0,
                barThickness: 'flex',
                barPercentage: 1,
                categoryPercentage:1,
                data: occupied,
                backgroundColor: 'red',
                fill: true
            },{
                pointRadius: 0,
                barThickness: 'flex',
                barPercentage: 1,
                categoryPercentage:1,
                data: free,
                backgroundColor: 'green',
                fill: true
            }]
        },
        options: {
            responsive: true,
            legend: { display: false },
            scales: {
                yAxes: [{
                    display: false,
                    stacked:false,
                    ticks:{
                        beginAtZero:true
                    }
                }],
                xAxes: [{
                    stacked:true
                }]
            }
        }
    };


    window.myChart = new Chart(ctx, config);
}