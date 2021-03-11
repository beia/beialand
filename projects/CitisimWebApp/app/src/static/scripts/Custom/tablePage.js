


function addNewBill(scenarioID){
    var name = $('#newBillName')[0].value;
    var base = $('#newBillBaseline')[0].value;
    var current = $('#newBillCurrent')[0].value;

    if(name == "" || base == "" || current == ""){
        alert("All fields are required!!!");
        return;
    }

    var request = $.ajax({
      type: "post",
      url: "/newBill",
      contentType: "application/json",
      data: JSON.stringify({
                             "scenarioID": scenarioID,
                             "BillName": name,
                             "BaselineBill": base,
                             "CurrentBill": current
                           })
    });


    request.fail(function(jqXHR,textStatus,errorThrown){
      alert("The following error occurred: " + errorThrown);
    });

    request.done(function(response, textStatus, jqXHR){
        var row = JSON.parse(response);

        var elem = "<tr id='bill_" + row["id"] + "'>"+
                      "<td id='billActions_" + row["id"] + "'>"+
                          "<i  class='close pointed icon' data-content='Remove Bill' onclick='removeBill("+row["id"]+")'></i>"+
                          "<i  class='edit pointed icon' data-content='Edit Bill' onclick='editBill("+row["id"]+")'></i>"+
                      "</td>"+
                      "<td id='billName_" + row["id"] + "' class='nameCell'>" + row["name"] + "</td>"+
                      "<td id='billBase_" + row["id"] + "'>" + row["base"] + "</td>"+
                      "<td id='billCurr_" + row["id"] + "'>" + row["curr"] + "</td>"+
                      "<td>" + row["savingsMU"] + "</td>"+
                      "<td>" + row["savingsPercent"] + " %</td>"+
                      "<td>" + row["amntRet"] + "</td>"+
                      "<td>" + row["rest"] + "</td>"+
                      "<td>" + row["roi"] + " %</td>"+
                      "<td>" + row["irr"] + " %</td>"+
                      "<td>" + row["npv"] + "</td>"+
                      "<td>" + row["esco"] + "</td>"+
                      "<td>" + row["client"] + "</td>"+
                  "</tr>";

        $('#newBillRow').before(elem);
        $('#newBillName')[0].value="";
        $('#newBillBaseline')[0].value="";
        $('#newBillCurrent')[0].value="";
    });

}

function removeBill(id){
    $('#rmvbID')[0].value = id;
    $('.ui.basic.modal.RMVBILL').modal('show');
}

function rmvbModalYes(){
    id =   $('#rmvbID')[0].value;

    var request = $.ajax({
        type: "post",
        url: "/removeBill",
        contentType: "application/json",
        data: JSON.stringify({ "BillID": id  })
    });


    request.fail(function(jqXHR,textStatus,errorThrown){
        alert("The following error occurred: " + errorThrown);
    });

    request.done(function(response, textStatus, jqXHR){
        $('#bill_'+id)[0].remove();
    });

}

function editBill(id){
    var name = $('#billName_'+id).html();
    var base = $('#billBase_'+id).html();
    var curr = $('#billCurr_'+id).html();

    $('#billActions_'+id).html("<i  class='save icon' data-content='Save Changes' onclick='saveChanges("+id+")'></i>");
    $('i').popup({on: 'hover'});

    // $('#billName_'+id).html("<input id='editBillName_"+id+"' class='billInput' type='text' name='' value='"+name+"'>");
    $('#billBase_'+id).html("<input id='editBillBase_"+id+"' class='billInput' type='number' step='0.01' name='' value='"+base+"'>");
    $('#billCurr_'+id).html("<input id='editBillCurr_"+id+"' class='billInput' type='number' step='0.01' name='' value='"+curr+"'>");
}

function saveChanges(id){
    $('#saveID')[0].value = id;
    $('.ui.basic.modal.SAVE').modal('show');
}

