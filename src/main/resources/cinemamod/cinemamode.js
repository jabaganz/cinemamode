function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
    ev.preventDefault();
    var data = ev.dataTransfer.getData("text");
    ev.currentTarget.appendChild(document.getElementById(data));
}


function queryMmovies() {
    $.get("http://localhost:8090/getMovies", function (data, status) {
        searchMovies(data);
    });
}


function searchMovies(data) {
    var innerHtml = "";
    var movies = $.parseJSON(data);
    for (var i = 0; i < movies.length; i++) {
        innerHtml += '<option value="' + movies[i].movieid + '">';
        innerHtml += movies[i].label;
        innerHtml += '</option>';
    }
    document.getElementById("movieSelection").innerHTML = innerHtml;
}