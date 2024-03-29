var app = new Vue({
  el: "#app",
  data: {
    players: [],
    games: [],
    currentUser: "Guest",
    username:"",
    password:""
  },
  methods: {
    joinGameAjax : function (gameId){
                       $.post("/api/games/"+gameId+"/players")
                       .done(function (data){
                        returnGame(data.gpId, "NO")
                        })
                       .fail(function (jqXHR, textStatus) {
                             console.log(jqXHR, textStatus)
                           });
                    },
    returnGame : function (gpId, hasShips){
                      if(hasShips=="YES"){
                              window.location.href = "/web/game.html?gp=" +gpId;
                              } else {
                              window.location.href = "/web/grid.html?gp=" +gpId;
                              }
                    }
    }

})

$(function () {
  loadData();
  cargarUsuario();
});



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
    $.post("/api/games")
    .done(function(data, textStatus){
        if(data.status == 200){
           app.returnGame(data.gpid);
        }else{
            alert("bad response");
        }
    })
    .fail(function (jqXHR, textStatus) {
          alert('Failed: ' + textStatus);
        })
}

function login() {
                 if (app.currentUser == "Guest") {
                     $.post("/api/login", {
                         username: app.username,
                         password: app.password
                       })
                       .done(setTimeout(function(){ cargarUsuario(); }, 1000))
                       .fail(function (jqXHR, textStatus) {
                         alert('Failed: ' + jqXHR.status);
                       });
                   } else {
                     console.log("Ya existe un usuario");
                     window.reload();
                   }
             }
function register() {
                 $.post("/api/players", {
                         username: app.username,
                         password: app.password
                     })
                     .done(function () {
                             login();
                           })
                     .fail(function (jqXHR, textStatus) {
                             if(jqXHR.status == 401 ){
                                       alert("failed: no tenes permisos")
                                       console.log(textStatus)
                                       }
                           });

             }

function logout() {
  $.post("/api/logout")
    .done(window.location.replace("games.html"))
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}