function saveModalYes(){
    id =   $('#saveID')[0].value;
    scenarioID =   $('#saveScenarioID')[0].value;
    //var name = $('#editBillName_'+id)[0].value;
    var name = $('#billName_'+id).html();
    var base = $('#editBillBase_'+id)[0].value;
    var curr = $('#editBillCurr_'+id)[0].value;

    var request = $.ajax({
        type: "post",
        url: "/changeBill",
        contentType: "application/json",
        data: JSON.stringify({
            "billID": id,
            "scenarioID": scenarioID,
            "name": name,
            "base": base,
            "curr": curr
          })
    });


    request.fail(function(jqXHR,textStatus,errorThrown){
        alert("The following error occurred: " + errorThrown);
    });

    request.done(function(response, textStatus, jqXHR){
      var resultSet = JSON.parse(response);
      var elem = "";

      for (var key in resultSet ){
          row = resultSet[key];

          elem = "<td id='billActions_" + row["id"] + "'>"+
                          "<i  class='edit pointed icon' data-content='Edit Bill' onclick='editBill("+row["id"]+")'></i>"+
                      "</td>"+
                      "<td id='billName_" + row["id"] + "' class='nameCell'>" + row["name"] + "</td>"+
                      "<td id='billBase_" + row["id"] + "'>" + row["base"] + "</td>"+
                      "<td id='billCurr_" + row["id"] + "'>" + row["curr"] + "</td>"+
                      "<td>" + row["savingsMU"] + "</td>"+
                      "<td>" + row["savingsPercent"] + " %</td>"+
                      "<td>" + row["amntRet"] + "</td>"+
                      "<td>" + row["rest"] + "</td>"+
                      "<td>" + row["roi"] + " %</td>"+
                      "<td>" + row["irr"] + " %</td>"+
                      "<td>" + row["npv"] + "</td>"+
                      "<td>" + row["esco"] + "</td>"+
                      "<td>" + row["client"] + "</td>";

            $('#bill_'+row["id"]).html(elem);

      }

    });

  }


function displayBaseCurrBillChart(id){
      var request = $.ajax({
          type: "post",
          url: "/getColumns",
          contentType: "application/json",
          data: JSON.stringify({
              "scenarioID": id,
              "cols": "BillName,BaselineBill,CurrentBill"
            })
      });


      request.fail(function(jqXHR,textStatus,errorThrown){
          alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
          var resultSet = JSON.parse(response);

          var labels = [];
          var dataset1 = [];
          var dataset2 = [];

          var elem = "<table class='ui stripped celled selectable inverted table'>" +
                          "<thead align='center'>" +
                              "<tr>" +
                                  "<th>Name</th>" +
                                  "<th>Baseline Bill</th>" +
                                  "<th>Current Bill</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody align='center'>";
          for (var key in resultSet ){
              row = resultSet[key];
              labels.push(row["BillName"]);
              dataset1.push(row["BaselineBill"]);
              dataset2.push(row["CurrentBill"]);
              elem = elem + "<tr>" +
                              "<td>"+row["BillName"]+"</td>" +
                              "<td>"+row["BaselineBill"]+"</td>" +
                              "<td>"+row["CurrentBill"]+"</td>" +
                          "</tr>";
          }
          elem = elem + "</tbody></table>";

          $('#chartTitle').html("Baseline Bill and Current Bill");
          $('#modalTable').html(elem);

          if(window.barChart != null){
            window.barChart.destroy();
          }
          window.barChart = new Chart(barCtx, {
              // The type of chart we want to create
              type: 'bar',

              // The data for our dataset
              data: {
                  labels: labels,
                  datasets: [{
                      label: "Baseline Bill",
                      backgroundColor: 'rgb(255, 99, 132, 0.5)',
                      borderColor: 'rgb(255, 99, 132)',
                      borderWidth: 3,
                      data: dataset1,
                  }, {
                      label: "CurrentBill",
                      backgroundColor: 'rgb(54, 162, 235, 0.5)',
                      borderColor: 'rgb(54, 162, 235)',
                      borderWidth: 3,
                      data: dataset2
                  }]
              },

              // Configuration options go here
              options: {
                  responsive: true,
                  legend: {
                    labels: {
                      fontColor: "white"
                    }
                  },
                  scales: {
                      xAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }],
                      yAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }]
                  }
              }
          });

          $('.ui.basic.modal.CHART').modal('show');
      });


  }


