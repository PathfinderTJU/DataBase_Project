"use script"

var regist_button = document.getElementById("button_regist");
var login_button = document.getElementById("button");
var forget_button = document.getElementById("iforget");

forget_button.addEventListener("click", forgetFunction, false);
regist_button.addEventListener("click", registFunction, false);
login_button.addEventListener("click", loginFunction, false);

function forgetFunction(){
    location.href="E://学习/大二下/数据库/Project/web/forget_password.html";
}

function registFunction(){
    location.href="E://学习/大二下/数据库/Project/web/regist.html";
}

function loginFunction(){
    let sid = document.getElementById("username").value;
    let password = document.getElementById("userpwd").value;
    if (sid === ""){
        alert("请输入用户名");
        return;
    }
    if (password === ""){
        alert("请输入密码");
        return;
    }
    let login_uri = "";
    if (sid !== "admin"){
        login_uri = "http://localhost:80/api/login_student?sid=" + sid + "&password=" + password;
    }else{
        login_uri = "http://localhost:80/api/login_admin?adminID=admin&password=" + password;
    }
    fetch(login_uri).then(res => res.json()).then(function(result){
        if (result.status === false){
            alert("用户名或密码错误");
        }else{
            alert("登录成功");
            if (result.type === "student"){
                let uri = "E://学习/大二下/数据库/Project/web/student_main.html?sid=" + sid;
                location.href=uri;
            }else{
                let uri = "E://学习/大二下/数据库/Project/web/admin_main.html";
                location.href=uri;
            }
        }
    })
}