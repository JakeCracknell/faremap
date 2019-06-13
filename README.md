# faremap
Mapping UK train fares and split tickets, using data from ATOC and TfL.

http://faremap.ml

![website preview](https://raw.githubusercontent.com/JakeCracknell/fare_map/master/preview.png)

## Running locally
To generate the fare data, you will need Java 8+ and Maven 2+.

You can start a local web server in `web/`. You will see the station data, but not any fares data, as this totals 16 GB and hence does not belong in a GitHub repo.

To generate the JSON files in `web/data/fares/`:
1. Register here: http://data.atoc.org/
2. Download the latest fares feed and copy all of the files into the `atoc/` directory
3. Run `MakeFilesJson.java`. It should take less than an hour to run on a reasonably modern PC.


## Rebuilding TfL data
Run `RebuildTflFaresData.java`. It will look at any stations with the tag OYSTER in `stations.json` and attempt to fill in any gaps it sees in `tfl.json.gz`. If you want to rebuild the file from scratch, delete it, run as normal and prepare to wait a few hours.