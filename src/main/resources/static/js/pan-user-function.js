function editInfo(){
    layer.open({
        type: 2,
        content: '/user/editInfo',
        area: ['400px', '550px'],
        maxmin: true,
        title: '修改信息',
        end: function() {
            location.reload();
        }
    });
};

function changePwd(){
    layer.open({
        type: 2,
        content: '/user/changePwd',
        area: ['400px', '370px'],
        maxmin: true,
        title: '修改密码',
        end: function() {
            // layer.alert('修改成功',{title: '信息' },function(){
            //     location.reload();
            // });
        }
    });
};

function forgot(){
    layer.open({
        type: 2,
        content: '/user/forgot',
        area: ['400px', '370px'],
        maxmin: true,
        title: '忘记密码',
    });
};

// 新建文件夹按钮触发
function mkdir(parentId, userId) {
    layer.prompt({
        formType: 0,
        value: '',
        title: '请输入新建文件夹名称',
    }, function(newName, index) {
        $.ajax({
            url: "/makeDir",
            type: "POST",
            data: {
                dirName: newName,
                parentId: parentId,
                userId: userId
            },
            success: function(data) {
                if (data == "true") {
                    window.location.reload();
                    layer.msg(newName + '创建成功');
                } else
                    layer.msg('文件夹已存在');
            }
        });
    });
}

// 分享按钮触发
function choiceShare(dirId) {
    var dirIds = "";
    if (dirId == "select") {
        var namebox = $("input[name^='boxs']"); /*获取name值为boxs的所有input*/
        for (i = 0; i < namebox.length; i++) {
            if (namebox[i].checked == true) {
                console.log("dirId: " + namebox[i].id);
                dirIds += namebox[i].id + " ";
            }
        }
    } else
        dirIds = dirId;
    if (dirIds == "")
        layer.alert("请选择要分享的文件")
    else {
        layer.confirm('选择分享时效：', {
            btn: ['一天', '一周', '一月', '永久', '取消'] /*按钮*/ ,
            btn3: function(index, layero) {
                share(dirIds, "month")
            },
            btn4: function(index, layero) {
                share(dirIds, "forever")
            }
        }, function() {
            share(dirIds, "day")
        }, function() {
            share(dirIds, "week")
        });
    }
}
// 发送要分享的数据
function share(dirIds, choice) {
    $.ajax({
        url: "/shareDirs",
        type: "POST",
        data: {
            dirIds: dirIds,
            choice: choice
        },
        success: function(data) {
            console.log(data)
            var url = window.location.host + '/share/' + data.split(" ")[0];
            var key = data.split(" ")[1];
            var share_text = "链接地址:<br>" + url + "<br>口令（区分大小写）:" + key;
            console.log(share_text)
            layer.msg(share_text, {
                time: 60000,
                /*一分钟后自动关闭*/
                btn: ['确定', '取消']
            }, function () {
                document.getElementById("share").value = "链接地址:" + url + " 口令（区分大小写）:" + key;
                var message = document.getElementById("share");
                message.select();
                document.execCommand("copy");
                layer.msg("已复制到剪贴板");
            }, function () {
                document.getElementById("share").value = share_text;
                var message = document.getElementById("share");
                message.select();
                document.execCommand("copy");
                layer.msg("已复制到剪贴板");
            });
        }
    });
}
// 打开分享链接
function openShare(share_md5) {
    window.open("/share/" + share_md5);
}
// 复制接收到的分享链接信息到剪贴板
function copyShare(share_md5, key) {
    var share_text = '链接地址:' + location.host + '/share/' + share_md5 + '口令（区分大小写）:' + key;
    document.getElementById("share").value = share_text;
    var message = document.getElementById("share");
    message.select();
    document.execCommand("copy");
    layer.msg("已复制到剪贴板");
}

function cancelShare(Share_md5) {
    var type = 'share';
    var flag = '';
    $.ajax({
        url: "/ban",
        type: "POST",
        data: {
            type: type,
            flag: flag,
            id: Share_md5
        },
        success: function(data) {
            window.location.reload();
        }
    });

}


// 移动、复制、（分享链接内保存）按钮触发
function copyOrMoveOrSaveDirs(type, userId) {
    var dirIds = "";
    var namebox = $("input[name^='boxs']"); /*获取name值为boxs的所有input*/
    for (i = 0; i < namebox.length; i++) {
        if (namebox[i].checked == true) {
            dirIds += namebox[i].id + " ";
        }
    }
    if (dirIds == "")
        layer.alert("请选择文件！");
    else {
        var title = '';
        if (type == 'copy')
            title = "复制";
        else if (type == 'move')
            title = '移动'
        layer.open({
            type: 2,
            content: '/copyOrMoveOrSaveDirList?type=' + type + "&dirIds=" + dirIds + "&userId=" +
                userId + "&dirId=" + userId,
            area: ['600px', '500px'],
            maxmin: true,
            title: title,
            end: function() {
                window.location.reload();
            }
        });
    }
}

