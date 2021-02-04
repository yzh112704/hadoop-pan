
// 向后端请求数据
function allListUsers(){
    $.ajax({
        url: "/allListUsers",
        type: "POST",
        success: function (datas) {
            getDatas(datas);
            initLayPage('users');
        },
    });
}

function allListFiles(){
    $.ajax({
        url: "/allListFiles",
        type: "POST",
        success: function (datas) {
            getDatas(datas);
            initLayPage('files');
        },
    });
}

function allListBanFiles(){
    $.ajax({
        url: "/allListBanFiles",
        type: "POST",
        success: function (datas) {
            getDatas(datas);
            initLayPage('banFiles');
        },
    });
}

function allListShares(){
    $.ajax({
        url: "/allListShares",
        type: "POST",
        success: function (Datas) {
            getDatas(Datas);
            initLayPage('shares');
        },
    });
}
var datas;
function getDatas(Datas){
    datas = Datas;
}

/**
 * 初始化layui分页
 */
function initLayPage(choice, pageConf) {
    if (!pageConf) {
        pageConf = {};
        pageConf.pageSize = 10;
        pageConf.currentPage = 1;
    }
    $("#search").on('input propertychange', function(){
        let keyWord = document.getElementById("search").value;
        let searchSelect = document.getElementById('searchSelect').value;
        let data = searchByReg(datas, keyWord, searchSelect);
        changeLayPage(choice, data, pageConf);
    });
    changeLayPage(choice, datas, pageConf);
}

function changeLayPage(choice, data, pageConf){
    layui.use(['laypage', 'layer', 'form'], function () {
        var page = layui.laypage;
        page.render({
            elem: 'layui',
            count: data.length,
            curr: pageConf.currentPage,
            limit: pageConf.pageSize,
            first: "首页",
            last: "尾页",
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function (obj, first) {
                if (!first) {
                    pageConf.currentPage = obj.curr;
                    pageConf.pageSize = obj.limit;
                    if(choice == 'users'){
                        fillUsersTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
                    }else if(choice == 'files'){
                        fillFilesTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
                    }else if(choice == 'banFiles'){
                        fillBanFilesTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
                    }else if(choice == 'shares'){
                        fillSharesTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
                    }
                }
            }
        });
        if(choice == 'users'){
            fillUsersTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
        }else if(choice == 'files'){
            fillFilesTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
        }else if(choice == 'banFiles'){
            fillBanFilesTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
        }else if(choice == 'shares'){
            fillSharesTable(data, (pageConf.currentPage - 1) * pageConf.pageSize, pageConf.pageSize); //页面填充
        }
    });
}


