var canMaxX;
var canMaxY;
var img;
var beacons = [];
var hallways = [];
var destinations = [];
// var intersections = [];
var drawMode = "addBeacon";
var tempx = -1;
var tempy = -1;
var xPosToSet = -1;
var yPosToSet = -1;
var makingHallway = false;
var showBackground = true;

class Beacon {
    constructor(xCoord, yCoord, metaData) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.displayPosX = xCoord*canMaxX;
        this.displayPosY = yCoord*canMaxY;
        this.metaData = metaData;
        this.fillColor = color(255, 0, 0);
    }
    display() {
        strokeWeight(1);
        noStroke();
        smooth();
        fill(this.fillColor);
        triangle(this.displayPosX, this.displayPosY, this.displayPosX-9, this.displayPosY-25, this.displayPosX+9, this.displayPosY-25);
        ellipse(this.displayPosX, this.displayPosY-30, 20, 20);
        
        fill(255, 255, 255);
        ellipse(this.displayPosX, this.displayPosY-30, 10, 10);
    }
    hurtbox(xPos, yPos) {
        strokeWeight(1);
        if ( (xPos>(this.displayPosX-16) && xPos<(this.displayPosX+16)) && (yPos>(this.displayPosY-42) && yPos<(this.displayPosY+1)) ) {
            stroke( 0, 0, 0);
            noFill();
            rect(this.displayPosX-15, this.displayPosY, 30, -41);
            return true;
        }
        return false;
    }
    identify() {
        this.fillColor = color(0, 255, 0);
    }
    unIdentify(){
        this.fillColor = color(255, 0, 0);
    }
    show() {
        this.metaData.style.visibility = "visible"; 
    }
    hide() {
        this.metaData.style.visibility = "hidden"; 
    }
}

class Hallway {
    constructor(xCoord1, yCoord1, xCoord2, yCoord2) {
        this.xCoord1 = xCoord1;
        this.yCoord1 = yCoord1;
        this.xCoord2 = xCoord2;
        this.yCoord2 = yCoord2;
        this.displayPosX1 = xCoord1*canMaxX;
        this.displayPosY1 = yCoord1*canMaxY;
        this.displayPosX2 = xCoord2*canMaxX;
        this.displayPosY2 = yCoord2*canMaxY;
    }
    display() {
        stroke(0, 0, 0);
        strokeWeight(5);
        smooth();
        fill(0, 0, 0);
        line(this.displayPosX1, this.displayPosY1, this.displayPosX2, this.displayPosY2);
    }
    snapBox(xPos, yPos) {
        strokeWeight(1);
        if ( (xPos>(this.displayPosX1-20) && xPos<(this.displayPosX1+20)) && (yPos>(this.displayPosY1-20) && yPos<(this.displayPosY1+20)) ) {
            stroke(255, 0, 0);
            noFill();
            rect(this.displayPosX1-20, this.displayPosY1-20, 40, 40);
            return "first";
        }
        if ( (xPos>(this.displayPosX2-20) && xPos<(this.displayPosX2+20)) && (yPos>(this.displayPosY2-20) && yPos<(this.displayPosY2+20)) ) {
            stroke(255, 0, 0);
            noFill();
            rect(this.displayPosX2-20, this.displayPosY2-20, 40, 40);
            return "last";
        }
        return "none";
    }
}

class Destination {
    constructor(xCoord, yCoord, metaData){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.displayPosX = xCoord*canMaxX;
        this.displayPosY = yCoord*canMaxY;
        this.metaData = metaData;
        this.fillColor = color(255, 0, 0);
    }
    display() {
        stroke(0, 0, 0);
        strokeWeight(1);
        smooth();
        fill(this.fillColor);
        ellipse(this.displayPosX, this.displayPosY, 20, 20);
    }
    hurtbox(xPos, yPos) {
        strokeWeight(1);
        if ( (xPos>(this.displayPosX-20) && xPos<(this.displayPosX+20)) && (yPos>(this.displayPosY-20) && yPos<(this.displayPosY+20)) ) {
            stroke(255, 0, 0);
            noFill();
            rect(this.displayPosX-20, this.displayPosY-20, 40, 40);
            return true;
        }
        return false;
    }
    identify() {
        this.fillColor = color(0, 255, 0);
    }
    unIdentify(){
        this.fillColor = color(255, 0, 0);
    }
    show() {
        this.metaData.style.visibility = "visible"; 
    }
    hide() {
        this.metaData.style.visibility = "hidden"; 
    }
}

