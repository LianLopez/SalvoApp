function showOutput(text) {
    $("#list").text(text);
  }

function loadData() {
    $.get("/api/games")
    .done(function(data) {
      showOutput(JSON.stringify(data, null, 2));
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }

  loadData()