// 删除按钮触发
function deleteDirs(dirId, parentId, userId) {
    var dirIds = "";
    if (dirId == 'select') {
        var namebox = $("input[name^='boxs']"); /*获取name值为boxs的所有input*/
        for (i = 0; i < namebox.length; i++) {
            if (namebox[i].checked == true) {
                console.log("delete dirId: " + namebox[i].id);
                dirIds += namebox[i].id + " ";
            }
        }
    } else
        dirIds = dirId;
    console.log("dirIds: " + dirIds, "parentId: " + parentId, "userId: " + userId)
    $.ajax({
        url: "/deleteDirs",
        type: "POST",
        data: {
            dirIds: dirIds,
            userId: userId
        },
        success: function(data) {
            console.log("删除成功");
            window.location = "/dirList?dirId=" + parentId;
        }
    });
};

// 选择框
/* 是否点击全选*/
$('input[name="allboxs"]').click(function() {
    if ($(this).is(':checked')) {
        $('input[name="boxs"]').each(function() {
            /*此处如果用attr，会出现第三次失效的情况*/
            $(this).prop("checked", true);
        });
        var num = 0;
        $('.jr').each(function() {
            num += parseFloat($(this).text());
        });
        $('#J_Total').text(num);
    } else {
        $('input[name="boxs"]').each(function() {
            $(this).prop("checked", false);
        });
        $('#J_Total').text('0.00');
    }
});
/*判断当全选时 若有一个不选 全选按钮为不选状态*/
$('input[name="boxs"]').click(function() {
    var checkedLength = $("input[name='boxs']:checked").length;
    var checkLength = $("input[name='boxs']").length;
    var cont = 0;
    $("input[name='boxs']:checked").each(function() {
        cont += parseFloat($(this).parents('.td-chk').siblings('.td-sum').find('.jr').text());
    });
    $('#J_Total').text(cont);
    if (checkLength == checkedLength) {
        $("input[name='allboxs']").prop("checked", true);
        return true;
    } else {
        $("input[name='allboxs']").prop("checked", false);
        return true;
    }
});

// 重命名按钮触发
function rename(dirId, dirName) {
    layer.prompt({
        formType: 0,
        value: dirName,
        title: '请输入新文件夹名称',
    }, function(newName, index) {
        $.ajax({
            url: "/rename",
            type: "POST",
            data: {
                dirId: dirId,
                newName: newName
            },
            success: function(data) {
                if (data == "success") {
                    layer.msg(newName + '创建成功');
                    window.location.reload();
                } else
                    layer.msg('文件夹已存在');
            }
        });
    });
}

// 下载按钮触发
function downloadFile(dirId, dirName) {
    layer.prompt({
        formType: 0,
        value: dirName,
        title: '下载文件名称',
    }, function(newName, index) {
        $.ajax({
            url: "/downloadFile",
            type: "POST",
            data: {
                dirId: dirId
            },
            success: function(data) {
                var url = '/' + data;
                $("#download").attr("href", url);
                $("#download").attr("download", newName);
                $("#download")[0].click();
                window.location.reload();
            }
        });
    });
}

function openSave(type, dirId, userId) {
    var dirIds = "";
    if (type == "save") {
        var dirIds = "";
        var namebox = $("input[name^='boxs']"); //获取name值为boxs的所有input
        for (i = 0; i < namebox.length; i++) {
            if (namebox[i].checked == true) {
                dirIds += namebox[i].id + " ";
            }
        }
    } else
        dirIds = dirId
    if (dirIds == "")
        alert("请选择文件！")
    else {
        console.log("dirIds" + dirIds);
        var userId = userId;
        layer.open({
            type: 2,
            content: '/copyOrMoveOrSaveDirList?type=save&dirIds=' + dirIds + "&userId=" + userId +
                "&dirId=" + userId,
            area: ['600px', '500px'],
            maxmin: true,
            title: '保存',
            end: function() {
                window.location.reload();
            }
        });
    }
}

function save(dirIds, userId, parentId, type) {
    $.ajax({
        url: "/copyOrMoveOrSaveDirs",
        type: "POST",
        data: {
            dirIds: dirIds,
            userId: userId,
            parentId: parentId,
            type: type
        },
        success: function(data) {
            var index = parent.layer.getFrameIndex(window.name);
            if (data == 'moveSuccess') {
                parent.layer.close(index);
            } else if (data == 'moveRepeat')
                layer.msg("移动失败，文件已存在！");
            else if (data == 'moveError')
                layer.msg("文件夹不能移动到自己内部或其子文件夹内！！");
            else if (data == 'copySuccess') {
                parent.layer.close(index);
            } else if (data == 'copyError')
                layer.msg("文件夹不能保存到自己内部或其子文件夹内！！");
            else if (data == 'copyRepeat')
                layer.msg("复制失败，文件已存在！");
            else if (data == 'copySpace')
                layer.msg("复制失败，剩余空间不足！");
            else if (data == 'saveSuccess') {
                parent.layer.close(index);
            } else if (data == 'saveError')
                layer.msg("文件夹不能保存到自己内部或其子文件夹内！！");
            else if (data == 'saveRepeat')
                layer.msg("保存失败，文件已存在！");
            else if (data == 'saveSpace')
                layer.msg("保存失败，剩余空间不足！");
        }
    });
}

function cancle() {
    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
}