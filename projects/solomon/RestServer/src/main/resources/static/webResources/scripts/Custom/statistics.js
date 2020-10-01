
var perDayViewsChart;
var ageDistributionChart;
var genderDistributionChart;

        function getUsersStatistics(startDate, endDate, json){
            var users = [];
            var males = 0;
            var females = 0;
            var ages = [0, 0, 0, 0, 0];

            for(x in json["campaignViews"]){
                if(!users.includes(json["campaignViews"][x]["idUser"])){
                    users.push(json["campaignViews"][x]["idUser"]);

                    if(json["campaignViews"][x]["gender"] == "Male"){
                        males ++;
                    } else {
                        females ++;
                    }

                    if(json["campaignViews"][x]["age"] <= 20){
                        ages[0] ++;
                    } else if (json["campaignViews"][x]["age"] <= 40) {
                        ages[1] ++;
                    } else if (json["campaignViews"][x]["age"] <= 60) {
                        ages[2] ++;
                    } else if (json["campaignViews"][x]["age"] <= 80) {
                        ages[3] ++;
                    } else {
                        ages[4] ++;
                    }
                }
            }


			var cell = document.getElementById("viewsNumCell");
            // Numar de zile
            var daysNum = Math.ceil((endDate - startDate)/(1000*60*60*24));

      cell.innerHTML = "<p>Number of campaign days: <span class='viewStats'>" + daysNum + "</span></p>";
			cell.innerHTML = cell.innerHTML + "<p>Number of users that accesed the campaign: <span class='viewStats'>" + users.length + "</span></p>";
			cell.innerHTML = cell.innerHTML + "<p>Number of total views: <span class='viewStats'>" + json["campaignViews"].length + "</span></p>";


			drawAgeDistributionChart(ages);
			drawGenderDistributionChart(males, females);

        }

        function drawAgeDistributionChart(ages){
        	var ageDistributionCtx = document.getElementById('ageDistributionCanvas').getContext('2d');
          if (ageDistributionChart) {
            ageDistributionChart.destroy();
          }
				  ageDistributionChart = new Chart(ageDistributionCtx, {
								type: 'horizontalBar',
								data: {
										labels: ['0-20', '21-40', '41-60', '61-80', '> 80'],
										datasets: [{
											backgroundColor: ["#f6cfca","#e4c5ff","#7de589","#6598c6","#cacaca"],
											borderColor: ["#b86b77","#b967ff","#4fa73b","#005b96","#868686"],
											borderWidth: 1,
											data: ages
										}]

								},
								options: {
									// Elements options apply to all of the options unless overridden in a dataset
									// In this case, we are setting the border of each horizontal bar to be 2px wide
									elements: {
										rectangle: {
											borderWidth: 2,
										}
									},
									responsive: true,
									legend: {
										display: false,
									},
									title: {
										display: true,
										text: 'Age Distribution'
									},
									scales: {
										xAxes: [{
											display: true,
											ticks: {
												beginAtZero: true
											}
										}]
									}
								}
				});
        }


        function drawGenderDistributionChart(males, females){
			var genderDistributionCtx = document.getElementById('genderDistributionCanvas').getContext('2d');
      if (genderDistributionChart) {
        genderDistributionChart.destroy();
      }
						genderDistributionChart = new Chart(genderDistributionCtx, {
										type: 'pie',
										data: {
												labels: ['Females', 'Males'],
												datasets: [{
													backgroundColor: ["#e4c5ff","#6598c6"],
													borderColor: ["#b967ff","#005b96"],
													borderWidth: 1,
													data: [females, males]
												}]

										},
										options: {
											responsive: true,
											legend: {
												display: false,
											},
											title: {
												display: true,
												text: 'Gender Distribution'
											}
										}
			});

        }


        function getViewsByDay(startDate, endDate, json){

        	var weekDays = ["SUN","MON","TWE","WEN","THU","FRI","SAT"];
        	var totalViews = 0;
            var dates = new Array();
            var currDate = startDate;


			while(smallEqualDates(currDate, endDate)){
				var item = {};
				var dayViews = 0;
                var ages = [0, 0, 0, 0, 0];

				var lbl = [("0" + currDate.getDate()).slice(-2) + "/" + ("0" + (currDate.getMonth() + 1)).slice(-2),weekDays[currDate.getDay()]];
                item["label"] = lbl;

				for(x in json["campaignViews"]){
					if(sameDayDates(currDate,new Date(json["campaignViews"][x]["viewDate"]))){
						totalViews ++;
						dayViews ++;

                        if(json["campaignViews"][x]["age"] <= 20){
                            ages[0] ++;
                        } else if (json["campaignViews"][x]["age"] <= 40) {
                            ages[1] ++;
                        } else if (json["campaignViews"][x]["age"] <= 60) {
                            ages[2] ++;
                        } else if (json["campaignViews"][x]["age"] <= 80) {
                            ages[3] ++;
                        } else {
                            ages[4] ++;
                        }
					}
				}

				item["totalViews"] = totalViews;
				item["dayViews"] = dayViews;
                item["ages"] = ages;
				dates.push(item);

				currDate.setDate(currDate.getDate() + 1);
			}

            //debugger;
			drawPerDayViewsChart(dates);

        }

        function sameDayDates(first, second){
        	return first.getFullYear() === second.getFullYear() &&
    				first.getMonth() === second.getMonth() &&
    					first.getDate() === second.getDate();
        }

        function smallEqualDates(first, second){
			if(first.getFullYear() < second.getFullYear()){
				return true;
			} else if(first.getFullYear() > second.getFullYear()){
					return false;
			}

			if(first.getMonth() < second.getMonth()){
				return true;
			} else if(first.getMonth() > second.getMonth()) {
					return false;
			}

			if(first.getDate() <= second.getDate()){
				return true;
			} else {
				return false;
			}

        }


        function drawPerDayViewsChart(dates){

            var labels = [];
            var days = [];
            var a020 = [];
            var a2140 = [];
            var a4160 = [];
            var a6180 = [];
            var a80 = [];
            for(x in dates){
                labels.push(dates[x]["label"]);
                days.push(dates[x]["dayViews"]);
                a020.push(dates[x]["ages"][0]);
                a2140.push(dates[x]["ages"][1]);
                a4160.push(dates[x]["ages"][2]);
                a6180.push(dates[x]["ages"][3]);
                a80.push(dates[x]["ages"][4]);
            }
            //debugger;
        	var perDayViewsCtx = document.getElementById('perDayViewsCanvas').getContext('2d');

          if (perDayViewsChart) {
            perDayViewsChart.destroy();
          }
          perDayViewsChart = new Chart(perDayViewsCtx, {
							type: 'bar',
							data: {
									labels: labels,
									datasets: [{
                                        label: "0-20",
                                        backgroundColor: "#e4c5ff",
										borderColor: "#b967ff",
										borderWidth: 1,
                                        stack: "Stack0",
										data: a020
                                    },{
                                        label: "21-40",
                                        backgroundColor: "#7de589",
										borderColor: "#4fa73b",
										borderWidth: 1,
                                        stack: "Stack0",
										data: a2140
                                    },{
                                        label: "41-60",
                                        backgroundColor: "#6598c6",
										borderColor: "#005b96",
										borderWidth: 1,
                                        stack: "Stack0",
										data: a4160
                                    },{
                                        label: "61-80",
                                        backgroundColor: "#cacaca",
										borderColor: "#868686",
										borderWidth: 1,
                                        stack: "Stack0",
										data: a6180
                                    },{
                                        label: "> 80",
                                        backgroundColor: "#d7ffd1",
										borderColor: "#a7dfa5",
										borderWidth: 1,
                                        stack: "Stack0",
										data: a80
                                    },{
                                        label: "Total",
										backgroundColor: "#f6cfca",
										borderColor: "#b86b77",
										borderWidth: 1,
                                        stack: "Stack1",
										data: days
									}]

							},
							options: {
								// Elements options apply to all of the options unless overridden in a dataset
								// In this case, we are setting the border of each horizontal bar to be 2px wide
								elements: {
									rectangle: {
										borderWidth: 2,
									}
								},
								responsive: true,
								legend: {
									display: true,
								},
								title: {
									display: true,
									text: 'Views per Day '
								},
                                tooltips: {
            						mode: 'index',
            						intersect: false
            					},
								scales: {
									xAxes: [{
            							stacked: true,
                                        display: true,
										ticks: {
                                            fontSize: 10,
											beginAtZero: true,
                                            autoSkip: true,
										}
            						}],
            						yAxes: [{
            							stacked: true,
                                        display: true,
										ticks: {
											beginAtZero: true
										}
            						}]
								}
							}
			});

}


