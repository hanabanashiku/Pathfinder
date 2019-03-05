var canMaxX;
var canMaxY;
var img;
var beacons = [];
var hallways = [];
var destinations = [];
var intersections = [];
var drawMode = "addBeacon";
var tempx = -1;
var tempy = -1;
var xPosToSet = -1;
var yPosToSet = -1;
var makingHallway = false;

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
    constructor(xCoord, yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.displayPosX = xCoord*canMaxX;
        this.displayPosY = yCoord*canMaxY;
    }
    display() {
        stroke(0, 0, 0);
        strokeWeight(1);
        smooth();
        fill(255, 0, 0);
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
}

class Intersection {
    constructor(xCoord, yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.displayPosX = xCoord*canMaxX;
        this.displayPosY = yCoord*canMaxY;
    }
    display() {
        stroke(0, 0, 0);
        strokeWeight(1);
        smooth();
        fill(0, 0, 255);
        ellipse(this.displayPosX, this.displayPosY, 20, 20);
    }
}

function preload() {
    img = loadImage("../uploads/floorPlan.jpg");
}

function setup() {
    canMaxX = img.width;
    canMaxY = img.height;
    var canvas = createCanvas(canMaxX+1 , canMaxY+1);
    canvas.parent('sketch-holder');

    var elem = document.getElementById("addBeaconButton");
    elem.classList.add("active");
}

function draw() {
    image(img, 0, 0, canMaxX, canMaxY);
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
        for(var element of intersections) {
            element.display();
        }
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
        for(var element of intersections) {
            element.display();
        }
        for(var element of destinations) {
            element.display();
        }
    }
    else if (drawMode == "removeDestination") {
        for(var element of hallways) {
            element.display();
        }
        for(var element of intersections) {
            element.display();
        }
        for(var element of destinations) {
            element.display();
            element.hurtbox(mouseX, mouseY);
        }
    }
}

function mouseClicked() {
    if (drawMode == "addBeacon"){
        if (mouseX>=0 && mouseX<canMaxX && mouseY>=0 && mouseY<canMaxY) {
            currBeacon = new Beacon(mouseX/canMaxX, mouseY/canMaxY, createBeaconNode());
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
                    var currIntersection = new Intersection(tempx/canMaxX, tempy/canMaxY);
                    intersections.push(currIntersection);
                    break;
                }
                else if (element.snapBox(mouseX, mouseY) == "last") {
                    tempx = element.displayPosX2;
                    tempy = element.displayPosY2;
                    var currIntersection = new Intersection(tempx/canMaxX, tempy/canMaxY);
                    intersections.push(currIntersection);
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
                    var currIntersection = new Intersection(xPosToSet/canMaxX, yPosToSet/canMaxY);
                    intersections.push(currIntersection);
                    break;
                }
                else if (element.snapBox(mouseX, mouseY) == "last"){
                    xPosToSet = element.displayPosX2;
                    yPosToSet = element.displayPosY2;
                    var currIntersection = new Intersection(xPosToSet/canMaxX, yPosToSet/canMaxY);
                    intersections.push(currIntersection);
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
            var currDestination = new Destination(xPosToSet/canMaxX, yPosToSet/canMaxY);
            destinations.push(currDestination);
        }
    }
    else if (drawMode == "removeDestination"){
        for(var element of destinations) {
            if (element.hurtbox(mouseX, mouseY)){
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
            destinations.splice(-1,1);
        }
    }
}

function createBeaconNode() {  
    var parentDiv = document.createElement("div");
    parentDiv.className = 'd-flex justify-content-between';
    parentDiv.id = 'beacon' + beacons.length;

    // var textDiv = document.createElement("div");
    // textDiv.className = 'p-2';
    var inputDiv = document.createElement("div");
    inputDiv.className = 'p-2';
    var identifyButtonDiv = document.createElement("div");
    identifyButtonDiv.className = 'p-2';
    var removeButtonDiv = document.createElement("div");
    removeButtonDiv.className = 'p-2';

    // var para = document.createElement("p");
    // var node = document.createTextNode("Pin " + beacons.length);// + " is located at x:" + beacons[beacons.length -1].xCoord + " y:"+ beacons[beacons.length -1].yCoord);
    // para.appendChild(node);
    // textDiv.appendChild(para);

    var input = document.createElement("input");
    input.type = "text";
    input.className = "form-control";
    inputDiv.appendChild(input);
    
    var identifyButton = document.createElement('button');
    identifyButton.style.float = 'right';
    identifyButton.className = "btn btn-info";
    identifyButton.innerHTML = '🔍';
    identifyButton.onclick = function() {
        for(var element of beacons) {
            element.unIdentify();
        }
        for(var element of beacons) {
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
        for(var element of beacons) {
            if (element.metaData == parentDiv){
                beacons.splice(beacons.indexOf(element), 1);
            }
        }
    };
    removeButtonDiv.appendChild(removeButton);

    // parentDiv.appendChild(textDiv);
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
    }
    else if (mode == "removeBeacon") {
        resetMode();
        var elem = document.getElementById("removeBeaconButton");
        elem.classList.add("active");
    }
    else if (mode == "addHallway") {
        resetMode();
        var elem = document.getElementById("addHallwayButton");
        elem.classList.add("active");
    }
    else if (mode == "addDestination") {
        resetMode();
        var elem = document.getElementById("addDestinationButton");
        elem.classList.add("active");
    }
    else if (mode == "removeDestination") {
        resetMode();
        var elem = document.getElementById("removeDestinationButton");
        elem.classList.add("active");
    }
}

function resetMode(){
    var buttons = [document.getElementById("addBeaconButton"), document.getElementById("removeBeaconButton"), document.getElementById("addHallwayButton"), document.getElementById("addDestinationButton"), document.getElementById("removeDestinationButton")];
    for(var element of buttons) {
        element.classList.remove("active");
    }
}