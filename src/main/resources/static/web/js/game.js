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

function login(evt) {
  evt.preventDefault();
  var form = evt.target.form;
  $.post("/api/login",
         { name: form["username"].value,
           pwd: form["password"].value })
   .done(console.log("loggeado"))
   .fail(function (jqXHR, textStatus) {
           alert('Failed: ' + textStatus);
         });
}

function logout(evt) {
  evt.preventDefault();
  $.post("/api/logout")
   .done(console.log("desloggeado"))
   .fail(function (jqXHR, textStatus) {
           alert('Failed: ' + textStatus);
         });
}

}