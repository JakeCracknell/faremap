function initialiseColorKey() {
    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    });
    $("#color-key")
        .mousemove(showPriceForColorKeyHover)
        .mouseleave(() => hidePolygonsWithPriceGreaterThan(maxPriceCurrentlyDisplayed));
}

function showColorKeyIfFaresExist() {
    $('#color-key')[maxPriceCurrentlyDisplayed > 0 ? 'fadeIn' : 'fadeOut']();
}

function showPriceForColorKeyHover(e) {
    const price = Math.max(0, maxPriceCurrentlyDisplayed * (e.offsetX / e.target.clientWidth));
    $("#color-key")
        .attr('data-original-title', formatPrice(price))
        .tooltip('show');
    hidePolygonsWithPriceGreaterThan(price);
}

function hidePolygonsWithPriceGreaterThan(price) {
    d3.select("#map-svg-overlay").select("g").selectAll("g")
        .style("opacity", s => (s.fareSet.preferred && s.fareSet.preferred.price <= price) ? 1 : 0);
}
