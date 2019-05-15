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

//获取全部学生信息
var select_uri="http://localhost:80/api/get_all_student";

fetch(select_uri).then(res => res.json()).then(function(result){
    var select_table = document.getElementById("all_table");
    for (let i = 0 ; i < result.length ; i++){
        let gender = result[i].gender;
        if (gender === "man"){
            gender = "男";
        }else{
            gender = "女";
        }
        select_table.innerHTML += "<tr>"
                         + "<td class=\"cell\">" + result[i].sid + "</td>"
                         + "<td class=\"cell\">" + result[i].sname + "</td>"
                         + "<td class=\"cell\">" + gender + "</td>"
                         + "<td class=\"cell\">" + result[i].age + "</td>"
                         + "<td class=\"cell\">" + result[i].grade + "</td>"
                         + "<td class=\"cell\">" + result[i].class + "</td>"
                         + "</tr>";
    }
})


//查询单个学生
var basic_table = document.getElementById("basic_table");
var sid_cell = document.getElementById("sid_cell");
var sname_cell = document.getElementById("sname_cell");
var gender_cell = document.getElementById("gender_cell");
var age_cell = document.getElementById("age_cell");
var grade_cell = document.getElementById("grade_cell");
var class_cell = document.getElementById("class_cell");

var sid_query = document.getElementById("sid_query");
var sname_query = document.getElementById("sname_query");
sid_query.addEventListener("click", sidFunction, false);
sname_query.addEventListener("click", snameFunction1, false);

function sidFunction(){
    sid_result = document.getElementById("sid").value;
    let query_uri = "http://localhost:80/api/admin_get_student_basic?fieldName=sid&value=" + sid_result;
    fetch(query_uri).then(res => res.json()).then(function(result){
        if (result[0].code === "500"){
            alert("查询失败，用户不存在");
            location.reload();
        }else{
            sid_cell.innerHTML=result[0].sid;
            sname_cell.innerHTML=result[0].sname;
            if (result[0].gender === "man"){
                gender_cell.innerHTML = "男";
            }else{
                gender_cell.innerHTML = "女";
            }
            age_cell.innerHTML=result[0].age;
            grade_cell.innerHTML=result[0].grade;
            class_cell.innerHTML=result[0].class;
        }
    })
}

function snameFunction1(){
    var sname_result = document.getElementById("sname").value;
    let query_uri = "http://localhost:80/api/admin_get_student_basic?fieldName=sname&value=" + sname_result;
    fetch(query_uri).then(res => res.json()).then(function(result){
        if (result[0].code === "500"){
            alert("查询失败，用户不存在");
            location.reload();
        }else{
            sid_cell.innerHTML=result[0].sid;
            sname_cell.innerHTML=result[0].sname;
            if (result[0].gender === "man"){
                gender_cell.innerHTML = "男";
            }else{
                gender_cell.innerHTML = "女";
            }
            age_cell.innerHTML=result[0].age;
            grade_cell.innerHTML=result[0].grade;
            class_cell.innerHTML=result[0].class;
        }
    })
}

//修改学生属性
//更新按钮
var update_sname = document.getElementById("update_sname");
var update_gender = document.getElementById("update_gender");
var update_age = document.getElementById("update_age");
var update_grade = document.getElementById("update_grade");
var update_class = document.getElementById("update_class");

update_sname.addEventListener("click", snameFunction, false);
function snameFunction(){
    let newSname = document.getElementById("sname_input").value;
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid_cell.innerText + "&fieldName=sname&value=" + newSname;
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
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid_cell.innerText + "&fieldName=gender&value=" + newSname;
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
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid_cell.innerText + "&fieldName=age&value=" + newSname;
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
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid_cell.innerText + "&fieldName=grade&value=" + newSname;
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
    let submit_uri = "http://localhost:80/api/update_student?sid=" + sid_cell.innerText + "&fieldName=class&value=" + newSname;
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


//增加学生
var add_button = document.getElementById("submit");
add_button.addEventListener("click", addFunction, false);

function addFunction(){
    var sid = document.getElementById("add_sid").value;
    var sname = document.getElementById("add_sname").value;
    var gender = document.getElementById("add_gender").value;
    var age = document.getElementById("add_age").value;
    var grade = document.getElementById("add_grade").value;
    var className = document.getElementById("add_class").value;

    if (gender === "男"){
        gender = "man";
    }else{
        gender = "women";
    }
    var add_uri="http://localhost:80/api/add_student?sid=" + sid + "&sname=" + sname
                + "&gender=" + gender + "&age=" + age + "&grade=" + grade + "&class=" + className;
    fetch(add_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            alert("增加失败，学生已存在");
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
    var sid2 = document.getElementById("delete_sid").value;
    var delete_uri="http://localhost:80/api/delete_student?sid=" + sid2;
    fetch(delete_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            alert("学生不存在");
            location.reload();
        }else{
            alert("删除成功");
            location.reload();
        }
    })
}