function displaySavingsMUChart(id){
      var request = $.ajax({
          type: "post",
          url: "/getColumns",
          contentType: "application/json",
          data: JSON.stringify({
              "scenarioID": id,
              "cols": "BillName,SavingsMU"
            })
      });


      request.fail(function(jqXHR,textStatus,errorThrown){
          alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
          var resultSet = JSON.parse(response);

          var labels = [];
          var dataset1 = [];
          var dataset2 = [];

          var elem = "<table class='ui stripped celled selectable inverted table'>" +
                          "<thead align='center'>" +
                              "<tr>" +
                                  "<th>Name</th>" +
                                  "<th>Savings Achieved (monetary units)</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody align='center'>";
          for (var key in resultSet ){
              row = resultSet[key];
              labels.push(row["BillName"]);
              dataset1.push(row["SavingsMU"]);
              elem = elem + "<tr>" +
                              "<td>"+row["BillName"]+"</td>" +
                              "<td>"+row["SavingsMU"]+"</td>" +
                          "</tr>";
          }
          elem = elem + "</tbody></table>";

          $('#chartTitle').html("Savings Achieved <br> (monetary units)");
          $('#modalTable').html(elem);

          if(window.barChart != null){
            window.barChart.destroy();
          }
          window.barChart = new Chart(barCtx, {
              // The type of chart we want to create
              type: 'line',

              // The data for our dataset
              data: {
                  labels: labels,
                  datasets: [{
                      label: "Savings",
                      backgroundColor: 'rgb(255, 159, 64, 0.5)',
                      borderColor: 'rgb(255, 159, 64)',
                      borderWidth: 3,
                      fill: "start",
                      data: dataset1,
                  }]
              },

              // Configuration options go here
              options: {
                  responsive: true,
                  legend: {
                    labels: {
                      fontColor: "white"
                    }
                  },
                  elements: {
                    line: {
                      tension: 0
                    }
                  },
                  scales: {
                      xAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }],
                      yAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }]
                  }
              }
          });

          $('.ui.basic.modal.CHART').modal('show');
      });
}

function displaySavingsPercentChart(id){
      var request = $.ajax({
          type: "post",
          url: "/getColumns",
          contentType: "application/json",
          data: JSON.stringify({
              "scenarioID": id,
              "cols": "BillName,SavingsPercent"
            })
      });


      request.fail(function(jqXHR,textStatus,errorThrown){
          alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
          var resultSet = JSON.parse(response);

          var labels = [];
          var dataset1 = [];
          var dataset2 = [];

          var elem = "<table class='ui stripped celled selectable inverted table'>" +
                          "<thead align='center'>" +
                              "<tr>" +
                                  "<th>Name</th>" +
                                  "<th>Savings Achieved (percent)</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody align='center'>";
          for (var key in resultSet ){
              row = resultSet[key];
              labels.push(row["BillName"]);
              dataset1.push(row["SavingsPercent"]);
              elem = elem + "<tr>" +
                              "<td>"+row["BillName"]+"</td>" +
                              "<td>"+row["SavingsPercent"]+"</td>" +
                          "</tr>";
          }
          elem = elem + "</tbody></table>";

          $('#chartTitle').html("Savings Achieved <br> (percent)");
          $('#modalTable').html(elem);

          if(window.barChart != null){
            window.barChart.destroy();
          }
          window.barChart = new Chart(barCtx, {
              // The type of chart we want to create
              type: 'line',

              // The data for our dataset
              data: {
                  labels: labels,
                  datasets: [{
                      label: "Savings",
                      backgroundColor: 'rgb(255, 205, 86, 0.5)',
                      borderColor: 'rgb(255, 205, 86)',
                      borderWidth: 3,
                      fill: "start",
                      data: dataset1,
                  }]
              },

              // Configuration options go here
              options: {
                  responsive: true,
                  legend: {
                    labels: {
                      fontColor: "white"
                    }
                  },
                  elements: {
                    line: {
                      tension: 0
                    }
                  },
                  scales: {
                      xAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }],
                      yAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }]
                  }
              }
          });

          $('.ui.basic.modal.CHART').modal('show');
      });
}



