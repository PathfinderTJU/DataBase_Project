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

//全部课程信息
var all_course_uri = "http://localhost:80/api/get_all_course";
fetch(all_course_uri).then(res => res.json()).then(function(result){
    var all_table = document.getElementById("all_table");
    for (let i = 0 ; i < result.length ; i++){
        let cancel_year = result[i].cancel_year;
        if (cancel_year === "-1"){
            cancel_year = "未取消";
        }
        all_table.innerHTML += "<tr>"
                         + "<td class=\"cell\">" + result[i].cid + "</td>"
                         + "<td class=\"cell\">" + result[i].cname + "</td>"
                         + "<td class=\"cell\">" + result[i].teacher + "</td>"
                         + "<td class=\"cell\">" + result[i].point + "</td>"
                         + "<td class=\"cell\">" + result[i].suit_year + "</td>"
                         + "<td class=\"cell\">" + cancel_year + "</td>"
                         + "</tr>";
    }
})

//查询单个学生
var basic_table = document.getElementById("basic_table");
var cid_cell = document.getElementById("cid_cell");
var cname_cell = document.getElementById("cname_cell");
var teacher_cell = document.getElementById("teacher_cell");
var point_cell = document.getElementById("point_cell");
var suit_year_cell = document.getElementById("suit_year_cell");
var cancel_year_cell = document.getElementById("cancel_year_cell");

var cid_query = document.getElementById("cid_query");
var cname_query = document.getElementById("cname_query");
cid_query.addEventListener("click", cidFunction, false);
cname_query.addEventListener("click", cnameFunction1, false);

function cidFunction(){
    cid_result = document.getElementById("cid").value;
    let query_uri = "http://localhost:80/api/get_course_basic?fieldName=cid&value=" + cid_result;
    fetch(query_uri).then(res => res.json()).then(function(result){
        if (result[0].code === "500"){
            alert("查询失败，课程不存在");
            location.reload();
        }else{
            let cancel_year = result[0].cancel_year;
            if (cancel_year === "-1"){
                cancel_year = "未取消";
            }

            cid_cell.innerHTML=result[0].cid;
            cname_cell.innerHTML=result[0].cname;
            teacher_cell.innerHTML=result[0].teacher;
            point_cell.innerHTML=result[0].point;
            suit_year_cell.innerHTML=result[0].suit_year;
            cancel_year_cell.innerHTML=cancel_year;
        }
    })
}

function cnameFunction1(){
    var cname_result = document.getElementById("cname").value;
    let query_uri = "http://localhost:80/api/get_course_basic?fieldName=cname&value=" + cname_result;
    fetch(query_uri).then(res => res.json()).then(function(result){
        if (result[0].code === "500"){
            alert("查询失败，课程不存在");
            location.reload();
        }else{
            let cancel_year = result[0].cancel_year;
            if (cancel_year === "-1"){
                cancel_year = "未取消";
            }

            cid_cell.innerHTML=result[0].cid;
            cname_cell.innerHTML=result[0].cname;
            teacher_cell.innerHTML=result[0].teacher;
            point_cell.innerHTML=result[0].point;
            suit_year_cell.innerHTML=result[0].suit_year;
            cancel_year_cell.innerHTML=cancel_year;
        }
    })
}

//修改学生属性
//更新按钮
var update_cname = document.getElementById("update_cname");
var update_teacher = document.getElementById("update_teacher");
var update_point = document.getElementById("update_point");
var update_suit_year = document.getElementById("update_suit_year");
var update_cancel_year = document.getElementById("cancel_button");

update_cname.addEventListener("click", cnameFunction, false);
function cnameFunction(){
    let newcname = document.getElementById("cname_input").value;
    let submit_uri = "http://localhost:80/api/update_course?cid=" + cid_cell.innerText + "&fieldName=cname&value=" + newcname;
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

update_teacher.addEventListener("click", teacherFunction, false);
function teacherFunction(){
    let newTeacher = document.getElementById("teacher_input").value;
    let submit_uri = "http://localhost:80/api/update_course?cid=" + cid_cell.innerText + "&fieldName=teacher&value=" + newTeacher;
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

update_point.addEventListener("click", pointFunction, false);
function pointFunction(){
    let newpoint = document.getElementById("point_input").value;
    let submit_uri = "http://localhost:80/api/update_course?cid=" + cid_cell.innerText + "&fieldName=point&value=" + newpoint;
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

update_suit_year.addEventListener("click", suitFunction, false);
function suitFunction(){
    let newsuit = document.getElementById("suit_year_input").value;
    let submit_uri = "http://localhost:80/api/update_course?cid=" + cid_cell.innerText + "&fieldName=suit_grade&value=" + newsuit;
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

cancel_button.addEventListener("click", cancelFunction, false);
function cancelFunction(){
    let newcancel = document.getElementById("cancel_year_input").value;
    let submit_uri = "http://localhost:80/api/update_course?cid=" + cid_cell.innerText + "&fieldName=cancel_year&value=" + newcancel;
    fetch(submit_uri).then(res => res.json()).then(function(result){
        if (result.status === false){
            alert("取消失败");
            location.reload();
        }else{
            alert("取消成功");
            location.reload();
        }
    })
}

var add_button = document.getElementById("add_button");
add_button.addEventListener("click", addFunction, false);

function addFunction(){
    console.log("a");
    var cid = document.getElementById("add_cid").value;
    var cname = document.getElementById("add_cname").value;
    var teacher = document.getElementById("add_teacher").value;
    var point = document.getElementById("add_point").value;
    var suit_year = document.getElementById("add_suit_year").value;
    var cancel_year = document.getElementById("add_cancel_year").value;
console.log(cancel_year);
    if(cancel_year === ""){
        cancel_year = "-1";
    }

    var add_uri="http://localhost:80/api/add_course?cid=" + cid + "&cname=" + cname
                + "&teacher=" + teacher + "&point=" + point + "&suit_year=" + suit_year 
                + "&cancel_year=" + cancel_year;
    console.log(add_uri);
    fetch(add_uri).then(res => res.json()).then(function(result){
        console.log(result);
        if (result.code === "500"){
            alert("增加失败，课程已存在");
            location.reload();
        }else{
            if(result.status === true){
                alert("增加成功");
                location.reload();
            }else{
                alert("增加失败，参数错误");
                location.reload();
            }
        }
    })
}

//删除学生
var delete_button = document.getElementById("delete_button");
delete_button.addEventListener("click", deleteFunction, false);

function deleteFunction(){
    var cid2 = document.getElementById("cid2").value;
    var delete_uri="http://localhost:80/api/delete_course?cid=" + cid2;
    fetch(delete_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            alert("课程不存在");
            location.reload();
        }else{
            alert("删除成功");
            location.reload();
        }
    })
}