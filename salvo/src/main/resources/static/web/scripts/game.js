var app = new Vue({
    el: '#app',
    data: {
        player: 0,
        opponent: 0,
        ships: 0
    }

})
//Create grid
function createGrid(options) {
    //Grid initialization
    $('.grid-stack').gridstack(options);
    grid = $('#grid').data('gridstack');
}
// Grid options object==> STATIC
var gridOptionStatic = {
    //11 X 11 grid
    width: 10,
    height: 10,
    //Widget separation
    verticalMargin: 0,
    //cell height
    cellHeight: 45,
    //disable widget resize
    disableResize: true,
    //floating widgets
    float: true,
    //removeTimeout: 100,
    //allow widget to span over more than 1 col
    disableOneColumnMode: true,
    //false allows grid to move, true prevents
    staticGrid: true,
    //allow animations
    animate: true
}
// Grid options object ==> DYNAMIC
var gridOptionDynamic = {
    //11 X 11 grid
    width: 10,
    height: 10,
    //Widget separation
    verticalMargin: 0,
    //cell height
    cellHeight: 45,
    //disable widget resize
    disableResize: true,
    //floating widgets
    float: true,
    //removeTimeout: 100,
    //allow widget to span over more than 1 col
    disableOneColumnMode: true,
    //false allows grid to move, true prevents
    staticGrid: false,
    //allow animations
    animate: true
}
// Construct URL for AJAX Request by isolating gamePlayer ID
var gpId = paramObj();
//This code returns a JavaScript object.
//If you use it on the query string "?gp=1",
//you will get an object of the form { gp: "1" }.
function paramObj() {
    var search = window.location.href;
    var obj = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    search.replace(reg, function (match, param, val) {
        obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });

    return obj;
}
//AJAX request to display relevant gamePlayer information. 

var data;
$.ajax({
    url: window.location.origin + "/api/game_view/" + gpId.gp,
    dataType: "json",
    success: function (json) {
        data = json;
        app.ships = json.ships;
        whoIsWho(); //Determine who is PLAYER and who is OPPONENT
        checkShips();
        getSalvoCoordinates();
        displaySalvoes();
        chooseSalvoLocation();
    },
    error: function (json) {
        alert(json.responseJSON.Message);
    }
})