// class Intersection {
//     constructor(xCoord, yCoord) {
//         this.xCoord = xCoord;
//         this.yCoord = yCoord;
//         this.displayPosX = xCoord*canMaxX;
//         this.displayPosY = yCoord*canMaxY;
//     }
//     display() {
//         stroke(0, 0, 0);
//         strokeWeight(1);
//         smooth();
//         fill(0, 0, 255);
//         ellipse(this.displayPosX, this.displayPosY, 20, 20);
//     }
// }

function preload() {
    img = loadImage("../uploads/placeholder.png");
}

function setup() {
    canMaxX = 1000;
    canMaxY = 1000;
    var canvas = createCanvas(canMaxX+1 , canMaxY+1);
    canvas.parent('sketch-holder');

    var elem = document.getElementById("addBeaconButton");
    elem.classList.add("active");
}

function draw() {
    background(255);
    if(showBackground)
        image(img, 0, 0, canMaxX, canMaxY);
    else
        background(255);
    if (drawMode == "addBeacon"){
        for(var element of beacons) {
            element.display();
        }
    }
    else if (drawMode == "removeBeacon") {
        for(var element of beacons) {
            element.hurtbox(mouseX, mouseY);
            element.display();
        }
    }
    else if (drawMode == "addHallway") {
        if (tempx != -1 && tempy != -1) {
            strokeWeight(5);
            stroke(0, 0, 0);
            line(tempx, tempy, mouseX, mouseY);
        }

        for(var element of hallways) {
            element.display();
            element.snapBox(mouseX, mouseY);
        }
        // for(var element of intersections) {
        //     element.display();
        // }
        for(var element of destinations) {
            element.display();
            element.hurtbox(mouseX, mouseY);
        }
    }
    else if (drawMode == "addDestination") {
        for(var element of hallways) {
            element.display();
            element.snapBox(mouseX, mouseY);
        }
        // for(var element of intersections) {
        //     element.display();
        // }
        for(var element of destinations) {
            element.display();
        }
    }
    else if (drawMode == "removeDestination") {
        for(var element of hallways) {
            element.display();
        }
        // for(var element of intersections) {
        //     element.display();
        // }
        for(var element of destinations) {
            element.display();
            element.hurtbox(mouseX, mouseY);
        }
    }
}

