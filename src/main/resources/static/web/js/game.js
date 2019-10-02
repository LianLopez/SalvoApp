var app = new Vue({
    el: "#app",
    data: {
           players:[]
}
})

$(function () {
  loadData();
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

function login() {
      var form = document.getElementById('login-form')
      $.post("/api/login",
             { username: form["username"].value,
               password: form["password"].value })
             .done(console.log("logeado"))
       .fail(function (jqXHR, textStatus) {
               alert('Failed: ' + textStatus);
             });
    }

    function logout() {
      $.post("/api/logout")
       .done(console.log("desloggeado"))
       .fail(function (jqXHR, textStatus) {
               alert('Failed: ' + textStatus);
             });
    }