//填充用户表格数据
function fillUsersTable(data, start, end) {
    $("#tab_list").html('');
    end = (start + end)>=data.length?data.length:(start + end);
    for(;start<end; start++){
        // id 很多时候并不是连续的，如果为了显示比较连续的记录数，可以这样根据当前页和每页条数动态的计算记录序号
        var info = '';
        info += '<tr>';
        info += '<td><input onclick="clickBoxs()" type="checkbox" name="boxs" id="' + data[start]["id"] + '">&ensp;'
        info += '<a onclick="openInfo(\'user\', \'' + data[start]["id"] + '\')"> '+ data[start]["id"] + '</a>'
        info += '</td>'
        info += '<td><a onclick="openInfo(\'user\', \'' + data[start]["id"] + '\')">' + data[start]["name"] + '</a></td>'
        info += '<td>' + data[start]["institute"] + '</td>'
        info += '<td>' + data[start]["grade"] + '</td>'
        info += '<td>' + data[start]["major"] + '</td>'
        if(data[start]["status"] == 'true')
            info += '<td>已激活</td>'
        else
            info += '<td>未激活</td>'
        info += '<td>'
        if('[[${admin.level}]]' != 'common'){
            info += '<a onclick="openEdit(\'user\', \'' + data[start]["id"] + '\')">'
            info += '<button class="layui-btn demo">'
            info += '<i class="layui-icon">&#xe642;</i>'
            info += '</button> '
            info += '</a>'
            info += '<a href="/adminDel?type=user&id=' + data[start]["id"] + '">'
            info += '<button class="layui-btn demo">'
            info += '<i class="layui-icon">&#xe640;</i>'
            info += '</button>'
            info += '</a>'
        }
        info += '</td>'
        info += '</tr>';
        $("#tab_list").append(info);
    }
};
//填充文件表格数据
function fillFilesTable(data, start, end) {
    $("#tab_list").html('');
    end = (start + end)>=data.length?data.length:(start + end);
    for(;start<end; start++){
        var info = '';
        info += '<tr>';
        info += '<td><input type="checkbox" name="boxs" id="' + data[start]["file_id"] + '">&ensp;'
        info += '<a onclick="openInfo(\'file\', \'' + data[start]["md5"] + '\')"> '+ data[start]["file_id"] + '</a>'
        info += '</td>'
        info += '<td style="max-width:100px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
        info += '<img src="images/filetype/' + data[start]["file_type"] + '.ico" style="height: 30px;"></img>&ensp;'
        info += '<a onclick="openInfo(\'file\', \'' + data[start]["md5"] + '\')">' + data[start]["file_name"] + '</a>'
        info += '</td>';
        info += '<td>' + data[start]['currentChunk'] + '</td>';
        info += '<td>' + data[start]['totalChunks'] + '</td><td>';
        if('[[${admin.level}]]' != 'common' && data[start]['file_id'] != 'banned') {
            info += '<button class="layui-btn demo" onclick="ban(\'file\', \'true\', \'' + data[start]["md5"] + '\', \'' + data[start]["file_name"] + '\')">'
            info += '<i class="layui-icon">&#xe64f;</i>&ensp;禁用(备份)'
            info += '</button> '
            info += '<button class="layui-btn demo" onclick="ban(\'file\', \'false\', \'' + data[start]["md5"] + '\')">'
            info += '<i class="layui-icon">&#x1006;</i>&ensp;禁用(不备份)'
            info += '</button>'
            info += '<button class="layui-btn demo" onclick="downloadFile(\'' + data[start]["md5"] + '\')"><i class="layui-icon">&#xe601;</i>&ensp;下载</button></td>';
        }
        if('[[${admin.level}]]' != 'common' && data[start]['file_id'] == 'banned') {
            info += '已被禁用'
        }
        info += '</tr>';
        $("#tab_list").append(info);
    }
};
//填充被禁用文件表格数据
function fillBanFilesTable(data, start, end) {
    $("#tab_list").html('');
    end = (start + end)>=data.length?data.length:(start + end);
    for(;start<end; start++){
        // id 很多时候并不是连续的，如果为了显示比较连续的记录数，可以这样根据当前页和每页条数动态的计算记录序号
        var info = '';
        info += '<tr>';
        info += '<td style="max-width:100px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
        info += '<img src="images/filetype/' + data[start]["file_type"] + '.ico" style="height: 30px;"></img>&ensp;'
        info += '<a onclick="openInfo(\'file\', \'' + data[start]["md5"] + '\')">' + data[start]["file_name"] + '</a>'
        info += '</td>';
        info += '<td>' + data[start]['currentChunk'] + '</td>';
        info += '<td>' + data[start]['totalChunks'] + '</td>';
        info += '<td><button class="layui-btn demo" onclick="downloadFile(\'' + data[start]["md5"] + '\')"><i class="layui-icon">&#xe601;</i>&ensp;下载</button></td>';
        info += '</tr>';
        $("#tab_list").append(info);
    }
};
//填充分享表格数据
function fillSharesTable(data, start, end) {
    $("#tab_list").html('');
    end = (start + end)>=data.length?data.length:(start + end);
    for(;start<end; start++){
        var info = '';
        info += '<tr>';
        info += '<td style="max-width:20px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">';
        if(data[start]["expired"] == '已过期' || data[start]["expired"] == '已失效'){
            info += '<i class="layui-icon">&#xe64d;</i>&ensp;' + data[start]["share_md5"]
            info += '</td>';
            info += '<td><i class="layui-icon">&#xe683;</i>&ensp;' + data[start]["key"] + '</td>'
            info += '</td>';
        }
        else {
            info += '<i class="layui-icon">&#xe64c;</i>&ensp;'
            info += '<a onclick="openShare(\'' + data[start]["share_md5"] + '\')">' + data[start]["share_md5"] + '</a>'
            info += '</td>';
            info += '<td><i class="layui-icon">&#xe683;</i>&ensp;<a onclick="copyShare(\'' + data[start]["share_md5"] + '\', \'' + data[start]["key"] + '\')">' + data[start]["key"] + '</a></td>'
            info += '</td>';
        }
        info += '<td>'
        if(data[start]["expired"] == '永久'){
            info += '<a style="color: blue">'
        }else if(data[start]["expired"] == '已过期' || data[start]["expired"] == '已失效'){
            info += '<a style="color: gray">'
        }else{
            info += '<a>'
        }
        info += data[start]["expired"] + '</a>'
        info += '</td>';
        info += '<td>'
        if('[[${admin.level}]]' != 'common' && data[start]["expired"] != '已过期' && data[start]["expired"] != '已失效') {
            info += '<button class="layui-btn demo" onclick="ban(\'share\', \'' + data[start]["md5"] + '\')">'
            info += '<i class="layui-icon">&#xe64d;</i>&ensp;禁用链接'
            info += '</button>'
        }
        info += '</td>';
        info += '</tr>';
        $("#tab_list").append(info);
    }
};

