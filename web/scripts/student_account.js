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
    
    sid_cell.innerHTML=result.sid;
    sname_cell.innerHTML=result.sname;
    if (result.gender === "man"){
        gender_cell.innerHTML = "男";
    }else{
        gender_cell.innerHTML = "女";
    }
    age_cell.innerHTML=result.age;
    grade_cell.innerHTML=result.grade;
    class_cell.innerHTML=result.class;
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

//修改密码
var submit = document.getElementById("submit_button")

submit.addEventListener("click", submitFunction, false);

function submitFunction(){
    let oldPass = document.getElementById("oldPass").value;
    let newPass = document.getElementById("newPass").value;
    let submit_uri = "http://localhost:80/api/update_password?sid=" + sid + "&oldPass=" + oldPass + "&newPass=" + newPass;

    fetch(submit_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            alert("密码错误");
            return;
        }else{
            if (result.status === false){
                alert("密码必须为6-15位");
                location.reload();
            }else{
                alert("修改成功");
                location.reload();
            }
        }
    })
}

//表单数据
var sid_cell = document.getElementById("sid_cell");
var sname_cell = document.getElementById("sname_cell");
var gender_cell = document.getElementById("gender_cell");
var age_cell = document.getElementById("age_cell");
var grade_cell = document.getElementById("grade_cell");
var class_cell = document.getElementById("class_cell");

//更新按钮
var update_sname = document.getElementById("update_sname");
var update_gender = document.getElementById("update_gender");
var update_age = document.getElementById("update_age");
var update_grade = document.getElementById("update_grade");
var update_class = document.getElementById("update_class");

update_sname.addEventListener("click", snameFunction, false);
function snameFunction(){
    let newSname = document.getElementById("sname_input").value;
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid + "&fieldName=sname&value=" + newSname;
    fetch(submit_uri).then(res => res.json()).then(function(result){
        if (result.status === false){
            alert("修改失败");
            location.reload();
        }else{
            alert("修改成功");
            location.reload();
        }
    })
}

update_gender.addEventListener("click", genderFunction, false);
function genderFunction(){
    let newSname = document.getElementById("gender_input").value;
    if (newSname === "男"){
        newSname = "man";
    }else{
        newSname = "women";
    }
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid + "&fieldName=gender&value=" + newSname;
    fetch(submit_uri).then(res => res.json()).then(function(result){
        if (result.status === false){
            alert("修改失败");
            location.reload();
        }else{
            alert("修改成功");
            location.reload();
        }
    })
}

update_age.addEventListener("click", ageFunction, false);
function ageFunction(){
    let newSname = document.getElementById("age_input").value;
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid + "&fieldName=age&value=" + newSname;
    fetch(submit_uri).then(res => res.json()).then(function(result){
        if (result.status === false){
            alert("修改失败");
            location.reload();
        }else{
            alert("修改成功");
            location.reload();
        }
    })
}

update_grade.addEventListener("click", gradeFunction, false);
function gradeFunction(){
    let newSname = document.getElementById("grade_input").value;
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid + "&fieldName=grade&value=" + newSname;
    fetch(submit_uri).then(res => res.json()).then(function(result){
        if (result.status === false){
            alert("修改失败");
            location.reload();
        }else{
            alert("修改成功");
            location.reload();
        }
    })
}

update_class.addEventListener("click", classFunction, false);
function classFunction(){
    let newSname = document.getElementById("class_input").value;
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid + "&fieldName=class&value=" + newSname;
    fetch(submit_uri).then(res => res.json()).then(function(result){
        if (result.status === false){
            alert("修改失败");
            location.reload();
        }else{
            alert("修改成功");
            location.reload();
        }
    })
}