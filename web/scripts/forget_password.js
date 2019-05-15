"use script"

var submit_button = document.getElementById("button_regist");

submit_button.addEventListener("click", submitFunction, false);

function submitFunction(){
    let sid = document.getElementById("username").value;
    let newPass = document.getElementById("userpwd").value;

    if (sid === "" || newPass === ""){
        alert("请输入完整信息");
        location.reload();
    }

    let forget_uri = "http://localhost:80/api/forget_password?sid=" + sid + "&newPass=" + newPass;
    fetch(forget_uri).then(res => res.json()).then(function(result){
        if (result.code === "500"){
            alert("修改失败！用户不存在")
        }else{
            if (result.status === false){
                alert("修改失败！用户名必须为10位，密码必须为6-15位");
                location.reload();
            }else{
                alert("修改成功");
                location.href="E://学习/大二下/数据库/Project/web/login.html";
            }
        }
    })
}