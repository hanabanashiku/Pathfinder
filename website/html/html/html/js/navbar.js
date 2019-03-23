var toggled = false;

function toggleNav() {
    if (toggled == false) {
        document.getElementById("mySidenav").style.width = "250px";
        //document.getElementById("expandButton").style.marginLeft = "250px";
        document.body.style.backgroundColor = "rgba(0,0,0,0.4)";
        toggled = true;
    }
    else {
        document.getElementById("mySidenav").style.width = "0px";
        //document.getElementById("expandButton").style.marginLeft= "0";
        document.body.style.backgroundColor = "white";
        toggled = false;
    }
}

function closeNav() {
    if (toggled == true){
        document.getElementById("mySidenav").style.width = "0";
        document.getElementById("main").style.marginLeft= "0";
        document.body.style.backgroundColor = "white";
        toggled = false;
    }
}