function displayAmountReturnedChart(id){
      var request = $.ajax({
          type: "post",
          url: "/getColumns",
          contentType: "application/json",
          data: JSON.stringify({
              "scenarioID": id,
              "cols": "BillName,AmountReturned,AmountYetToBeReturned"
            })
      });


      request.fail(function(jqXHR,textStatus,errorThrown){
          alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
          var resultSet = JSON.parse(response);

          var labels = [];
          var dataset1 = [];
          var dataset2 = [];

          var elem = "<table class='ui stripped celled selectable inverted table'>" +
                          "<thead align='center'>" +
                              "<tr>" +
                                  "<th>Name</th>" +
                                  "<th>Amount Returned</th>" +
                                  "<th>Amount to be Returned</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody align='center'>";
          for (var key in resultSet ){
              row = resultSet[key];
              labels.push(row["BillName"]);
              dataset1.push(row["AmountReturned"]);
              dataset2.push(row["AmountYetToBeReturned"]);
              elem = elem + "<tr>" +
                              "<td>"+row["BillName"]+"</td>" +
                              "<td>"+row["AmountReturned"]+"</td>" +
                              "<td>"+row["AmountYetToBeReturned"]+"</td>" +
                          "</tr>";
          }
          elem = elem + "</tbody></table>";

          $('#chartTitle').html("The amount returned and the remaining");
          $('#modalTable').html(elem);

          if(window.barChart != null){
            window.barChart.destroy();
          }
          window.barChart = new Chart(barCtx, {
              // The type of chart we want to create
              type: 'line',

              // The data for our dataset
              data: {
                  labels: labels,
                  datasets: [{
                      label: "Returned",
                      backgroundColor: 'rgb(75, 192, 192, 0.5)',
                      borderColor: 'rgb(75, 192, 192)',
                      borderWidth: 3,
                      data: dataset1,
                  },{
                      label: "Remaining",
                      backgroundColor: 'rgb(153, 102, 255, 0.5)',
                      borderColor: 'rgb(153, 102, 255)',
                      borderWidth: 3,
                      fill: "start",
                      data: dataset2,
                  }]
              },

              // Configuration options go here
              options: {
                  responsive: true,
                  legend: {
                    labels: {
                      fontColor: "white"
                    }
                  },
                  elements: {
                    line: {
                      tension: 0
                    }
                  },
                  scales: {
                      xAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }],
                      yAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }]
                  }
              }
          });

          $('.ui.basic.modal.CHART').modal('show');
      });
}







function displayROIChart(id){
      var request = $.ajax({
          type: "post",
          url: "/getColumns",
          contentType: "application/json",
          data: JSON.stringify({
              "scenarioID": id,
              "cols": "BillName,ROI"
            })
      });


      request.fail(function(jqXHR,textStatus,errorThrown){
          alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
          var resultSet = JSON.parse(response);

          var labels = [];
          var dataset1 = [];
          var dataset2 = [];

          var elem = "<table class='ui stripped celled selectable inverted table'>" +
                          "<thead align='center'>" +
                              "<tr>" +
                                  "<th>Name</th>" +
                                  "<th>ROI</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody align='center'>";
          for (var key in resultSet ){
              row = resultSet[key];
              labels.push(row["BillName"]);
              dataset1.push(row["ROI"]);
              elem = elem + "<tr>" +
                              "<td>"+row["BillName"]+"</td>" +
                              "<td>"+row["ROI"]+"</td>" +
                          "</tr>";
          }
          elem = elem + "</tbody></table>";

          $('#chartTitle').html("Return of Investment");
          $('#modalTable').html(elem);

          if(window.barChart != null){
            window.barChart.destroy();
          }
          window.barChart = new Chart(barCtx, {
              // The type of chart we want to create
              type: 'line',

              // The data for our dataset
              data: {
                  labels: labels,
                  datasets: [{
                      label: "ROI",
                      backgroundColor: 'rgb(201, 203, 207, 0.5)',
                      borderColor: 'rgb(201, 203, 207)',
                      borderWidth: 3,
                      fill: "start",
                      data: dataset1,
                  }]
              },

              // Configuration options go here
              options: {
                  responsive: true,
                  legend: {
                    labels: {
                      fontColor: "white"
                    }
                  },
                  elements: {
                    line: {
                      tension: 0
                    }
                  },
                  scales: {
                      xAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }],
                      yAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }]
                  }
              }
          });

          $('.ui.basic.modal.CHART').modal('show');
      });
}








