var app = new Vue({
  el: "#app",
  data: {
    players: [],
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
    .done(function (data) {
      app.players = data;
    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}

function cargarUsuario() {
  $.get("/api/games")
    .done(function (data) {
      app.games = data.games.reverse();
      app.currentUser = data.player.email;
      console.log(data.player.email)
    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    })
}

function createGame(){

}

function joinGame(gpid){
    location.href = "/web/games.html?gp="+gpid;
}
function register(){
    var form = document.getElementById("register-form");
    $.post("/api/players", {
        email: form["username"].value,
        password: form["password"].value
    })
    .done(function () {
            alert('Success');
          })
    .done(function (){
      location.reload();
    })
    .fail(function (jqXHR, textStatus) {
            alert('Failed: ' + jqXHR.status);
          });
          if(jqXHR.status == 401 ){
          alert("failed: ")
          }
}

function login() {
  if (app.currentUser == "Guest") {
    var form = document.getElementById('login-form')
    $.post("/api/login", {
        username: form["username"].value,
        password: form["password"].value
      })
      .done(setTimeout(function(){ cargarUsuario(); }, 1000))
      .fail(function (jqXHR, textStatus) {
        alert('Failed: ' + jqXHR.status);
      });
  } else {
    console.log("Ya existe un usuario")
  }
}

function logout() {
  $.post("/api/logout")
    .done(window.location.replace("game.html"))
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}