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

var sid_button = document.getElementById("sid_button");
var sname_button = document.getElementById("sname_button");
sid_button.addEventListener("click", sidFunction, false);
sname_button.addEventListener("click", snameFunction, false);

function sidFunction() {
    let sid = document.getElementById("sid").value;
    let sid_uri = "http://localhost:80/api/get_student_all_grade?fieldName=sid&value=" + sid;
    fetch(sid_uri).then(res => res.json()).then(function (result) {
        let table = document.getElementById("course_table");
        table.innerHTML = "";
        if (result[0].code === "500") {
            alert("不存在选课记录");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">课程编号</td>"
            + "<td class=\"cell\">课程名</td>"
            + "<td class=\"cell\">老师</td>"
            + "<td class=\"cell\">学分</td>"
            + "<td class=\"cell\">适合年级</td>"
            + "<td class=\"cell\">取消年份</td>"
            + "<td class=\"cell\">年份</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let cancel = result[i].cancel_year;
            if (cancel === "-1"){
                cancel = "未取消";
            }
            table.innerHTML += "<tr id=\"newRow\">"
                + "<td class=\"cell\">" + result[i].cid + "</td>"
                + "<td class=\"cell\">" + result[i].cname + "</td>"
                + "<td class=\"cell\">" + result[i].teacher + "</td>"
                + "<td class=\"cell\">" + result[i].point + "</td>"
                + "<td class=\"cell\">" + result[i].suit_year + "</td>"
                + "<td class=\"cell\">" + cancel + "</td>"
                + "<td class=\"cell\">" + result[i].time + "</td>"
                + "</tr>";
        }
    })
}

function snameFunction() {
    let sname = document.getElementById("sname").value;
    let sname_uri = "http://localhost:80/api/get_student_all_grade?fieldName=sname&value=" + sname;
    fetch(sname_uri).then(res => res.json()).then(function (result) {
        let table = document.getElementById("course_table");
        table.innerHTML = "";
        if (result[0].code === "500") {
            alert("不存在选课记录");
            location.reload();
        }
        table.innerHTML = "<tr>"
            + "<td class=\"cell\">课程编号</td>"
            + "<td class=\"cell\">课程名</td>"
            + "<td class=\"cell\">老师</td>"
            + "<td class=\"cell\">学分</td>"
            + "<td class=\"cell\">适合年级</td>"
            + "<td class=\"cell\">取消年份</td>"
            + "<td class=\"cell\">年份</td>"
            + "</tr>"
        for (let i = 0; i < result.length; i++) {
            let cancel = result[i].cancel_year;
            if (cancel === "-1"){
                cancel = "未取消";
            }
            table.innerHTML += "<tr id=\"newRow\">"
                + "<td class=\"cell\">" + result[i].cid + "</td>"
                + "<td class=\"cell\">" + result[i].cname + "</td>"
                + "<td class=\"cell\">" + result[i].teacher + "</td>"
                + "<td class=\"cell\">" + result[i].point + "</td>"
                + "<td class=\"cell\">" + result[i].suit_year + "</td>"
                + "<td class=\"cell\">" + cancel + "</td>"
                + "<td class=\"cell\">" + result[i].time + "</td>"
                + "</tr>";
        }
    })
}

var add_button = document.getElementById("add_button");
var delete_button = document.getElementById("delete_button");

add_button.addEventListener("click", addFunction, false);
delete_button.addEventListener("click", deleteFunction, false);

function addFunction(){
    let sid = document.getElementById("sid2").value;
    let cid = document.getElementById("cid2").value;
    let time = document.getElementById("time").value;
    let add_uri = "http://localhost:80/api/add_record?sid=" + sid + "&cid=" + cid + "&time=" + time;
    fetch(add_uri).then(res => res.json()).then(function(result){
        console.log(result);
        if(result.code === "500"){
            alert("选课失败，课程不存在或已被选择");
            location.reload();
        }else if(result.code === "400"){
            alert("选课失败，年级必须满足课程需求且选课时间早于课程取消时间");
            location.reload();
        }else{
            if(result.status === false){
                alert("选课失败, 参数错误");
                location.reload();
            }else{
                alert("选课成功");
                location.reload();
            }
        }
    })
}

function deleteFunction(){
    let sid = document.getElementById("sid3").value;
    let cid = document.getElementById("cid3").value;
    let time = document.getElementById("time2").value;
    let delete_uri = "http://localhost:80/api/delete_record?sid=" + sid + "&cid=" + cid + "&time=" + time;
    fetch(delete_uri).then(res => res.json()).then(function(result){
        console.log(result);
        if(result.code === "500"){
            alert("退课失败，课程不存在或已被选择");
            location.reload();
        }else{
            if(result.status === false){
                alert("退课失败");
                location.reload();
            }else{
                alert("退课成功");
                location.reload();
            }
        }
    })
}