function displayIRRChart(id){
      var request = $.ajax({
          type: "post",
          url: "/getColumns",
          contentType: "application/json",
          data: JSON.stringify({
              "scenarioID": id,
              "cols": "BillName,IRR"
            })
      });


      request.fail(function(jqXHR,textStatus,errorThrown){
          alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
          var resultSet = JSON.parse(response);

          var labels = [];
          var dataset1 = [];
          var dataset2 = [];

          var elem = "<table class='ui stripped celled selectable inverted table'>" +
                          "<thead align='center'>" +
                              "<tr>" +
                                  "<th>Name</th>" +
                                  "<th>IRR</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody align='center'>";
          for (var key in resultSet ){
              row = resultSet[key];
              labels.push(row["BillName"]);
              dataset1.push(row["IRR"]);
              elem = elem + "<tr>" +
                              "<td>"+row["BillName"]+"</td>" +
                              "<td>"+row["IRR"]+"</td>" +
                          "</tr>";
          }
          elem = elem + "</tbody></table>";

          $('#chartTitle').html("Internal Rate of Return");
          $('#modalTable').html(elem);

          if(window.barChart != null){
            window.barChart.destroy();
          }
          window.barChart = new Chart(barCtx, {
              // The type of chart we want to create
              type: 'line',

              // The data for our dataset
              data: {
                  labels: labels,
                  datasets: [{
                      label: "IRR",
                      backgroundColor: 'rgb(255, 99, 132, 0.5)',
                      borderColor: 'rgb(255, 99, 132)',
                      borderWidth: 3,
                      fill: "start",
                      data: dataset1,
                  }]
              },

              // Configuration options go here
              options: {
                  responsive: true,
                  legend: {
                    labels: {
                      fontColor: "white"
                    }
                  },
                  elements: {
                    line: {
                      tension: 0
                    }
                  },
                  scales: {
                      xAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }],
                      yAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }]
                  }
              }
          });

          $('.ui.basic.modal.CHART').modal('show');
      });
}



