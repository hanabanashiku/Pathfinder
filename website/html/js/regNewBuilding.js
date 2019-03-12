// https://medium.com/typecode/a-strategy-for-handling-multiple-file-uploads-using-javascript-eb00a77e15f
var arbNum = 0;
var floors = [];
var fileList = [];

class Floor {
    constructor(metaData) {
        this.metaData = metaData;
    }
}

function addNewFloor(){
    var currFloor = new Floor(createNode());
    floors.push(currFloor);
}

function checkName() {
    // alert(document.getElementById("buildingName").value);
    if (document.getElementById("buildingName").value.length > 0){
        document.getElementById('addFloorButton').disabled = false;
        document.getElementById('uploadButton').disabled = false;
    }
    else {
        document.getElementById('addFloorButton').disabled = true;
        document.getElementById('uploadButton').disabled = true;
    }
}

function createNode() {  
    var parentDiv = document.createElement("div");
    parentDiv.className = 'd-flex justify-content-start';
    parentDiv.id = 'file' + arbNum;

    var textDiv = document.createElement("div");
    textDiv.className = 'p-2';
    var numberDiv = document.createElement("div");
    numberDiv.className = 'p-2';
    var fileHolderDiv = document.createElement("div");
    fileHolderDiv.className = 'p-2 flex-grow-1';
    var removeButtonDiv = document.createElement("div");
    removeButtonDiv.className = 'p-2';


    var label = document.createElement("label");
    label.className = "mx-auto";
    label.innerHTML = "Floor Number:";
    textDiv.appendChild(label);


    var input = document.createElement("input");
    input.type = "number";
    input.className = "form-control";
    numberDiv.appendChild(input);


    var fileDiv = document.createElement("div");
    fileDiv.className = 'custom-file';

    var fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.className = "custom-file-input";
    fileInput.id = 'fileToUpload' + arbNum;
    fileInput.name = 'fileToUpload' + arbNum;
    fileInput.addEventListener("change", function(){
        //get the file name
        var fileName = $(this).val();
        //Only returns the filename
        var fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
        //replace the "Choose a file" label
        $(this).next('.custom-file-label').html(fileName);
    });
    fileDiv.appendChild(fileInput);

    var label2 = document.createElement("label");
    label2.className = "custom-file-label";
    label2.innerHTML = "Choose file";
    fileDiv.appendChild(label2);

    fileHolderDiv.appendChild(fileDiv);


    var removeButton = document.createElement('button');
    removeButton.className = "btn btn-danger";
    removeButton.innerHTML = '✘';
    removeButton.onclick = function() {
        parentDiv.parentNode.removeChild(parentDiv);
        for(var element of floors) {
            if (element.metaData == parentDiv){
                floors.splice(floors.indexOf(element), 1);
            }
        }
    };
    removeButtonDiv.appendChild(removeButton);


    parentDiv.appendChild(textDiv);
    parentDiv.appendChild(numberDiv);
    parentDiv.appendChild(fileHolderDiv);
    parentDiv.appendChild(removeButtonDiv);

    var element = document.getElementById("listview");
    element.appendChild(parentDiv);
    arbNum += 1;
    return parentDiv;
}

function uploadFiles() {
    document.getElementById("buildingName").disabled = true;
    document.getElementById("uploadButton").disabled = true;

    document.getElementById("addFloorButton").remove();
    document.getElementById("uploadButton").innerHTML = '<span class="spinner-border spinner-border-sm"></span>';

    var formData = new FormData();
    formData.set('folderName', document.getElementById("buildingName").value);

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "../api/folderCreate.php", false);
    xhttp.send(formData);

    var uploads = [];
    for(var element of floors) {
        var formData = new FormData();
        formData.set('file', element.metaData.childNodes[2].childNodes[0].childNodes[0].files[0]);
        formData.set('name', element.metaData.childNodes[1].childNodes[0].value.concat(element.metaData.childNodes[2].childNodes[0].childNodes[1].innerHTML.substring(element.metaData.childNodes[2].childNodes[0].childNodes[1].innerHTML.lastIndexOf("."))));
        formData.set('folderName', document.getElementById("buildingName").value);

        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "../api/multiUpload.php", false);
        xhttp.addEventListener("load", function(){
            element.metaData.childNodes[1].childNodes[0].disabled = true;
            element.metaData.childNodes[2].childNodes[0].childNodes[0].disabled = true;

            element.metaData.childNodes[3].childNodes[0].disabled = true; 
            element.metaData.childNodes[3].childNodes[0].className = "btn btn-success";
            element.metaData.childNodes[3].childNodes[0].innerHTML = '✔';
            element.metaData.childNodes[3].childNodes[0].onclick = function() { return false; };
        }); 
        xhttp.send(formData);
    }
    document.getElementById("uploadButton").innerHTML = '✔';
    showToast();
}

function showToast(){
    $('.toast').toast('show');
}

function outputfirstname() {
    alert(document.getElementById("buildingName").value);
}