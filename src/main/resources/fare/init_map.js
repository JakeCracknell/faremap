L.mapbox.accessToken = 'pk.eyJ1IjoiemV0dGVyIiwiYSI6ImVvQ3FGVlEifQ.jGp_PWb6xineYqezpSd7wA';

const map = L.mapbox.map('map', 'mapbox.streets-basic', {
    zoomControl: false,
    maxBounds:[[58.62, -5.88], [50.00, 1.76]], //GB
    minZoom: 7,
    maxZoom: 15
}).fitBounds([[51.92, 0.61], [51.11, -1.12]]); //London

map.doubleClickZoom.disable();

//TODO refactor all of this. work out which fields are actually needed, replace name with formattedName etc.

map.on('ready', function () {
    d3.json('/api/station', function (json) {
        json.forEach(s => s.name=formatStationName(s));
        stationsByIdMap = new Map(json.map((p) => [p.stationId, p]));
        map.addLayer({
            onAdd: function (map) {
                map.on('viewreset moveend', drawWithLoading);
                drawWithLoading();
            }
        });
        const typeaheadSource = new Bloodhound({
            datumTokenizer: Bloodhound.tokenizers.obj.nonword('name'),
            queryTokenizer: Bloodhound.tokenizers.nonword,
            local: json,
            identify: s => s.stationId
        });

        $("#station-picker-input").typeahead({
                hint: true,
                highlight: true,
                minLength: 1
            },
            {
                source: typeaheadSource.ttAdapter(),
                displayKey: 'name',
                name:'station',
                hint:true
            }
        ).bind('typeahead:select', onTypeAheadStationSelect);
        typeaheadSource.initialize();
    })
});

$('input[name="routePreferenceRadios"]:radio, input[name="travelTimeRadios"]:radio').change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(
        document.querySelector('input[name="routePreferenceRadios"]:checked').value,
        document.querySelector('input[name="travelTimeRadios"]:checked').value
    );
    drawWithLoading(e);
});