function displayNPVChart(id){
      var request = $.ajax({
          type: "post",
          url: "/getColumns",
          contentType: "application/json",
          data: JSON.stringify({
              "scenarioID": id,
              "cols": "BillName,NPV"
            })
      });


      request.fail(function(jqXHR,textStatus,errorThrown){
          alert("The following error occurred: " + errorThrown);
      });

      request.done(function(response, textStatus, jqXHR){
          var resultSet = JSON.parse(response);

          var labels = [];
          var dataset1 = [];
          var dataset2 = [];

          var elem = "<table class='ui stripped celled selectable inverted table'>" +
                          "<thead align='center'>" +
                              "<tr>" +
                                  "<th>Name</th>" +
                                  "<th>NPV</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody align='center'>";
          for (var key in resultSet ){
              row = resultSet[key];
              labels.push(row["BillName"]);
              dataset1.push(row["NPV"]);
              elem = elem + "<tr>" +
                              "<td>"+row["BillName"]+"</td>" +
                              "<td>"+row["NPV"]+"</td>" +
                          "</tr>";
          }
          elem = elem + "</tbody></table>";

          $('#chartTitle').html("Net Present Value");
          $('#modalTable').html(elem);

          if(window.barChart != null){
            window.barChart.destroy();
          }
          window.barChart = new Chart(barCtx, {
              // The type of chart we want to create
              type: 'line',

              // The data for our dataset
              data: {
                  labels: labels,
                  datasets: [{
                      label: "NPV",
                      backgroundColor: 'rgb(54, 162, 235, 0.5)',
                      borderColor: 'rgb(54, 162, 235)',
                      borderWidth: 3,
                      fill: "start",
                      data: dataset1,
                  }]
              },

              // Configuration options go here
              options: {
                  responsive: true,
                  legend: {
                    labels: {
                      fontColor: "white"
                    }
                  },
                  elements: {
                    line: {
                      tension: 0
                    }
                  },
                  scales: {
                      xAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }],
                      yAxes: [{
                          ticks: {
                              fontColor: "white",
                              fontSize: 18
                          },
                          gridLines: {
                              color: "grey"
                          }
                      }]
                  }
              }
          });

          $('.ui.basic.modal.CHART').modal('show');
      });
}







function displaySharingsChart(id){
    var request = $.ajax({
        type: "post",
        url: "/getColumns",
        contentType: "application/json",
        data: JSON.stringify({
            "scenarioID": id,
            "cols": "BillName,ESCO,Client"
          })
    });


    request.fail(function(jqXHR,textStatus,errorThrown){
        alert("The following error occurred: " + errorThrown);
    });

    request.done(function(response, textStatus, jqXHR){
        var resultSet = JSON.parse(response);

        var labels = [];
        var dataset1 = [];
        var dataset2 = [];

        var elem = "<table class='ui stripped celled selectable inverted table'>" +
                        "<thead align='center'>" +
                            "<tr>" +
                                "<th>Name</th>" +
                                "<th>ESCO</th>" +
                                "<th>Client</th>" +
                            "</tr>" +
                        "</thead>" +
                        "<tbody align='center'>";
        for (var key in resultSet ){
            row = resultSet[key];
            labels.push(row["BillName"]);
            dataset1.push(row["ESCO"]);
            dataset2.push(row["Client"]);
            elem = elem + "<tr>" +
                            "<td>"+row["BillName"]+"</td>" +
                            "<td>"+row["ESCO"]+"</td>" +
                            "<td>"+row["Client"]+"</td>" +
                        "</tr>";
        }
        elem = elem + "</tbody></table>";

        $('#chartTitle').html("Sharings ESCO and Client");
        $('#modalTable').html(elem);

        if(window.barChart != null){
          window.barChart.destroy();
        }
        window.barChart = new Chart(barCtx, {
            // The type of chart we want to create
            type: 'bar',

            // The data for our dataset
            data: {
                labels: labels,
                datasets: [{
                    label: "Sharings ESCO",
                    backgroundColor: 'rgb(255, 205, 86, 0.5)',
                    borderColor: 'rgb(255, 205, 86)',
                    borderWidth: 3,
                    data: dataset1,
                }, {
                    label: "Sharings Client",
                    backgroundColor: 'rgb(75, 192, 192, 0.5)',
                    borderColor: 'rgb(75, 192, 192)',
                    borderWidth: 3,
                    data: dataset2
                }]
            },

            // Configuration options go here
            options: {
                responsive: true,
                legend: {
                  labels: {
                    fontColor: "white"
                  }
                },
                scales: {
                    xAxes: [{
                        ticks: {
                            fontColor: "white",
                            fontSize: 18
                        },
                        gridLines: {
                            color: "grey"
                        }
                    }],
                    yAxes: [{
                        ticks: {
                            fontColor: "white",
                            fontSize: 18
                        },
                        gridLines: {
                            color: "grey"
                        }
                    }]
                }
            }
        });

        $('.ui.basic.modal.CHART').modal('show');
    });


}
