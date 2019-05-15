"use script"

var regist_button = document.getElementById("button_regist");

regist_button.addEventListener("click", regist_function, false);

function regist_function(){
    let sid = document.getElementById("username").value;
    let password = document.getElementById("userpwd").value;
    let repeatPass = document.getElementById("userpwd2").value;

    if (sid === "" || password === "" || repeatPass === ""){
        alert("请输入完整信息");
        location.reload();
    }

    if (password !== repeatPass){
        alert("两次输入的密码不匹配！");
        location.reload();
    }

    let regist_uri = "http://localhost:80/api/regist_student?sid=" + sid + "&password=" + password + "&repeatPass=" + repeatPass;
    fetch(regist_uri).then(res => res.json()).then(function(result){
        console.log(result.code);
        if (result.code === "500"){
            alert("注册失败，学号不存在或已被注册");
        }else{
            if (result.status === false){
                alert("注册失败，密码长度或用户名不符合规范，密码必须为6-12位");
                location.reload;
            }else{
                alert("注册成功");
                location.href="E://学习/大二下/数据库/Project/web/login.html";
            }
        }
    })
}