//Exec query every 10"
var apiInterval = window.setInterval(apiCall, 10000);
function apiCall() {
    $.ajax({
        url: window.location.origin + "/api/game_view/" + gpId.gp,
        dataType: "json",
        success: function (json) {
            data = json;
            app.ships = json.ships;
            getSalvoCoordinates();
            displaySalvoes();
        },
        error: function (json) {
            alert(json.responseJSON.Message);
        }
    })
}

    var salvoCoordinates = []; // Array to be used by fx getSalvoCoordinates
    //Determines who is PLAYER and who is OPPONENT
    function whoIsWho() {
        var player;
        var opponent;

        data.gamePlayers.forEach(function (gp) {
            if (gpId.gp == gp.id) {
                app.player = gp.player
            } else {
                app.opponent = gp.player
            }
        })
    }
    //------------SHIP FXs ------------
    //Checks status of ships --> if ships havent been placed --> Allow dynamic grid
    function checkShips() {
        if (data.ships.length == 0) {
            createGrid(gridOptionDynamic);
            startingShipLocations();
            addEvent()
        } else {
            createGrid(gridOptionStatic);
            getLocations();
            //HIDE PLACE BUTTON
        }
    }
    //Assign a generic location to ships WHEN THEY HAVE NOT BEEN PLACED YET
    function startingShipLocations() {
        grid.addWidget($('<div id="five"><div id="Minions" class="grid-stack-item-content fiveHorizontal ship"></div><div/>'), 1, 5, 5, 1);
        grid.addWidget($('<div id="four"><div id="Squadron" class="grid-stack-item-content fourHorizontal ship"></div><div/>'), 1, 3, 4, 1);
        grid.addWidget($('<div id="threeA"><div id="Trio" class="grid-stack-item-content threeAHorizontal ship"></div><div/>'), 1, 7, 3, 1);
        grid.addWidget($('<div id="tres"><div  id="Trios"class="grid-stack-item-content threeAHorizontal ship"></div><div/>'), 1, 1, 3, 1);
        grid.addWidget($('<div id="two"><div id="Duo"class="grid-stack-item-content twoHorizontal ship"></div><div/>'), 1, 9, 2, 1);
    }
    //Handles Ship location assignment WHEN SHIPS HAVE NOT BEEN PLACED YET
    function addShips() {
        var allShips = [];
        //Fills ^ array in order to process ship data and post it
        $(".ship").each(function (index, ship) {
            var obj = new Object
            //Type (for API JSON)
            obj.type = this.parentNode.id

            //Location information
            var x = +(this.parentNode.getAttribute("data-gs-x")) + 1; //+1
            var y = +(this.parentNode.getAttribute("data-gs-y")); //switch
            var width = this.parentNode.getAttribute("data-gs-width")
            var height = this.parentNode.getAttribute("data-gs-height")

            var rawLocation = []
            //Process ship location data from grid placement
            if (width > height) {
                for (i = 0; i < width; i++) {
                    rawLocation.push(switchIntToChar(y) + (x + i))
                }
            } else {
                for (i = 0; i < height; i++) {
                    rawLocation.push(switchIntToChar(y + i) + x)
                }
            }
            obj.location = rawLocation;
            allShips.push(obj);
        })

        $.post({
                url: "/api/games/players/" + gpId.gp + "/ships",
                data: JSON.stringify(allShips),
                dataType: "text",
                contentType: "application/json"
            })
            .done(function (response) {
                window.location.reload();
            })
    }

    //Generates an array of each ship's location and then creates a ship at those locations WHEN SHIPS HAVE ALREADY BEEN PLACED
    function getLocations() {

        var coordinates = [];
        data.ships.forEach(function (ship) {
            var x = +(ship.location[0].slice(1)) - 1; //Get Int in Location coordinate [ej 1 in A1] and substract 1 to correlate w/grid
            var y = switchCharToInt(ship.location[0].slice(0, 1)); //Get Character in Location coordinate and assign INT value to correlate w/grid
            var width;
            var height;
            //If there are two equal letters in location array, the ship is horizontally positioned. Otherwise, its vertically positioned.
            if ((ship.location[0].slice(0, 1)) === (ship.location[1].slice(0, 1))) {
                vertical = false;
                horizontal = true;
            } else {
                vertical = true;
                horizontal = false;
            }

            if ((ship.location[0].slice(0, 1)) === (ship.location[1].slice(0, 1))) {
                width = ship.location.length;
                height = 1;
            } else {
                width = 1;
                height = ship.location.length;
            }

            var coordinateObj = new Object;
            coordinateObj.x = x,
                coordinateObj.y = y,
                coordinateObj.width = width,
                coordinateObj.height = height;

            coordinates.push(coordinateObj);

            if (width > height) {
                grid.addWidget($('<div><div class="grid-stack-item-content ' + ship.type + 'Horizontal"></div><div/>'), x, y, width, height);
            } else {
                grid.addWidget($('<div><div class="grid-stack-item-content ' + ship.type + 'Vertical"></div><div/>'), x, y, width, height);
            }
        })
    }
    //------------SALVO FXs ------------
    //Shows the location of Salvoes a player has fired
    function displaySalvoes() {
        data.salvos.forEach(function (salvo) {
            if (salvo.player == app.player.id) {
                salvo.location.forEach(function (shot) {
                    $("#" + shot).addClass("down")
                    $("#" + shot).html(salvo.turn) //Add conditional to change color depending on turn
                });
            }
        })
    }
    //Find and display opponent's HITS on player's grid.
    function getSalvoCoordinates() {

        var opponentSalvoCoordinates = [];

        data.salvos.forEach(function (salvo) {
            if (salvo.player == app.opponent.id) {
                salvo.location.forEach(function (location) {

                    var x = +(location.slice(1)) - 1; //Get Int in Location coordinate [ej 1 in A1] and substract 1 to correlate w/grid
                    var y = switchCharToInt(location.slice(0, 1)); //Get Character in Location coordinate and assign INT value to correlate w/grid
                    var salvoCoordinateObj = new Object;
                    salvoCoordinateObj.x = x,
                        salvoCoordinateObj.y = y,
                        opponentSalvoCoordinates.push(salvoCoordinateObj);

                    data.ships.forEach(function (ship) {
                        if (ship.location.indexOf(location) != -1) {
                            $("#grid").append("<div class='salvo-shot' style= 'top:" + y * 45 + "px; left:" + x * 45 + "px;'></div>");
                        }
                    })
                })

            }
        })
    }


    var chosenLoc = [];
    //numberOfShotsAllowed = app.ships.length
    var numberOfShotsSelected = 0;
    //Allows player to choose his firing locatons and fills chosenLoc [Array] with desired positions. 
    function chooseSalvoLocation() {
        $(".salvo").click(function (e) {
            if (numberOfShotsSelected >= 5 && !$(this).hasClass("fire-here")) {
                alert("You can only fire as many shots as ships you have.");
            }
            else if ($(this).hasClass("down")) {
                alert("You already fired here, try somewhere else.");
            } else {
                if ($(this).hasClass("fire-here")) { //Considers situation that a player changes his/her/their mind regarding chosen Loc
                    $(this).removeClass("fire-here")
                    chosenLoc.splice($(this).attr('id'));
                    numberOfShotsSelected--;
                } else {
                    $(this).addClass("fire-here");
                    chosenLoc.push($(this).attr('id'));
                    numberOfShotsSelected++;
                }
            }
        })
    }

    function addSalvoes() {
        $.post({
                url: "/api/games/players/" + gpId.gp + "/salvos",
                data: JSON.stringify(chosenLoc),
                dataType: "text",
                contentType: "application/json"
            })
            .done(function (response) {
                window.location.reload();
            })
    }

    //----------- Utility FXs ------------
    //Used to correlate location array string data to grid at game.html
    function switchCharToInt(y) {
        switch (y) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            case "D":
                return 3;
            case "E":
                return 4;
            case "F":
                return 5;
            case "G":
                return 6;
            case "H":
                return 7;
            case "I":
                return 8;
            case "J":
                return 9;
        }
    }
    //Used to transform grid location data into values that can be read by back-end
    function switchIntToChar(y) {
        switch (y) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            case 8:
                return "I";
            case 9:
                return "J";
        }
    }
    //Allows to change ship orientation
    function addEvent() {

        //Handle 5 sq widgets
        $("#five").click(function () {
            if ($(this).children().hasClass("fiveHorizontal")) {
                grid.resize($(this), 1, 5);
                $(this).children().removeClass("fiveHorizontal");
                $(this).children().addClass("fiveVertical");
            } else {
                grid.resize($(this), 5, 1);
                $(this).children().addClass("fiveHorizontal");
                $(this).children().removeClass("fiveVertical");
            }
        });
        //Handle 4 sq widgets
        $("#four").click(function () {
            if ($(this).children().hasClass("fourHorizontal")) {
                grid.resize($(this), 1, 4);
                $(this).children().removeClass("fourHorizontal");
                $(this).children().addClass("fourVertical");
            } else {
                grid.resize($(this), 4, 1);
                $(this).children().addClass("fourHorizontal");
                $(this).children().removeClass("fourVertical");
            }
        });

        //Handle 3A sq widgets
        $("#threeA").click(function () {
            if ($(this).children().hasClass("threeAHorizontal")) {
                grid.resize($(this), 1, 3);
                $(this).children().removeClass("threeAHorizontal");
                $(this).children().addClass("threeAVertical");
            } else {
                grid.resize($(this), 3, 1);
                $(this).children().addClass("threeAHorizontal");
                $(this).children().removeClass("threeAVertical");
            }
        });

        //Handle 3B sq widgets
        $("#tres").click(function () {
            if ($(this).children().hasClass("tresHorizontal")) {
                grid.resize($(this), 1, 3);
                $(this).children().removeClass("tresHorizontal");
                $(this).children().addClass("tresVertical");
            } else {
                grid.resize($(this), 3, 1);
                $(this).children().addClass("tresHorizontal");
                $(this).children().removeClass("tresVertical");
            }
        });

        //Handle 2 sq widgets
        $("#two").click(function () {
            if ($(this).children().hasClass("twoHorizontal")) {
                grid.resize($(this), 1, 2);
                $(this).children().removeClass("twoHorizontal");
                $(this).children().addClass("twoVertical");
            } else {
                grid.resize($(this), 2, 1);
                $(this).children().addClass("twoHorizontal");
                $(this).children().removeClass("twoVertical");
            }
        });

        //All GridStack functions are available @
        //https://github.com/gridstack/gridstack.js/tree/develop/doc

    }