![Pathfinder](https://github.com/hanabanashiku/Pathfinder/blob/master/Pathfinder/app/src/main/res/drawable/logo.png)

Pathfinder is an application and architecture framework used for navigating a guest through a commercial building using wifi technology. The Pathfinder flow consists of an *administrator* that manages building layouts by defining pathways and destinations, and a *user* that views map data and directions through an Android app.

# Requirements
Apache
mySQL
PHP7
Android 6.0 or greater
A wifi hotspot solution (see below)

# Installation
Installing Pathfinder is fairly inuitive. The website is Apache-based. Simply put the website files into your html root and set up the database with the included mySQL files. In the /api directory, there is a JSON configuration file that for the purpose of supplying the database connection information. The included .htaccess files should take care of this, but it is imperitive that this file is not accessible from the web.

On the user side, installing Pathfinder is as simple as loading the provided APK.

# Creating a Building
To create a new building, navigate to My Buildings->Register New Building. Supply a building name and upload a clear map image for each accessible floor of the building. This appliaction assumes that the top edge of the map image faces north. If it is not facing north, it may be necessary to roate the image.
Next, navigate to View Existing Buildings and select the building that was just created. The interface allows the administrator to modify a map floor-by-floor.
The map is made up of *nodes*, *beacons*, and *hallways*. A hallway must connect two nodes. A node may either be a *room*, a hallway *intersection*, or a *floor connector*. Currently, if a room is not given a name, it is assumed to be an intersection. A beacon represents a wifi hotspot that will be used for the purpose of location resolution. Floor connectors may either be staircases, elevators, or escalators.

# Deploying Beacons
There is beacon implementation that must be strictly adhered to. Any device that provides a consistent wifi hotspot could theoretically function as a wifi beacon. The only requirement is that the SSID be hidden and match the SSID provided by the adminstration panel. For the purposes of this appliaction, the beacon is assumed to be a Raspberry Pi Zero W. The beacons must be placed in the exact location provided when creating the building floors to provide the most accurate positioning.
Please note that defining too many beacons or defining them too close together on a floor will cause interferance and lead to sporatic location resolutions. A medium-sized building should have five to seven beacons per floor. It is also recommended for any given floor to have no less than three beacons. A small building will likely only require three beacons.

# Using the App
Once the app launches, it will try to resolve the building it is currently in. If it finds a beacon matching the correct SSID signature, it will download the building map, and display the user's location in the building if the information provided is valid. Otherwise, the app will display an error message alerting the user that the building is not currently Pathfinder-enabled and invite the user to search for a building to view. The user can also do so at anytime by using the appbar's menu.
If the building is resolved, the user may search for a destination to navigate through. Submitting a search will display a list of destinations, with an approximate distance and whether or not the room requires authorization. Selecting a destination shows the map of the current floor with the direction the user is facing and a blue highlight to show the recommended path.

# License
This softare is provided free of charge in the hopes that it will be useful. It is provided open source under a GNU license.