function mouseClicked() {
    if (drawMode == "addBeacon"){
        if (mouseX>=0 && mouseX<canMaxX && mouseY>=0 && mouseY<canMaxY) {
            currBeacon = new Beacon(mouseX/canMaxX, mouseY/canMaxY, createNode(beacons));
            beacons.push(currBeacon);
        }
    }
    else if (drawMode == "removeBeacon"){
        for(var element of beacons) {
            if (element.hurtbox(mouseX, mouseY)){
                var listView = document.getElementById("listview");
                listView.removeChild(element.metaData);
                beacons.splice(beacons.indexOf(element), 1);
            }
        }
    }
    else if (drawMode == "addHallway"){
        if (!makingHallway && mouseX>=0 && mouseX<canMaxX && mouseY>=0 && mouseY<canMaxY){
            if (hallways.length == 0 && destinations.length == 0){
                tempx = mouseX;
                tempy = mouseY;
            }
            for(var element of hallways) {
                if (element.snapBox(mouseX, mouseY) == "first") {
                    tempx = element.displayPosX1;
                    tempy = element.displayPosY1;
                    // var currIntersection = new Intersection(tempx/canMaxX, tempy/canMaxY);
                    // intersections.push(currIntersection);
                    break;
                }
                else if (element.snapBox(mouseX, mouseY) == "last") {
                    tempx = element.displayPosX2;
                    tempy = element.displayPosY2;
                    // var currIntersection = new Intersection(tempx/canMaxX, tempy/canMaxY);
                    // intersections.push(currIntersection);
                    break;
                }
            }
            for(var element of destinations) {
                if (element.hurtbox(mouseX, mouseY)){
                    tempx = element.displayPosX;
                    tempy = element.displayPosY;
                    break;
                }
            }
            if (tempx == -1 && tempy == -1){
                tempx = mouseX;
                tempy = mouseY;
            }
            makingHallway = true;
        }
        else if (makingHallway && mouseX>=0 && mouseX<canMaxX && mouseY>=0 && mouseY<canMaxY) {
            xPosToSet = -1;
            yPosToSet = -1;
            if (hallways.length == 0 && destinations.length == 0){
                xPosToSet = mouseX;
                yPosToSet = mouseY;
            }
            for(var element of hallways) {
                if (element.snapBox(mouseX, mouseY) == "first"){
                    xPosToSet = element.displayPosX1;
                    yPosToSet = element.displayPosY1;
                    // var currIntersection = new Intersection(xPosToSet/canMaxX, yPosToSet/canMaxY);
                    // intersections.push(currIntersection);
                    break;
                }
                else if (element.snapBox(mouseX, mouseY) == "last"){
                    xPosToSet = element.displayPosX2;
                    yPosToSet = element.displayPosY2;
                    // var currIntersection = new Intersection(xPosToSet/canMaxX, yPosToSet/canMaxY);
                    // intersections.push(currIntersection);
                    break;
                }
                else {
                    xPosToSet = mouseX;
                    yPosToSet = mouseY;
                }
            }
            for(var element of destinations) {
                if (element.hurtbox(mouseX, mouseY)){
                    xPosToSet = element.displayPosX;
                    yPosToSet = element.displayPosY;
                    break;
                }
            }
            if (xPosToSet == -1 && yPosToSet == -1){
                xPosToSet = mouseX;
                yPosToSet = mouseY;
            }
            var currHallway = new Hallway(tempx/canMaxX, tempy/canMaxY, xPosToSet/canMaxX, yPosToSet/canMaxY);
            hallways.push(currHallway);
            tempx = -1;
            tempy = -1;
            makingHallway = false;
        }
    }
    else if (drawMode == "addDestination"){
        if (mouseX>=0 && mouseX<canMaxX && mouseY>=0 && mouseY<canMaxY){
            if (hallways.length == 0){
                xPosToSet = mouseX;
                yPosToSet = mouseY;
            }
            for(var element of hallways) {
                if (element.snapBox(mouseX, mouseY) == "first") {
                    xPosToSet = element.displayPosX1;
                    yPosToSet = element.displayPosY1;
                    break;
                }
                else if (element.snapBox(mouseX, mouseY) == "last") {
                    xPosToSet = element.displayPosX2;
                    yPosToSet = element.displayPosY2;
                    break;
                }
                else {
                    xPosToSet = mouseX;
                    yPosToSet = mouseY;
                }
            }
            var currDestination = new Destination(xPosToSet/canMaxX, yPosToSet/canMaxY, createNode(destinations));
            destinations.push(currDestination);
        }
    }
    else if (drawMode == "removeDestination"){
        for(var element of destinations) {
            if (element.hurtbox(mouseX, mouseY)){
                var listView = document.getElementById("listview");
                listView.removeChild(element.metaData);
                destinations.splice(destinations.indexOf(element), 1);
            }
        }
    }
}

function keyPressed() {
    if (drawMode == "addBeacon" || drawMode == "removeBeacon"){
        if (keyCode === LEFT_ARROW) {
            var listView = document.getElementById("listview");
            listView.removeChild(beacons[beacons.length-1].metaData);
            beacons.splice(-1,1);
        }
    }
    else if (drawMode == "addHallway"){
        if (keyCode === LEFT_ARROW) {
            hallways.splice(-1,1);
        }
    }
    else if (drawMode == "addDestination" || drawMode == "removeDestination"){
        if (keyCode === LEFT_ARROW) {
            var listView = document.getElementById("listview");
            listView.removeChild(destinations[destinations.length-1].metaData);
            destinations.splice(-1,1);
        }
    }
    if (keyCode === RIGHT_ARROW) {
        showBackground = !showBackground;
    }
}

function displaySideList(displayArray){
    for(var element of beacons) {
        element.hide();
    }
    for(var element of destinations) {
        element.hide();
    }
    for(var element of displayArray) {
        element.show();
    }
}

function createNode(displayArray) {  
    var parentDiv = document.createElement("div");
    parentDiv.className = 'd-flex justify-content-start';
    parentDiv.id = 'beacon' + displayArray.length;

    var inputDiv = document.createElement("div");
    inputDiv.className = 'p-2';
    var identifyButtonDiv = document.createElement("div");
    identifyButtonDiv.className = 'p-2';
    var removeButtonDiv = document.createElement("div");
    removeButtonDiv.className = 'p-2';

    var input = document.createElement("input");
    input.type = "text";
    input.className = "form-control";
    inputDiv.appendChild(input);
    
    var identifyButton = document.createElement('button');
    identifyButton.style.float = 'right';
    identifyButton.className = "btn btn-info";
    identifyButton.innerHTML = '🔍';
    identifyButton.onclick = function() {
        for(var element of displayArray) {
            element.unIdentify();
        }
        for(var element of displayArray) {
            if (element.metaData == parentDiv){
                element.identify();
            }
        }
    };
    identifyButtonDiv.appendChild(identifyButton);

    var removeButton = document.createElement('button');
    removeButton.style.float = 'right';
    removeButton.className = "btn btn-danger";
    removeButton.innerHTML = '✘';
    removeButton.onclick = function() {
        parentDiv.parentNode.removeChild(parentDiv);
        for(var element of displayArray) {
            if (element.metaData == parentDiv){
                displayArray.splice(displayArray.indexOf(element), 1);
            }
        }
    };
    removeButtonDiv.appendChild(removeButton);

    parentDiv.appendChild(inputDiv);
    parentDiv.appendChild(identifyButtonDiv);
    parentDiv.appendChild(removeButtonDiv);

    var element = document.getElementById("listview");
    element.appendChild(parentDiv);
    return parentDiv;
}

