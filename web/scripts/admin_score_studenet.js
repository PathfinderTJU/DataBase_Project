"use script"
//获取用户名，学号，显示右上
var logout_block = document.getElementById("logout");
var home_left = document.getElementById("home_left");
var student_left = document.getElementById("student_left");
var score_left = document.getElementById("score_left");
var course_left = document.getElementById("course_left");
var select_left = document.getElementById("select_left");
var student_top = document.getElementById("student_menu");
var class_top = document.getElementById("class_menu");
var course_top = document.getElementById("course_menu");

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

//顶端跳转
student_top.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_score_student.html";
})

class_top.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_score_class.html";
})

course_top.addEventListener("click", function(){
    location.href = "E://学习/大二下/数据库/Project/web/admin_score_course.html";
})

//查成绩+平均成绩
var sid_button = document.getElementById("sid_button");
var sname_button = document.getElementById("sname_button");
var average_block = document.getElementById("average");
sid_button.addEventListener("click", sidFunction, false);
sname_button.addEventListener("click", snameFunction, false);

function sidFunction() {
    var sid = document.getElementById("sid1").value;
    var sid_uri = "http://localhost:80/api/get_student_all_grade?fieldName=sid&value=" + sid;
    fetch(sid_uri).then(res => res.json()).then(function (result) {
        var table = document.getElementById("score_table");
        score_table.innerHTML = "";
        if (result[0].code === "500") {
            alert("不存在成绩");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">课程编号</td>"
            + "<td class=\"cell\">课程名</td>"
            + "<td class=\"cell\">老师</td>"
            + "<td class=\"cell\">学分</td>"
            + "<td class=\"cell\">年份</td>"
            + "<td class=\"cell\">成绩</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let score = result[i].score;
            if (score === "-1"){
                score = "未考试";
            }
            table.innerHTML += "<tr id=\"newRow\">"
                + "<td class=\"cell\">" + result[i].cid + "</td>"
                + "<td class=\"cell\">" + result[i].cname + "</td>"
                + "<td class=\"cell\">" + result[i].teacher + "</td>"
                + "<td class=\"cell\">" + result[i].point + "</td>"
                + "<td class=\"cell\">" + result[i].time + "</td>"
                + "<td class=\"cell\">" + score + "</td>"
                + "</tr>";
        }
    })
    var average_uri = "http://localhost:80/api/get_student_average?fieldName=sid&value=" + sid;
    fetch(average_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            average_block.innerHTML = "无成绩";
        }else{
            average_block.innerHTML = result.average;
        }
    })
}

function snameFunction() {
    var sname = document.getElementById("sname").value;
    var sname_uri = "http://localhost:80/api/get_student_all_grade?fieldName=sname&value=" + sname;
    fetch(sname_uri).then(res => res.json()).then(function (result) {
        var table = document.getElementById("score_table");
        score_table.innerHTML = "";
        if (result[0].code === "500") {
            alert("不存在成绩");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">课程编号</td>"
            + "<td class=\"cell\">课程名</td>"
            + "<td class=\"cell\">老师</td>"
            + "<td class=\"cell\">学分</td>"
            + "<td class=\"cell\">年份</td>"
            + "<td class=\"cell\">成绩</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let score = result[i].score;
            if (score === "-1"){
                score = "未考试";
            }
            table.innerHTML += "<tr id=\"newRow\">"
                + "<td class=\"cell\">" + result[i].cid + "</td>"
                + "<td class=\"cell\">" + result[i].cname + "</td>"
                + "<td class=\"cell\">" + result[i].teacher + "</td>"
                + "<td class=\"cell\">" + result[i].point + "</td>"
                + "<td  class=\"cell\">" + result[i].time + "</td>"
                + "<td class=\"cell\">" + score + "</td>"
                + "</tr>";
        }
    })
    var average_uri = "http://localhost:80/api/get_student_average?fieldName=sname&value=" + sname;
    fetch(average_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            average_block.innerHTML = "无成绩";
        }else{
            average_block.innerHTML = result.average;
        }
    })
}

//修改成绩或登成绩
var add_button = document.getElementById("add_button");
add_button.addEventListener("click", addFunction, false);
function addFunction(){
    let sid_input = document.getElementById("sid").value;
    let cid_input = document.getElementById("cid").value;
    let time_input = document.getElementById("time").value;
    let score_input = document.getElementById("score").value;
    let update_uri = "http://localhost:80/api/update_score?sid=" + sid_input + "&cid=" + cid_input + "&time=" + time_input + "&score=" + score_input;

    fetch(update_uri).then(res => res.json()).then(function(result){
        if(result.code === "500"){
            alert("不存在选课记录");
            location.reload();
        }else{
            if (result.status === "false"){
                alert("修改失败");
                location.reload();
            }else{
                alert("成绩修改成功")
                location.reload();
            }
        }
    })
}