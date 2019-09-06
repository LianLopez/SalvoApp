$(function() {
    loadData();
});

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};

function loadData(){
    $.get('/api/game_view/'+getParameterByName('gamePlayer'))
        .done(function(data) {
            console.log(data)
            let playerInfo;
            if(data.gamePlayer[0].id == getParameterByName('gamePlayer'))
                playerInfo = [data.gamePlayer[0].player.email,data.gamePlayer[1].player.email];
            else
                playerInfo = [data.gamePlayer[1].player.email,data.gamePlayer[0].player.email];

            $('#playerInfo').text(playerInfo[0] + '(you) vs ' + playerInfo[1]);

            data.ships.forEach(function(shipPiece){
                shipPiece.shipLocations.forEach(function(shipLocation){
                    $('#'+shipLocation).addClass('ship-piece');
                })
            });
        })
        .fail(function( jqXHR, textStatus ) {
          alert( "Failed: " + textStatus );
        });
};