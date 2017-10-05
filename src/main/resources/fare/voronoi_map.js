addShowHideEventsTo = function (selector) {
    d3.select(selector).select('.hide').on('click', function () {
        d3.select(selector)
            .classed('visible', false)
            .classed('hidden', true);
    });

    d3.select(selector).select('.show').on('click', function () {
        d3.select(selector)
            .classed('visible', true)
            .classed('hidden', false);
    });
};

function formatPrice(price) {
    return "£" + price.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}

voronoiMap = function (map, url) {
    var pointModes = d3.map(),
        points = [],
        pointsMap = {},
        lastSelectedPoint,
        maxFarePrice;

    var voronoi = d3.geom.voronoi()
        .x(function (d) {
            return d.x;
        })
        .y(function (d) {
            return d.y;
        });

    var selectPointForFareQuery = function () {
        d3.selectAll('.selected').classed('selected', false);

        var cell = d3.select(this),
            point = cell.datum();

        lastSelectedPoint = point;
        cell.classed('selected', true);

        fareUrl = "/api/fare/from/" + point.stationId
        d3.json(fareUrl, function (json) {
            pointsMap.forEach(function (station, stationId, m) {
                station.fares = []
            });
            for (var toStationId in json.fares) {
                pointsMap.get(toStationId).fares = json.fares[toStationId];
            }
            drawWithLoading();
        })
    };

    var getFillColourForAdjustedPrice = function (price) {
        if (price === Infinity || price === -Infinity || !price) return 'transparent';
        var hue = (price * 360).toString(10);
        return ["hsla(", hue, ",100%,50%,0.5)"].join("");
    };

    function getFormattedStation(station) {
        if (station === null || station === undefined) return "???";
        let stationString = station.stationId + " " + station.stationName;
        if (station.crs !== null && station.crs !== undefined) {
            stationString += " (" + station.crs + ")"
        }
        return stationString;
    }

    var showMouseOverInformationForPoint = function () {
        const cell = d3.select(this);
        const point = cell.datum();
        const faresTable = document.getElementById("fare-table");
        faresTable.innerHTML = "";
        if (point.fares.length > 0) {
            document.getElementById("selected-source-destination").textContent =
                getFormattedStation(lastSelectedPoint) + " → " + getFormattedStation(point);
            const mainPrice = getFareSelectorFunction()(point.fares);
            const fareColour = getFillColourForAdjustedPrice(mainPrice / maxFarePrice);
            const faresToDisplay = point.fares.filter(getFareTypeSelectorFilterFunction());
            document.getElementById("selected-main-price").style.visibility = "visible";
            document.getElementById("selected-main-price").textContent = formatPrice(mainPrice);
            document.getElementById("selected-main-price").style.backgroundColor = fareColour;

            for (i = 0; i < faresToDisplay.length; i++) {
                var tr = document.createElement("tr");
                var td = document.createElement("td");
                td.classList.add("fare-type", faresToDisplay[i].isTFL ? "tfl" : "nr");
                tr.appendChild(td);
                var td = document.createElement("td");
                td.appendChild(document.createTextNode(formatPrice(faresToDisplay[i].price)));
                tr.appendChild(td);
                var td = document.createElement("td");
                td.appendChild(document.createTextNode(faresToDisplay[i].routeDescription));
                tr.appendChild(td);
                faresTable.appendChild(tr);
            }
        } else {
            document.getElementById("selected-source-destination").textContent = getFormattedStation(point);
            document.getElementById("selected-main-price").style.visibility = "hidden"
        }

    };

    var setupDisplayOptionsPanel = function () {
        addShowHideEventsTo('#selections');
        d3.selectAll('#mode-toggles, #fare-type-toggles')
            .on("change", drawWithLoading);
    };

    function getFareTypeSelectorFilterFunction() {
        const fareTypesSelected = getSelectedCheckboxesFromGroup('#fare-type-toggles');
        return f => (f.offPeakOnly === fareTypesSelected.includes('off-peak')) &&
            ((f.isTFL && fareTypesSelected.includes('tfl')) || (!f.isTFL && fareTypesSelected.includes('national-rail')));
    }

    function getFareSelectorFunction() {
        const fareTypeSelectorFilter = getFareTypeSelectorFilterFunction();
        const fareSelectorElement = document.getElementById('fare-price-selector');
        const primaryFareSelector = fareSelectorElement.options[fareSelectorElement.selectedIndex].id;
        if (primaryFareSelector === 'low') {
            return fs => Math.min.apply(Math, fs.filter(fareTypeSelectorFilter).map(function (f) {
                return f.price;
            }));
        } else if (primaryFareSelector === 'high') {
            return fs => Math.max.apply(Math, fs.filter(fareTypeSelectorFilter).map(function (f) {
                return f.price;
            }));
        } else {
            return fs => Math.min.apply(Math, fs.filter(fareTypeSelectorFilter).filter(f => f.isDefaultRoute).map(function (f) {
                return f.price;
            }));
        }
    };

    var getSelectedCheckboxesFromGroup = function (selector) {
        checkedInputs = document.querySelectorAll(selector + ' input[type=checkbox]:checked');
        return [].slice.call(checkedInputs).map(function (c) {
            return c.value;
        });
    };

    var pointsFilteredToSelectedModes = function () {
        var currentSelectedModes = d3.set(getSelectedCheckboxesFromGroup('#mode-toggles'));
        return points.filter(function (item) {
            return item.modes.some(m => currentSelectedModes.has(m));
        });
    };

    var drawWithLoading = function (e) {
        d3.select('#loading').classed('visible', true);
        if (e && e.type == 'viewreset') {
            d3.select('#overlay').remove();
        }
        setTimeout(function () {
            draw();
            d3.select('#loading').classed('visible', false);
        }, 0);
    };

    var draw = function () {
        d3.select('#overlay').remove();

        var bounds = map.getBounds(),
            topLeft = map.latLngToLayerPoint(bounds.getNorthWest()),
            bottomRight = map.latLngToLayerPoint(bounds.getSouthEast()),
            existing = d3.set(),
            drawLimit = bounds.pad(0.4);
        var fareSelectorFunction = getFareSelectorFunction();


        filteredPoints = pointsFilteredToSelectedModes().filter(function (d) {
            var latlng = new L.LatLng(d.latitude, d.longitude);

            if (!drawLimit.contains(latlng)) {
                return false
            }

            var point = map.latLngToLayerPoint(latlng);

            key = point.toString();
            if (existing.has(key)) {
                return false
            }

            existing.add(key);

            d.x = point.x;
            d.y = point.y;
            return true;
        });

        maxFarePrice = filteredPoints.reduce(function (currentMax, thisPoint) {
            let fare = fareSelectorFunction(thisPoint.fares);
            if (fare !== Infinity) {
                return Math.max(currentMax, fare);
            } else {
                return currentMax;
            }
        }, 0);

        voronoi(filteredPoints).forEach(function (d) {
            d.point.cell = d;
        });


        var svg = d3.select(map.getPanes().overlayPane).append("svg")
            .attr('id', 'overlay')
            .attr("class", "leaflet-zoom-hide")
            .style("width", map.getSize().x + 'px')
            .style("height", map.getSize().y + 'px')
            .style("margin-left", topLeft.x + "px")
            .style("margin-top", topLeft.y + "px");

        var g = svg.append("g")
            .attr("transform", "translate(" + (-topLeft.x) + "," + (-topLeft.y) + ")");

        var svgPoints = g.attr("class", "points")
            .selectAll("g")
            .data(filteredPoints)
            .enter().append("g")
            .attr("class", "point");

        var buildPathFromPoint = function (point) {
            return "M" + point.cell.join("L") + "Z";
        };

        svgPoints.append("path")
            .attr("class", "point-cell")
            .attr("d", buildPathFromPoint)
            .style('fill', function (d) {
                return getFillColourForAdjustedPrice(fareSelectorFunction(d.fares) / maxFarePrice)
            })
            .on('click', selectPointForFareQuery)
            .on('mouseover', showMouseOverInformationForPoint)
            .classed("selected", function (d) {
                return lastSelectedPoint == d;
            })
            .classed("nodata", function (d) {
                return d.fares.length === 0;
            });

        svgPoints.append("circle")
            .attr("transform", function (d) {
                return "translate(" + d.x + "," + d.y + ")";
            })
            .style('fill', function (d) {
                return '#' + d.color
            })
            .attr("r", 2);
    };

    var mapLayer = {
        onAdd: function (map) {
            map.on('viewreset moveend', drawWithLoading);
            drawWithLoading();
        }
    };

    addShowHideEventsTo('#about');

    map.on('ready', function () {
        d3.json(url, function (json) {
            points = json;
            points.forEach(function (point) {
                point.modes.forEach(m => pointModes.set(m, {mode: m, color: 'black'}));
                point.fares = [];
            });
            pointsMap = new Map(points.map((p) => [p.stationId, p]));
            setupDisplayOptionsPanel();
            map.addLayer(mapLayer);
        })
    });
};