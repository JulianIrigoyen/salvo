var app = new Vue ({
    el:'#app',
    data:{
       games:[],
       player: [],
       stats:[]

    }
  })

//created by fx proacessData() with playerStat Obj
  var playerList = [];

// AJAX request to load Game data to be displayed in games.html
$( //Document.ready
    $.ajax({
        url: "/api/games",
        dataType:"json",
        success:function(data){console.log(data);}
    })
    .done(function(data){
        
        app.games = data.games;
        app.player = data.player;
        
        // Works with api data -> Fixes dates / calculates Player statistics for leaderboard
        processData();
        app.stats = playerList; 
        
        //Display functions
        hideBtns();  
    })
)

//All functions

function processData() {
    app.games.forEach(function(game){
       
    //Format Dates 
    var newDate = new Date(game.creationDate).toLocaleString()
    game.creationDate = newDate
    
    //Create playerList 
    game.gamePlayers.forEach(function(gamePlayer){
               
        if (playerList.find(playerInPlayerList => playerInPlayerList.id ==  gamePlayer.player.id) == undefined){
            
            var playerStatObj = new Object
            playerStatObj.id = gamePlayer.player.id
            playerStatObj.email = gamePlayer.player.Email
            playerStatObj.score = 0    
            playerStatObj.won = 0
            playerStatObj.lost = 0
            playerStatObj.tied = 0
            
            playerList.push(playerStatObj);
            }
        //Calculate values    
            calculatePlayerStats(gamePlayer.player.id, gamePlayer.score)
        })
        return playerList;       
    })
};


function calculatePlayerStats(id, score){
    playerList.forEach(function(player){
       if (player.id == id && score != null){
           
           if (score == 1.0){
               player.won ++;           
           }

           else if (score == 0.5){
               player.tied ++;            
           }

           else if (score == 0.0) {
               player.lost ++;
            }
    
        player.score += score;
       }

    })
}


//Handles Player login / registration
$("#submitLoginButton").click(function (e) {
    if ($("#userNameTextBox").val() == "")
    $("#userNamSpan").text("Enter Username");
    else
    $("#userNamSpan").text("");
    if ($("#passwordTextBox").val() == "")
    $("#passwordSpan").text("Enter Password");
    else
    $("#passwordSpan").text("");
    if (($("#userNameTextBox").val() != "") && ($("#passwordTextBox").val() != ""))

    login();
})

$("#submitSignupButton").click(function (e) {
    if ($("#userNameTextBox").val() == "")
    $("#userNamSpan").text("Enter Username");
    else
    $("#userNamSpan").text("");
    if ($("#passwordTextBox").val() == "")
    $("#passwordSpan").text("Enter Password");
    else
    $("#passwordSpan").text("");
    if (($("#userNameTextBox").val() != "") && ($("#passwordTextBox").val() != ""))

    $.post("/api/players", {userName: $("#userNameTextBox").val() , password: $("#passwordTextBox").val()  })
    .done(function(response){     
        login();
    })
    .fail(function(response){
        console.log(response)}
    )
})

//Login function (DRY)
function login (){
    $.post("/api/login", {userName: $("#userNameTextBox").val() , password: $("#passwordTextBox").val()  })
    .done(function(response){
        window.location.reload()
    })
    .fail(function(response){
        console.log(response)}
    )
}

//Handles Player logout
$("#logout-btn").click(function (e){
    $.post("/api/logout")
    .done(function() {
        $("#login-btn").addClass("show");
        $("#logout-btn").addClass("hide");
        window.location.reload()
        console.log("logged out"); 
    })
})


//Handles New Game Creation
$("#create-btn").click(function (e){
    $.post("/api/games")
    .done(function(response){
        console.log(response);
        window.location.href = "/web/game.html?gp=" + response.gpId;
    })
})

 //Join GAME fx
 //receives a string from the JOIN BUTTON clicked,
 //creates a new gameplayer and relocates to appropiate 
 //game window, which is determined by elm.dataset.game
 function joinGame(elm){
    $.post("/api/games/" + elm.dataset.game + "/players")
    .done(function(response){
    window.location.href = "/web/game.html?gp=" + response.gpId;
    })
    
    console.log(elm.dataset.game);
}

//Hides buttons depending on Authentication status
function hideBtns (){
    if (app.player !== "guest"){
        $("#login-btn").addClass("hide");
        $("#logout-btn").addClass("show");
        $("#create-btn").addClass("show");
    }
    else{
        $("#login-btn").addClass("show");
        $("#logout-btn").addClass("hide");  
        $("#create-btn").addClass("hide");

    }
}