function getStatistics(){
        var str = `{
		            "success": true,
		            "campaignViews": [
		                { "idCampaign": "20", "idUser": 1, "gender": "Female", "age": 23, "viewDate": "2020/04/07 19:43:00" },
		                { "idCampaign": "20", "idUser": 2, "gender": "Female", "age": 40, "viewDate": "2020/04/10 20:29:00" },
		                { "idCampaign": "20", "idUser": 3, "gender": "Female", "age": 28, "viewDate": "2020/04/01 19:27:00" },
		                { "idCampaign": "20", "idUser": 4, "gender": "Female", "age": 40, "viewDate": "2020/04/22 18:08:00" },
		                { "idCampaign": "20", "idUser": 5, "gender": "Female", "age": 79, "viewDate": "2020/04/20 22:26:00" },
		                { "idCampaign": "20", "idUser": 6, "gender": "Female", "age": 14, "viewDate": "2020/04/02 19:22:00" },
		                { "idCampaign": "20", "idUser": 7, "gender": "Female", "age": 11, "viewDate": "2020/04/08 14:22:00" },
		                { "idCampaign": "20", "idUser": 8, "gender": "Female", "age": 40, "viewDate": "2020/04/18 21:55:00" },
		                { "idCampaign": "20", "idUser": 9, "gender": "Female", "age": 54, "viewDate": "2020/04/04 14:14:00" },
		                { "idCampaign": "20", "idUser": 10, "gender": "Female", "age": 51, "viewDate": "2020/04/16 17:24:00" },
		                { "idCampaign": "20", "idUser": 11, "gender": "Female", "age": 79, "viewDate": "2020/04/17 16:01:00" },
		                { "idCampaign": "20", "idUser": 12, "gender": "Female", "age": 14, "viewDate": "2020/04/03 20:53:00" },
		                { "idCampaign": "20", "idUser": 13, "gender": "Female", "age": 55, "viewDate": "2020/04/06 16:13:00" },
		                { "idCampaign": "20", "idUser": 14, "gender": "Female", "age": 37, "viewDate": "2020/04/01 14:06:00" },
		                { "idCampaign": "20", "idUser": 15, "gender": "Female", "age": 59, "viewDate": "2020/04/21 10:40:00" },
		                { "idCampaign": "20", "idUser": 16, "gender": "Female", "age": 43, "viewDate": "2020/04/22 12:28:00" },
		                { "idCampaign": "20", "idUser": 17, "gender": "Female", "age": 70, "viewDate": "2020/04/11 15:45:00" },
		                { "idCampaign": "20", "idUser": 18, "gender": "Female", "age": 71, "viewDate": "2020/04/02 17:54:00" },
		                { "idCampaign": "20", "idUser": 19, "gender": "Female", "age": 38, "viewDate": "2020/04/21 14:38:00" },
		                { "idCampaign": "20", "idUser": 20, "gender": "Male", "age": 88, "viewDate": "2020/04/19 16:51:00" },
		                { "idCampaign": "20", "idUser": 21, "gender": "Male", "age": 31, "viewDate": "2020/04/16 20:54:00" },
		                { "idCampaign": "20", "idUser": 22, "gender": "Male", "age": 31, "viewDate": "2020/04/07 20:07:00" },
		                { "idCampaign": "20", "idUser": 22, "gender": "Male", "age": 31, "viewDate": "2020/04/10 17:40:00" },
		                { "idCampaign": "20", "idUser": 23, "gender": "Male", "age": 37, "viewDate": "2020/04/11 13:49:00" },
		                { "idCampaign": "20", "idUser": 24, "gender": "Male", "age": 35, "viewDate": "2020/04/10 10:51:00" },
		                { "idCampaign": "20", "idUser": 24, "gender": "Male", "age": 35, "viewDate": "2020/04/12 19:32:00" },
		                { "idCampaign": "20", "idUser": 25, "gender": "Male", "age": 84, "viewDate": "2020/04/19 10:35:00" },
		                { "idCampaign": "20", "idUser": 26, "gender": "Male", "age": 76, "viewDate": "2020/04/04 14:24:00" },
		                { "idCampaign": "20", "idUser": 26, "gender": "Male", "age": 76, "viewDate": "2020/04/08 16:38:00" },
		                { "idCampaign": "20", "idUser": 26, "gender": "Male", "age": 76, "viewDate": "2020/04/09 22:34:00" },
		                { "idCampaign": "20", "idUser": 27, "gender": "Male", "age": 23, "viewDate": "2020/04/16 17:04:00" },
		                { "idCampaign": "20", "idUser": 28, "gender": "Male", "age": 77, "viewDate": "2020/04/04 20:08:00" },
		                { "idCampaign": "20", "idUser": 28, "gender": "Male", "age": 42, "viewDate": "2020/04/17 14:04:00" },
		                { "idCampaign": "20", "idUser": 28, "gender": "Male", "age": 42, "viewDate": "2020/04/23 22:35:00" },
		                { "idCampaign": "20", "idUser": 29, "gender": "Male", "age": 57, "viewDate": "2020/04/22 11:51:00" },
		                { "idCampaign": "20", "idUser": 30, "gender": "Male", "age": 40, "viewDate": "2020/04/19 22:55:00" },
		                { "idCampaign": "20", "idUser": 30, "gender": "Male", "age": 40, "viewDate": "2020/04/19 11:34:00" },
		                { "idCampaign": "20", "idUser": 30, "gender": "Male", "age": 40, "viewDate": "2020/04/13 12:01:00" },
		                { "idCampaign": "20", "idUser": 30, "gender": "Male", "age": 40, "viewDate": "2020/04/22 20:09:00" },
		                { "idCampaign": "20", "idUser": 32, "gender": "Male", "age": 21, "viewDate": "2020/04/17 13:51:00" }
		            ]
		        }`;

    var noViewsStr = `{
      "success": true,
      "campaignViews": null
    }`;

    requestData = {};
    requestData["requestType"] = "getViewStats";
    requestData["campaignID"] = document.getElementById("itemID").value;

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
          document.getElementById("viewStatisticsTable").hidden = true;
          return;
      }

      if(response["campaignViews"] != null){
        document.getElementById("viewStatisticsTable").hidden = false;
        getUsersStatistics(new Date(json[document.getElementById("itemID").value]["startDate"]), new Date(),response);
        getViewsByDay(new Date(json[document.getElementById("itemID").value]["startDate"]), new Date(),response);
      } else {
        document.getElementById("viewStatisticsTable").hidden = true;
      }
    });




}