function setMode(mode) {
    drawMode = mode;
    if (mode == "addBeacon") {
        resetMode();
        var elem = document.getElementById("addBeaconButton");
        elem.classList.add("active");
        displaySideList(beacons);
    }
    else if (mode == "removeBeacon") {
        resetMode();
        var elem = document.getElementById("removeBeaconButton");
        elem.classList.add("active");
        displaySideList(beacons);
    }
    else if (mode == "addHallway") {
        resetMode();
        var elem = document.getElementById("addHallwayButton");
        elem.classList.add("active");
        displaySideList(destinations);
    }
    else if (mode == "addDestination") {
        resetMode();
        var elem = document.getElementById("addDestinationButton");
        elem.classList.add("active");
        displaySideList(destinations);
    }
    else if (mode == "removeDestination") {
        resetMode();
        var elem = document.getElementById("removeDestinationButton");
        elem.classList.add("active");
        displaySideList(destinations);
    }
}

function resetMode(){
    var buttons = [document.getElementById("addBeaconButton"), document.getElementById("removeBeaconButton"), document.getElementById("addHallwayButton"), document.getElementById("addDestinationButton"), document.getElementById("removeDestinationButton")];
    for(var element of buttons) {
        element.classList.remove("active");
    }
}

window.onload = function() {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        var myObj = JSON.parse(this.responseText);
        for(var element of myObj) {
            var option = document.createElement("option");
            option.text = element;
            document.getElementById("buildingList").add(option);
        }
    }
    };
    xmlhttp.open("POST", "../api/populateBuildingBox.php", true);
    xmlhttp.send();
};

function populateFloorsFocus() {
    
}

function populateFloorsChange() {
    try{
        document.getElementById('tempOptionBuilding').remove();
    } catch (ex) {}
    document.getElementById('floorList').disabled = false;

    var floorList = document.getElementById("floorList");
    while (floorList.firstChild) {
        floorList.removeChild(floorList.firstChild);
    }

    var option = document.createElement("option");
    option.text = 'Select';
    option.id = 'tempOptionFloor';
    document.getElementById("floorList").add(option);

    var formData = new FormData();
    formData.set('building', document.getElementById("buildingList").options[document.getElementById("buildingList").selectedIndex].text);

    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var myObj = JSON.parse(this.responseText);
            for (var i = 0; i < myObj[0].length; i++) {
                var option = document.createElement("option");
                option.text = myObj[0][i];
                option.value = myObj[1][i];
                document.getElementById("floorList").add(option);
            }
        }
    };
    xmlhttp.open("POST", "../api/populateFloorBox.php", false);
    xmlhttp.send(formData);

    resetData();
    img = loadImage("../uploads/placeholder.png");
}

function changeImageFocus() {
    
}

function changeImageChange() {
    try{
        document.getElementById('tempOptionFloor').remove();
    } catch (ex) {}

    img = loadImage(document.getElementById("floorList").options[document.getElementById("floorList").selectedIndex].value);
    resetData();
}

function resetData() {
    beacons = [];
    hallways = [];
    destinations = [];

    var listView = document.getElementById("listview");
    while (listView.firstChild) {
        listView.removeChild(listView.firstChild);
    }
}

function sendToDB() {
    sendBeacons();
}

function sendBeacons() {
    document.getElementById("submitButton").disabled = true;

    for(var element of beacons) {
        var formData = new FormData();
        formData.set('building', document.getElementById("buildingList").options[document.getElementById("buildingList").selectedIndex].text);
        formData.set('floor', document.getElementById("floorList").options[document.getElementById("floorList").selectedIndex].text);
        formData.set('beaconName', element.metaData.firstChild.firstChild.value);
        formData.set('xPos', element.xCoord);
        formData.set('yPos', element.yCoord);

        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "../api/beaconUpload.php", false);
        xhttp.send(formData);
    }
}