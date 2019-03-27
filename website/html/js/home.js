var buildings = [];

class Building {
    constructor(title, imgPath, parent) {
        this.title = title;
        this.imgPath = imgPath;
        this.parent = parent;
        this.metaData = createNode(this.title, this.imgPath, this.parent);
    }
}

window.onload = function() {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        var myObj = JSON.parse(this.responseText);
        for (var i = 0; i < myObj[0].length; i++) {
            var tempBuilding = new Building(myObj[0][i], myObj[1][i], document.getElementById('myBuildings'));
            buildings.push(tempBuilding);
        }
    }
    };
    xmlhttp.open("POST", "../api/homePage.php", false);
    xmlhttp.send();

    loadMyBuildings();
};

function loadMyBuildings() {
    for(var element of buildings) {
        document.getElementById("myBuildings").appendChild(element.metaData);
    }
}

function createNode(buildingName, imagePath, parent) {  
    var cardDiv = document.createElement("div");
    cardDiv.className = 'card m-2';

    var headerDiv = document.createElement("div");
    headerDiv.className = 'card-header d-flex';

    var buildingNameDiv = document.createElement("div");
    buildingNameDiv.className = 'p-2 mr-auto';
    buildingNameDiv.innerHTML = buildingName;

    var viewButtonDiv = document.createElement("div");
    viewButtonDiv.className = 'p-2';

    var viewButton = document.createElement('button');
    viewButton.className = "btn btn-block btn-info";
    viewButton.innerHTML = 'View';
    viewButton.onclick = function() {
        
    };
    viewButton.disabled = true;
    viewButtonDiv.appendChild(viewButton);

    var removeButtonDiv = document.createElement("div");
    removeButtonDiv.className = 'p-2';

    var removeButton = document.createElement('button');
    removeButton.className = "btn btn-block btn-danger";
    removeButton.innerHTML = 'Remove';
    removeButton.onclick = function() {
        cardDiv.parentNode.removeChild(cardDiv);
        
    };
    removeButtonDiv.appendChild(removeButton);

    headerDiv.appendChild(buildingNameDiv);
    headerDiv.appendChild(viewButtonDiv);
    headerDiv.appendChild(removeButtonDiv);


    var bodyDiv = document.createElement("div");
    bodyDiv.className = 'card-body d-flex justify-content-around';

    var image = document.createElement("img");
    image.src = imagePath;
    bodyDiv.appendChild(image);


    cardDiv.appendChild(headerDiv);
    cardDiv.appendChild(bodyDiv);
    
    parent.appendChild(cardDiv);
    return cardDiv;
}