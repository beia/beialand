

function startComparing(){
    var sel1 = $('#select_scenario_1')[0].value;
    var sel2 = $('#select_scenario_2')[0].value;

    if(sel1 == "" || sel2 == ""){
      alert("Two scenarios must be selected!")
      return;
    }

    window.location.href = "/comparePage?scenarioID_1=" + sel1 +
                                      "&scenarioID_2=" + sel2;
}
