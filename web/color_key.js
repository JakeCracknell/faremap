let colorKeyCanHover = true;

function initialiseColorKey() {
    $(function () {
        $('[data-toggle="tooltip"]').tooltip( {trigger: 'manual'} )
    });
    $("#color-key")
        .mousemove(showPriceForColorKeyHover)
        .mouseleave(handleMouseLeaveColorKey)
        .click(handleColorKeyClick);
}

function showColorKeyIfFaresExist() {
    $('#color-key')[maxPriceCurrentlyDisplayed > 0 ? 'fadeIn' : 'fadeOut']();
}

function showPriceForColorKeyHover(e) {
    if (colorKeyCanHover) {
        const price = Math.max(0, maxPriceCurrentlyDisplayed * (e.offsetX / e.target.clientWidth));
        $("#color-key")
            .attr('data-original-title', formatPrice(price))
            .tooltip('show');
        hidePolygonsWithPriceGreaterThan(price);
    }
}

function handleColorKeyClick() {
    $("#color-key").toggleClass('locked', !(colorKeyCanHover = !colorKeyCanHover));
}

function handleMouseLeaveColorKey() {
    if (colorKeyCanHover) {
        hidePolygonsWithPriceGreaterThan(maxPriceCurrentlyDisplayed);
        $("#color-key").tooltip('hide');
    } else {
        $("#color-key").tooltip('show');
    }
}

function hidePolygonsWithPriceGreaterThan(price) {
    d3.select("#map-svg-overlay").select("g").selectAll("g")
        .style("opacity", s => (s.fareSet.preferred && s.fareSet.preferred.price <= price) ? 1 : 0);
}

function resetColorKey() {
    colorKeyCanHover = true;
    $("#color-key").removeClass('locked');
    $("#color-key").tooltip('hide');
}
