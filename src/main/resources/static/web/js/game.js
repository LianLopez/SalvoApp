var app = new Vue({
    el: "#app",
    data: {
           players:[],
           games: [],
           currentUser: ""
}
})

$(function () {
  loadData();
  cargarUsuario();
});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function loadData() {
  $.get('/api/leaderboard')
  .done(function (data){
    app.players = data;
    })
  .fail(function (jqXHR, textStatus) {
        alert('Failed: ' + textStatus);
      });



}

    function cargarUsuario(){
     $.get("/api/games")
           .done(function(data){
               app.games = data.games;
               app.currentUser = data.player.email;
               console.log(data.player.email)
               })
            .fail(function (jqXHR, textStatus) {
               alert('Failed: ' + textStatus);
            })
    }

function login() {
     if(app.currentUser == "Guest"){
     var form = document.getElementById('login-form')
             $.post("/api/login",
                          { username: form["username"].value,
                            password: form["password"].value })
                         .done(app.currentUser = form["username"])
                         .fail(function (jqXHR, textStatus) {
                            alert('Failed: ' + textStatus);
                          });
    }else{
        console.log("Ya existe un usuario")
    }

    }

    function logout() {
      $.post("/api/logout")
       .done(cargarUsuario())
       .fail(function (jqXHR, textStatus) {
            alert('Failed: ' + textStatus);
        });
    }

