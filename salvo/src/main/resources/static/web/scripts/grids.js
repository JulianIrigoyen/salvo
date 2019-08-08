//Create grid
function createGrid () {

    var options = {
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
    //Grid initialization
    $('.grid-stack').gridstack(options);
    grid = $('#grid').data('gridstack');

    //checks area availability
    //if free == true


    console.log(grid.isAreaEmpty(1, 8, 3, 1));
    console.log(grid.isAreaEmpty(1, 7, 3, 1));
    $("#carrier,#carrier2").click(function(){
        if($(this).children().hasClass("carrierHorizontal")){
            grid.resize($(this),1,3);
            $(this).children().removeClass("carrierHorizontal");
            $(this).children().addClass("carrierHorizontalRed");
        }else{
            grid.resize($(this),3,1);
            $(this).children().addClass("carrierHorizontal");
            $(this).children().removeClass("carrierHorizontalRed");
        }
    });
    $("#patroal,#patroal2").click(function(){
        if($(this).children().hasClass("patroalHorizontal")){
            grid.resize($(this),1,2);
            $(this).children().removeClass("patroalHorizontal");
            $(this).children().addClass("patroalHorizontalRed");
        }else{
            grid.resize($(this),2,1);
            $(this).children().addClass("patroalHorizontal");
            $(this).children().removeClass("patroalHorizontalRed");
        }
    });


    getLocations();
    //all functions available @
    //https://github.com/gridstack/gridstack.js/tree/develop/doc
};
//Generates an array of each ship's location and then creates a ship at those locations
function getLocations (){

    var coordinates = [];

    data.ships.forEach(function (ship){
        var x= +(ship.location[0].slice(1)) - 1; //Get Int in Location coordinate [ej 1 in A1] and substract 1 to correlate w/grid
        var y= switchCharToInt(ship.location[0].slice(0, 1)); //Get Character in Location coordinate and assign INT value to correlate w/grid
        var width;
        var height;
            //If there are two equal letters in location array, the ship is horizontally positioned.
            //Otherwise, its vertically positioned.
            if ( (ship.location[0].slice(0,1)) === (ship.location[1].slice(0, 1)) ) {
                        vertical = false;
                        horizontal = true;
                        }
                    else {
                        vertical = true;
                        horizontal = false;
                    }
            //Determines Ship orientation
            if ( (ship.location[0].slice(0,1)) === (ship.location[1].slice(0, 1)) ) {
                width = ship.location.length;
                height = 1;
            }

            else {
                width = 1;
                height = ship.location.length;
            }

        var coordinateObj = new Object;
        coordinateObj.x = x,
        coordinateObj.y = y,
        coordinateObj.width = width,
        coordinateObj.height = height;

        coordinates.push(coordinateObj);

           if ( width > height){
           grid.addWidget($('<div><div class="grid-stack-item-content '+ship.type+'Horizontal"></div><div/>'), x, y, width, height);
            }

           else {
           grid.addWidget($('<div><div class="grid-stack-item-content '+ship.type+'Vertical"></div><div/>'), x, y, width, height);
           }

    })

    console.log (coordinates);

}


function switchCharToInt(y){
    switch (y){
        case "A": return 0;
        case "B": return 1;
        case "C": return 2;
        case "D": return 3;
        case "E": return 4;
        case "F": return 5;
        case "G": return 6;
        case "H": return 7;
        case "I": return 8;
        case "J": return 9;
        case "L": return 10;
 }
}




// si el gameplayer tiene barcos en el back end, get, create grid, FREEZE
//si no hay barcos, grilla movible con locaciones