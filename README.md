# faremap
Mapping UK train fares and split tickets, using open data from National Rail and TfL.

https://faremap.cjar.co.uk/
(currently hosted on [Netlify](https://www.netlify.com/))

![website preview](https://raw.githubusercontent.com/JakeCracknell/fare_map/master/preview.png)

## Running locally
To generate the fare data, you will need Java 8+ and Maven 2+.

You can start a local web server in `web/`. You will see the station data, but not any fares data, as this totals 16 GB and hence does not belong in a GitHub repo.

To generate the JSON files in `web/data/fares/`:
1. Register here: https://opendata.nationalrail.co.uk/
2. Using the fares API feed, download and move all of the files into the `atoc/` directory
3. Run `MakeFaresJson.java`. It should take less than an hour to run on a reasonably modern PC, depending on your CPU.

## Updating TfL data
Run `RebuildTflFaresData.java`. It will look at any stations with the tag TFLFARE in `stations.json` and attempt to fill in any gaps it sees in `tfl.json.gz`. If you want to rebuild the file from scratch, delete it, run as normal and prepare to wait four hours. This is useful if fares have risen across the board, whilst updating gaps is useful if you only need to account for new stations that have been added.

## Useful Resources
For adding new stations to stations.json:
* https://www.railfuture.org.uk/New+stations
* http://www.railwaycodes.org.uk/crs/crsb.shtm
