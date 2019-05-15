"use script"
//获取用户名，学号，显示右上
var logout_block = document.getElementById("logout");
var home_left = document.getElementById("home_left");
var student_left = document.getElementById("student_left");
var score_left = document.getElementById("score_left");
var course_left = document.getElementById("course_left");
var select_left = document.getElementById("select_left");

//左侧导航栏
home_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_main.html";
}, false);

student_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_student.html";
}, false);

score_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_score_student.html";
}, false);

course_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_course.html";
}, false);

select_left.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_select_course.html";
}, false);

//顶端退出
logout_block.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/login.html";
})