// 正则搜索
function searchByReg(datas, keyWord, type){
    if(keyWord == "")
        return datas;
    var reg = new RegExp(keyWord);
    var arr = [];
    for(let i=0; i<datas.length; i++){
        if(reg.test(datas[i][type])){
            arr.push(datas[i]);
        }
    }
    return arr;
}

// 排序
function sortBy(choice, type, upOrDown, id){
    var bt = document.getElementById(id)
    // 列名
    let name = bt.innerText
    // 获取所有可排序列名
    let sorts = document.getElementsByName('sorts');
    for(let i=0; i<sorts.length; i++){
        // 不是需要排序的列，去除 ↑、↓ 符号
        if(sorts[i].id != id){
            let name = sorts[i].innerText
            name = name.replace('↑', '')
            name = name.replace('↓', '')
            sorts[i].innerText = name;
        }
    }
    // 当前列名以 ↓ 结尾，替换为↑；数据排序
    if(name.endsWith('↓')){
        bt.innerText = name.replace('↓', '↑')
        datas.sort(function(d1, d2){
            if(type != 'id' && type != 'file_id'){
                if(type != 'currentChunk'){
                    // 数据类型不为学号、文件ID、大小时，中文按照拼音、英文按照先后顺序排序
                    let name1 = d1[type];
                    let name2 = d2[type];
                    if(name1.charCodeAt(0) >= 19968 && name1.charCodeAt(0) <= 40869){
                        name1 = CC2PY(name1).toLowerCase();
                    }else{
                        name1 = name1.toUpperCase();
                    }
                    if(name2.charCodeAt(0) >= 19968 && name2.charCodeAt(0) <= 40869){
                        name2 = CC2PY(name2).toLowerCase();
                    }else{
                        name2 = name2.toUpperCase();
                    }
                    return (name1<name2)==true?-1:1;
                }
                else{
                    // size（B、KB、MB、GB）排序
                    return compareSize(d1[type], d2[type]);
                }
            }else{
                // 按照学号、文件ID排序
                return parseInt(d1[type]) - parseInt(d2[type]);
            }
        });
    }
    else if(name.endsWith('↑')){
        // 当前列名以 ↑ 结尾，替换为↓；数据排序
        bt.innerText = name.replace('↑', '↓')
        datas.sort(function(d1, d2){
            if(type != 'id' && type != 'file_id'){
                if(type != 'currentChunk'){
                    // 数据类型不为学号、文件ID、大小时，中文按照拼音、英文按照先后顺序排序
                    let name1 = d1[type];
                    let name2 = d2[type];
                    if(name1.charCodeAt(0) >= 19968 && name1.charCodeAt(0) <= 40869){
                        name1 = CC2PY(name1).toLowerCase();
                    }else{
                        name1 = name1.toUpperCase();
                    }
                    if(name2.charCodeAt(0) >= 19968 && name2.charCodeAt(0) <= 40869){
                        name2 = CC2PY(name2).toLowerCase();
                    }else{
                        name2 = name2.toUpperCase();
                    }
                    return (name1>name2)==true?-1:1;
                }
                else{
                    // size（B、KB、MB、GB）排序
                    return compareSize(d2[type], d1[type]);
                }
            }else{
                // 按照学号、文件ID排序
                return parseInt(d2[type]) - parseInt(d1[type]);
            }
        });
    }else{
        bt.innerText = name + ' ↓';
        datas.sort(function(d1, d2){
            if(type != 'id' && type != 'file_id'){
                if(type != 'currentChunk'){
                    // 数据类型不为学号、文件ID、大小时，中文按照拼音、英文按照先后顺序排序
                    let name1 = d1[type];
                    let name2 = d2[type];
                    if(name1.charCodeAt(0) >= 19968 && name1.charCodeAt(0) <= 40869){
                        name1 = CC2PY(name1).toLowerCase();
                    }else{
                        name1 = name1.toUpperCase();
                    }
                    if(name2.charCodeAt(0) >= 19968 && name2.charCodeAt(0) <= 40869){
                        name2 = CC2PY(name2).toLowerCase();
                    }else{
                        name2 = name2.toUpperCase();
                    }
                    return (name1>name2)==true?-1:1;
                }
                else{
                    // size（B、KB、MB、GB）排序
                    return compareSize(d2[type], d1[type]);
                }
            }else{
                // 按照学号、文件ID排序
                return parseInt(d2[type]) - parseInt(d1[type]);
            }
        });
    }
    // 更改按钮内容的升序、降序为相对的
    if(upOrDown == 'up'){
        bt.removeAttribute('onclick')
        bt.setAttribute('onclick', 'sortBy(\'' + choice + '\', \'' + type + '\', \'down\', \'' + id + '\')');
    }else{
        bt.removeAttribute('onclick')
        bt.setAttribute('onclick', 'sortBy(\'' + choice + '\', \'' + type + '\', \'up\', \'' + id + '\')');
    }
    // 重新初始化页面内容
    pageConf = {};
    pageConf.pageSize = 10;
    pageConf.currentPage = 1;
    // 判断是否已经输入过内容
    let keyWord = document.getElementById("search").value;
    if(keyWord != ''){
        // 根据输入的内容进行筛选
        let searchSelect = document.getElementById('searchSelect').value;
        let data = searchByReg(datas, keyWord, searchSelect);
        changeLayPage(choice, data, pageConf);
    }
    else{
        changeLayPage(choice, datas, pageConf);
    }
}
// 汉字转拼音
function CC2PY(l1) {
    var l2 = l1.length;
    var I1 = "";
    var reg = new RegExp('[a-zA-Z0-9\- ]');
    for (var i=0;i<l2;i++) {
        var val = l1.substr(i,1);
        var name = arraySearch(val,PinYin);
        if(reg.test(val)) {
            I1 += val;
        } else if (name!==false) {
            I1 += name;
        }
    }
    I1 = I1.replace(/ /g,'-');
    while (I1.indexOf('--')>0) {
        I1 = I1.replace('--','-');
    }
    return I1;
}
// 在对象中搜索
function arraySearch(l1,l2){
    for (var name in PinYin){
        if (PinYin[name].indexOf(l1)!=-1) {
            return name;
            break;
        }
    }
    return false;
}
// size（B、KB、MB、GB）比较大小
function compareSize(size1, size2){
    // 获取size的级别
    var unit1 = getSizeUnit(size1);
    var unit2 = getSizeUnit(size2);
    // 级别不相等，做差
    if(unit1 != unit2){
        return unit1 - unit2;
    }else{
        // 级别相等
        if(unit1 == 0){
            // 单位为B，替换掉最后一位 B
            let num1 = parseFloat(size1.replace('B', ''));
            let num2 = parseFloat(size2.replace('B', ''));
            // 前面数字做差
            return num1 - num2;
        }else{
            // 单位不为B，不取最后两位单位（KB、MB、GB）
            let num1 = parseFloat(size1.substring(0, size1.length));
            let num2 = parseFloat(size2.substring(0, size2.length));
            // 前面数字做差
            return num1 - num2;
        }
    }
}
// 获取size单位级别
function getSizeUnit(size){
    if(size.endsWith("KB")){
        return 1;
    }else if(size.endsWith("MB")){
        return 2;
    }else if(size.endsWith("GB")){
        return 3;
    }if(size.endsWith("B")){
        return 0;
    }
}