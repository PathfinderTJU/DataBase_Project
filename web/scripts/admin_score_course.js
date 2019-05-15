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

var cid_button = document.getElementById("cid_button");
var cname_button = document.getElementById("cname_button");
var average_block = document.getElementById("average");
cid_button.addEventListener("click", cidFunction, false);
cname_button.addEventListener("click", cnameFunction, false);

function cidFunction(){
    let cid = document.getElementById("cid").value;
    
    let uri = "http://localhost:80/api/get_course_grade_all?fieldName=cid&value=" + cid;
    fetch(uri).then(res => res.json()).then(function(result){
        var table = document.getElementById("score_table");
        score_table.innerHTML = "";
        if (result[0].code === "500") {
            alert("不存在成绩");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">学号</td>"
            + "<td class=\"cell\">姓名</td>"
            + "<td class=\"cell\">年级</td>"
            + "<td class=\"cell\">班级</td>"
            + "<td class=\"cell\">年份</td>"
            + "<td class=\"cell\">成绩</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let score = result[i].score;
            if (score === "-1"){
                score = "未考试";
            }
            table.innerHTML += "<tr id=\"newRow\">"
                + "<td class=\"cell\">" + result[i].sid + "</td>"
                + "<td class=\"cell\">" + result[i].sname + "</td>"
                + "<td class=\"cell\">" + result[i].grade + "</td>"
                + "<td class=\"cell\">" + result[i].class + "</td>"
                + "<td class=\"cell\">" + result[i].time + "</td>"
                + "<td class=\"cell\">" + score + "</td>"
                + "</tr>";
        }
    })
    var average_uri = "http://localhost:80/api/get_course_average?fieldName=cid&value=" + cid;
    fetch(average_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            average_block.innerHTML = "无成绩";
        }else{
            average_block.innerHTML = result.average;
        }
    })

    var chartData = [];
    var range_uri = "http://localhost:80/api/get_course_range?fieldName=cid&value=" + cid + "&low=";
    fetch(range_uri + "90&high=100").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "80&high=90").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "70&high=80").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "60&high=70").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "0&high=60").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    }).then(function(result){
        var ctx = document.getElementById('chart').getContext('2d');
        chartData = chartData.reverse();
        var data = {
            labels: ["E: 不及格", "D: 60-70", "C: 70-80", "B: 80-90", "A: 90-100"],
            datasets: [{
                label: "人数",
                data: chartData,
                backgroundColor: "#224797",
                borderWidth: "70"
            }]
        }
        var myBarChart = new Chart(ctx, {
            type: 'bar',
            data: data
        });
    })
}

function cnameFunction(){
    let cname = document.getElementById("cname").value;
    
    let uri = "http://localhost:80/api/get_course_grade_all?fieldName=cname&value=" + cname;
    fetch(uri).then(res => res.json()).then(function(result){
        var table = document.getElementById("score_table");
        score_table.innerHTML = "";
        if (result[0].code === "500") {
            alert("不存在成绩");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">学号</td>"
            + "<td class=\"cell\">姓名</td>"
            + "<td class=\"cell\">年级</td>"
            + "<td class=\"cell\">班级</td>"
            + "<td class=\"cell\">年份</td>"
            + "<td class=\"cell\">成绩</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let score = result[i].score;
            if (score === "-1"){
                score = "未考试";
            }
            table.innerHTML += "<tr id=\"newRow\">"
                + "<td class=\"cell\">" + result[i].sid + "</td>"
                + "<td class=\"cell\">" + result[i].sname + "</td>"
                + "<td class=\"cell\">" + result[i].grade + "</td>"
                + "<td class=\"cell\">" + result[i].class + "</td>"
                + "<td class=\"cell\">" + result[i].time + "</td>"
                + "<td class=\"cell\">" + score + "</td>"
                + "</tr>";
        }
    })
    var average_uri = "http://localhost:80/api/get_course_average?fieldName=cname&value=" + cname;
    fetch(average_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            average_block.innerHTML = "无成绩";
        }else{
            average_block.innerHTML = result.average;
        }
    })

    //图标
    var chartData = [];
    var range_uri = "http://localhost:80/api/get_course_range?fieldName=cname&value=" + cname + "&low=";
    fetch(range_uri + "90&high=100").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "80&high=90").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "70&high=80").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "60&high=70").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    });
    fetch(range_uri + "0&high=60").then(res => res.json()).then(function(res){
        if (res.code === "500"){
            chartData.push(0);
        }else{
            chartData.push(parseInt(res.count));
        }
    }).then(function(result){
        var ctx = document.getElementById('chart').getContext('2d');
        chartData = chartData.reverse();
        var data = {
            labels: ["E: 不及格", "D: 60-70", "C: 70-80", "B: 80-90", "A: 90-100"],
            datasets: [{
                label: "人数",
                data: chartData,
                backgroundColor: "#224797",
                borderWidth: "70"
            }]
        }
        var myBarChart = new Chart(ctx, {
            type: 'bar',
            data: data
        });
    })
}
