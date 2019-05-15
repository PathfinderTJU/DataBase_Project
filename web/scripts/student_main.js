"use script"
//获取用户名，学号，显示右上
var sid = getQueryString("sid");
var sname = "";
var username_block = document.getElementById("username");
var logout_block = document.getElementById("logout");
var home_left = document.getElementById("home_left");
var profile_left = document.getElementById("profile_left");
var score_left = document.getElementById("score_left");
var select_left = document.getElementById("select_left");

function getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}

let get_name_uri = "http://localhost:80/api/get_student_basic?fieldName=sid&value=" + sid;

fetch(get_name_uri).then(res => res.json()).then(function(result){
    sname = result.sname;
    username_block.innerHTML = sname + "(" + sid + ")";
    var welcome_block = document.getElementById("welcome");
    welcome_block.innerHTML = "您好，" + sname + "(" + sid + ")";
    console.log(sname);
})

//左侧导航栏
home_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/student_main.html?sid=" + sid;
}, false);

profile_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/student_account.html?sid=" + sid;
}, false);

score_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/student_score.html?sid=" + sid;
}, false);

select_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/student_select_course.html?sid=" + sid;
}, false);

//顶端退出
logout_block.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/login.html";
})