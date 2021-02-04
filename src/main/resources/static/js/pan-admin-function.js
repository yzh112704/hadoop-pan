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
// 按钮点击
function clickBoxs(){
    console.log("clickBoxs")
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
}

// 添加用户或管理员
function addUserOrAdmin(type) {
    if(type == 'addAdmin'){
        layer.open({
            type: 2,
            content: '/adminChange?type=' + type,
            area: ['400px', '350px'],
            maxmin: true,
            title: '添加管理员',
            end: function() {
                location.reload();
            }
        });
    }
    else{
        layer.open({
            type: 2,
            content: '/adminChange?type=' + type,
            area: ['400px', '550px'],
            maxmin: true,
            title: '添加用户',
            end: function() {
                location.reload();
            }
        });
    }
}
// 下载批量添加用户Excel模板
function downloadTemplateExcel() {
    var url = '/TemplateUsersExcel.xlsx';
	var fileName = '批量添加用户表.xlsx';
	var flag = prompt("下载文件名称", fileName)
	if (flag) {
		$("#download").attr("href", url);
		$("#download").attr("download", flag);
		$("#download")[0].click();
	}
}
// 批量添加用户
function addUsers(users) {
    $.ajax({
            url: "/addUsers",
            type: "POST",
            data: {
                users: users,
            },
            success: function(output) {
                if(output == '<br>'){
                    location.reload();
                }else{
                    layer.alert(output,{title: '添加结果' },function(){
                        location.reload();
                    });
                }
            }
        });
}
//excel日期格式转换 numb为数字，format为拼接符“-”
function formatDate(numb, format) {
	if (numb != undefined) {
		let time = new Date((numb - 1) * 24 * 3600000 + 1)
		time.setYear(time.getFullYear() - 70)
		let year = time.getFullYear() + ''
		let month = time.getMonth() + 1 + ''
		let date = time.getDate() + ''
		if (format && format.length === 1) {
			return year + format + month + format + date
		}
		return year + (month < 10 ? '0' + month : month) + (date < 10 ? '0' + date : date)
	} else {
		return undefined;
	}
}

// 用户或文件详细信息
function openInfo(type, id) {
    if(type == 'file'){
        layer.open({
            type: 2,
            content: '/showInfo?type=' + type + '&id=' + id,
            area: ['400px', '330px'],
            maxmin: true,
            title: '文件详细信息',
        });
    }
    else{
        layer.open({
            type: 2,
            content: '/showInfo?type=' + type + '&id=' + id,
            area: ['400px', '550px'],
            maxmin: true,
            title: '用户详细信息',
        });
    }
}

// 管理员编辑（用户或管理员）弹窗
function openEdit(type, id){
    if(type == 'admin'){
        if(id == 'administrator')
            id = '';
        layer.open({
            type: 2,
            content: '/adminChange?type=' + type + '&id=' + id,
            area: ['400px', '330px'],
            maxmin: true,
            title: '修改管理员信息',
            end: function() {
                location.reload();
            }
        });
    }
    else {
        layer.open({
            type: 2,
            content: '/adminChange?type=' + type + '&id=' + id,
            area: ['400px', '550px'],
            maxmin: true,
            title: '修改用户信息',
            end: function() {
                location.reload();
            }
        });
    }
};

// 管理员禁用文件或分享链接
function ban(type, flag, id, name) {
    if (type == 'file' && flag == 'true') {
        var name = prompt('请输入被禁用后的名称', name);
        if (name != null) {
            $.ajax({
                url: "/ban",
                type: "POST",
                data: {
                    type: type,
                    flag: flag,
                    name: name,
                    id: id
                },
                success: function(data) {
                    window.location.reload();
                }
            });
        }
    } else {
        $.ajax({
            url: "/ban",
            type: "POST",
            data: {
                type: type,
                flag: flag,
                id: id
            },
            success: function(data) {
                window.location.reload();
            }
        });
    }
}

// 管理员下载文件
function downloadFile(md5) {
    $.ajax({
        url: "/AdminDownloadFile",
        type: "POST",
        data: {
            md5: md5
        },
        success: function(data) {
            console.log("success!");
            console.log("href: " + window.location.host + '/' + data);
            var url = '/' + data;
            var fileName = url.split("/")[url.split("/").length - 1];
            var flag = prompt("下载文件名称", fileName)
            if (flag) {
                $("#download").attr("href", url);
                $("#download").attr("download", flag);
                $("#download")[0].click();
            }
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