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

var all_uri="http://localhost:80/api/get_all_course";

fetch(all_uri).then(res => res.json()).then(function(result){
    var course_table = document.getElementById("course_table");
    number_course = result.length;
    for (let i = 0 ; i < result.length ; i++){
        let cancel_year = result[i].cancel_year;
        if (cancel_year === "-1"){
            cancel_year = "未取消";
        }
        course_table.innerHTML += "<tr>"
                         + "<td class=\"cell\">" + result[i].cid + "</td>"
                         + "<td class=\"cell\">" + result[i].cname + "</td>"
                         + "<td class=\"cell\">" + result[i].teacher + "</td>"
                         + "<td class=\"cell\">" + result[i].point + "</td>"
                         + "<td class=\"cell\">" + result[i].suit_year + "</td>"
                         + "<td class=\"cell\">" + cancel_year + "</td>"
                         + "</tr>";
    }
})

var select_uri="http://localhost:80/api/get_student_all_grade?fieldName=sid&value=" + sid;

fetch(select_uri).then(res => res.json()).then(function(result){
    var select_table = document.getElementById("select_table");
    for (let i = 0 ; i < result.length ; i++){
        let cancel_year = result[i].cancel_year;
        if (cancel_year === "-1"){
            cancel_year = "未取消";
        }
        select_table.innerHTML += "<tr>"
                         + "<td class=\"cell\">" + result[i].cid + "</td>"
                         + "<td class=\"cell\">" + result[i].cname + "</td>"
                         + "<td class=\"cell\">" + result[i].teacher + "</td>"
                         + "<td class=\"cell\">" + result[i].point + "</td>"
                         + "<td class=\"cell\">" + result[i].suit_year + "</td>"
                         + "<td class=\"cell\">" + cancel_year + "</td>"
                         + "<td class=\"cell\">" + result[i].time + "</td>"
                         + "</tr>";
    }
})

var add_button = document.getElementById("add_button");
var delete_button = document.getElementById("delete_button");

add_button.addEventListener("click", addFunction, false);
delete_button.addEventListener("click", deleteFunction, false);

function addFunction(){
    let cid1 = document.getElementById("cid1").value;
    let time1 = document.getElementById("time1").value;
    let add_uri = "http://localhost:80/api/student_select_course?sid=" + sid + "&cid=" + cid1 + "&time=" + time1;
    fetch(add_uri).then(res => res.json()).then(function(result){
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
    let cid2 = document.getElementById("cid2").value;
    let time2 = document.getElementById("time2").value;
    let delete_uri = "http://localhost:80/api/student_delete_course?sid=" + sid + "&cid=" + cid2 + "&time=" + time2;
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