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

fetch(get_name_uri).then(res => res.json()).then(function (result) {
    sname = result.sname;
    username_block.innerHTML = sname + "(" + sid + ")";
})

//左侧导航栏
home_left.addEventListener("click", function () {
    location.href = "E://学习/大二下/数据库/Project/web/student_main.html?sid=" + sid;
}, false);

profile_left.addEventListener("click", function () {
    location.href = "E://学习/大二下/数据库/Project/web/student_account.html?sid=" + sid;
}, false);

score_left.addEventListener("click", function () {
    location.href = "E://学习/大二下/数据库/Project/web/student_score.html?sid=" + sid;
}, false);

select_left.addEventListener("click", function () {
    location.href = "E://学习/大二下/数据库/Project/web/student_select_course.html?sid=" + sid;
}, false);

//顶端退出
logout_block.addEventListener("click", function () {
    location.href = "E://学习/大二下/数据库/Project/web/login.html";
})

//成绩
var cid_button = document.getElementById("cid_button");
var cname_button = document.getElementById("cname_button");
cid_button.addEventListener("click", cidFunction, false);
cname_button.addEventListener("click", cnameFunction, false);

function cidFunction() {
    var cid = document.getElementById("cid").value;
    var cid_uri = "http://localhost:80/api/get_student_grade?studentFieldName=sid&studentValue=" + sid + "&courseFieldName=cid&courseValue=" + cid;
    fetch(cid_uri).then(res => res.json()).then(function (result) {
        var table = document.getElementById("score_table");
        score_table.innerHTML = "";
        console.log(result);
        if (result[0].code === "500") {
            alert("不存在选课记录");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">课程编号</td>"
            + "<td class=\"cell\">课程名</td>"
            + "<td class=\"cell\">任课教师</td>"
            + "<td class=\"cell\">学分</td>"
            + "<td class=\"cell\">年份</td>"
            + "<td class=\"cell\">成绩</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let score = result[i].score;
            if (score === -1){
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
}

function cnameFunction() {
    var cid = document.getElementById("cname").value;
    var cid_uri = "http://localhost:80/api/get_student_grade?studentFieldName=sid&studentValue=" + sid + "&courseFieldName=cname&courseValue=" + cid;
    fetch(cid_uri).then(res => res.json()).then(function (result) {
        var table = document.getElementById("score_table");
        table.innerHTML = "";
        if (result[0].code === "500") {
            alert("不存在选课记录");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">课程编号</td>"
            + "<td class=\"cell\">课程名</td>"
            + "<td class=\"cell\">任课教师</td>"
            + "<td class=\"cell\">学分</td>"
            + "<td class=\"cell\">年份</td>"
            + "<td class=\"cell\">成绩</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let score = result[i].score;
            if (score === -1){
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
}

//平均成绩
var average_uri = "http://localhost:80/api/get_student_average?fieldName=sid&value=" + sid;
fetch(average_uri).then(res => res.json()).then(function (result) {
    var average = document.getElementById("average");
    average.innerHTML = result.average;
})
