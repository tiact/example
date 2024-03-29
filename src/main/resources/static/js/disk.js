let initDir = "/";
let preDir = initDir;
let currDir = "";
let currDirArr = []

let fileArr;
let toPath = ''
let searchName = ''

let initData = () => {
    preDir = ""
    currDir = ""
}

let offDown = () => {
    console.log($('.down-content'))
    $('.down-content').toggleClass("toggle-hide")
    $('.down-container').toggleClass("down-show")
}

let showAdd = () => {
    $("#show").toggleClass("toggle-hide")
    $("#show").find('.edit-bar')[0].style.display = 'flex'
}

let showEdit = (item) => {
    console.log(item)
    console.log($(item).parent().prev()[0])
    $(item).parent().next()[0].style.display = 'flex'
    $(item).parent().prev()[0].style.display = 'none'
}

let offEdit = (idx) => {
    $('#newName-' + idx).parent()[0].style.display = 'none'
    $('#orgName-' + idx)[0].style.display = 'flex'
}

let optArr = ['del', 'edit', 'mkdir', 'rename','download','batch']


let openOpt = (item) => {
    // let name = $(item).find('a').text()
    $(item).find('span').removeClass("toggle-hide")
    item.style.background = '#b5d2e8'
}

let offOpt = (item) => {
    $(item).find('span').addClass("toggle-hide")
    item.style.background = 'white'
}


let fileOpt = (opt = 'mkdir', name, newName) => {
    let data = {
        route: currDir,
        name,
        opt,
        newName
    }
    console.log(JSON.stringify(data))
    switch (opt) {
        case 'del':
            layer.confirm('确认操作？', function (index) {
                optReq(data);
                layer.close(index);
            });
            return;
        case 'edit':
            if (data.newName !== null || data.newName !== '') {
                data.newName = $('#newName-' + data.newName).val()
                break;
            } else {
                layer.msg('Error');
            }
            return;
        case 'mkdir':
            data.name = $('#fileName').val()
            if (data.name !== null && data.name !== '') {
                break
            }
            return;
        case 'download':
            location.href = `/weChat${platform}download?route=`+data.route+'&name='+data.name
            // fileDown(data)
            return;
        case 'batch':
            // data['mode'] = 'batchZip'
            // fileDown(data)
            if(platform!='/'){
                layer.msg("建设中...")
                return;
            }
            location.href = `/weChat${platform}download?route=`+data.route+'&name='+data.name+'&mode=batchZip'
            return;
    }
    optReq(data);
}

let optReq = (data) => {
    if (data.name !== null && data.name !== '') {
        $.ajax({
            url: `/weChat${platform}opt`, data
        }).then((res) => {
            layer.msg(res.msg);
            if (data.opt === 'mkdir') {
                showAdd()
            }
            toRoute(currDir, 3)
        })
    } else {
        layer.msg('文件不存在')
    }
}

let fileDown = (data) => {
    $.ajax({
        url: `/weChat${platform}download`, data
    }).then((res) => {
        console.log(res)
    })
}

let tranSize = (fileSize) => {
    if (fileSize !== null && fileSize !== '') {
        fileSize = (fileSize / 1024).toFixed(0)
        if (fileSize > 1024 * 1024) {
            return (fileSize / (1024 * 1024)).toFixed(1) + 'G'
        }
        if (fileSize > 1024) {
            return (fileSize / 1024).toFixed(1) + 'M'
        }
        return fileSize + 'kb'
    }
    return '-'
}

let iptBarRender = (id, okPress = "fileOpt()", noPress = "showAdd()", val = '', ico = 'ico/dir.svg') => {
    return '<div class="edit-bar">'
        + '<input class="layui-input" id="' + id + '" value="' + val + '">'
        + '<button type="button" class="layui-btn layui-btn-primary layui-border-blue layui-btn-xs" onclick="' + okPress + '"><i class="layui-icon layui-icon-ok"></i></button>'
        + '<button type="button" class="layui-btn layui-btn-primary layui-border-blue layui-btn-xs" onclick="' + noPress + '"><i class="layui-icon layui-icon-close"></i></button>'
        + '</div>'
}

let regImage = (fileName) => {
    let reg = /\.(png|jpg|gif|icon|jpeg|svg)$/
    if (fileName.match(reg)) {
        return true
    }
    return false
}

$('#searchText').bind('keypress',function(event){
    if(event.keyCode === 13) {
        toSearch()
    }
});

let toSearch = () =>{
    searchName = $("#searchText").val()
    if(searchName===''){
        toRoute('',2)
    }else{
        toRoute('',4)
    }
}

let openUrl = (item) => {
    window.open($(item).find('a').attr('href'))
}

let toRoute = (path, opt) => {
    var index = layer.load(1);
    console.log("--- path ---" + path)
    let mode = 'list'
    switch (opt) {
        case 0: //下级
            path = currDir + initDir + path
            preDir = currDir
            currDir = path
            break;
        case 1://返回上一级
            let pathBack = initDir + path
            path = preDir
            currDir = preDir
            preDir = preDir.substring(0, preDir.indexOf(pathBack))
            break;
        case 2: //主页
            path = ""
            initData()
            break;
        case 3: //刷新
            currDir = path
            preDir = currDir.substring(0, currDir.indexOf(currDir))
            break;
        case 4: //搜索
            mode = 'search'
            break;
        case 5:
            path = ""
            initData()
            break;
    }
    console.log("当前路径：" + currDir)
    console.log("上级路径：" + preDir)
    fileArr = {}
    $.get({
        url: `/weChat${platform}route`,
        data: {
            route: path,
            mode,
            name:searchName
        }
    }).then((res) => {
        let fileList = '';
        let dirList = '';
        $("#pathList").empty();
        if (res.code === 200) {
            fileArr = res.data.file;
                for (let m in fileArr) {
                    let preTr = '<tr onmouseover="openOpt(this)" onmouseout="offOpt(this)">'

                    let removeBtn = '<img src="ico/del.svg" class="opt-ico" onclick="fileOpt(optArr[0],fileArr[' + m + '].name)">'

                    let downFileBtn = '<img src="ico/down.svg" class="opt-ico" onclick="fileOpt(optArr[4],fileArr[' + m + '].name)">'

                    let batchDirBtn = '<img src="ico/down.svg" class="opt-ico" onclick="fileOpt(optArr[5],fileArr[' + m + '].name)">'

                    let renameBtn = '<img src="ico/edit.svg" class="opt-ico" onclick="showEdit(this)">'
                    let renameBar = iptBarRender('newName-' + m, "fileOpt('edit','" + fileArr[m].name + "'," + m + ")", "offEdit(" + m + ")", fileArr[m].name)

                    let downBtn = downFileBtn
                    if(fileArr[m].type === 0){
                        downBtn = batchDirBtn
                    }
                    let optBar = '<span class="first-item-ico toggle-hide">'
                        + renameBtn
                        + removeBtn
                        + downBtn
                        + '</span>'

                    let sufTr = optBar
                        + renameBar
                        + '</td>'
                        + '<td>' + tranSize(fileArr[m]['size']) + '</td>'
                        + '<td>' + moment(fileArr[m]["editTime"]).format('YYYY-MM-DD HH:mm:ss') + '</td>'
                        + '</tr>';

                    if (fileArr[m].type === 0) {
                        let tr = preTr
                            + '<td class="first-item" style="text-align: left" ondblclick="toRoute(fileArr[' + m + '].name,0)">'
                            + '<img src="ico/dir.svg" class="file-ico">'
                            + '<a onclick="toRoute(fileArr[' + m + '].name,0)" id="orgName-'+m+'">' + fileArr[m]["name"] + '</a>'
                            + sufTr
                        dirList += tr
                    } else {
                        let fileUrl = (platform==='/'?"/weChat/s":prefix )+ currDir + initDir + fileArr[m]["name"]+"?t="+new Date().getMilliseconds()
                        let imgUrl = 'ico/file.svg'
                        if (regImage(fileArr[m]["name"])) {
                            imgUrl = fileUrl
                        }
                        let tr = preTr
                            + '<td class="first-item" style="text-align: left" ondblclick="openUrl(this)">'
                            + '<img src="' + imgUrl + '" class="file-ico"><a href="'+fileUrl+'" target="_blank" id="orgName-'+m+'">'
                            + fileArr[m]["name"] + '</a>'
                            + sufTr
                        fileList += tr
                    }
                }


                let addBar = ''
                if(mode==='list'){
                    let mkdirBar = iptBarRender("fileName")
                    addBar = '<tr id="show" class="toggle-hide"><td class="first-item" style="text-align: left">'
                        + '<img src="ico/dir.svg" class="file-ico">'
                        + mkdirBar
                        + '</td>'
                        + '<td>-</td>'
                        + '<td>-</td>'
                        + '</tr>';
                }

                $("#pathList").append(addBar + dirList + fileList)

                $("#preOpt").empty()

                if (currDir !== '' && currDir !== initDir && mode==='list') {
                    let rightIco = '<span class="router-color"><i class="layui-icon layui-icon-right" style="font-size: 10px;"></i></span>'
                    currDirArr = currDir.split("/")
                    let sufUrl = '<a onclick="toRoute(initDir,2)" class="router-color" >全部文件</a>'
                    if (currDirArr.length > 2) {
                        for (let i = 1; i < currDirArr.length - 1; i++) {
                            console.log("--- 子路径 ---" + currDir.substring(0, currDir.indexOf(currDirArr[i + 1]) - 1))
                            toPath = currDir.substring(0, currDir.indexOf(currDirArr[i + 1]) - 1)
                            let preUrl = '<a onclick="toRoute(toPath ,3)" class="router-color">' + currDirArr[i] + '</a>'
                            sufUrl += rightIco + preUrl
                        }
                    }
                    sufUrl += rightIco + currDirArr[currDirArr.length - 1]
                    // sufUrl += currDir.replace(/\//g,rightIco)
                    $("#preOpt").append('<a onclick="toRoute(preDir,1)" class="router-color">返回上一级</a><span class="router-color" style="margin: 0px 5px">|</span>' + sufUrl)
                } else {
                    $("#preOpt").append('<a onclick="toRoute(initDir,2)" >全部文件</a>')
                }
        }else{
            layer.msg(res.msg)
        }
        layer.close(index)
    })
}

toRoute('')

var uploadInst;
layui.use(['layer', 'upload', 'element'], function () {
    var $ = layui.jquery,
        upload = layui.upload,
        element = layui.element;

    uploadInst = upload.render({
        elem: '#testList'
        , elemList: $('#demoList') //列表元素对象
        , url: `/weChat${platform}upload`
        , accept: 'file'
        , multiple: true
        ,
        data: {
            over: () => {
                return 0;
            },
            root: () => {
                return currDir;
            }
        }
        , before: function (obj) {
            var that = this;
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function (index, file, result) {
                console.log("--- fileContainer ---", currDir)
                var tr = $(['<tr id="upload-' + index + '">'
                    , '<td  class="first-item" style="text-align: left">' + file.name + '</td>'
                    , '<td>' + tranSize(file.size) + '</td>'
                    , '<td><div class="layui-progress" lay-filter="progress-demo-' + index + '" lay-showPercent="true"><div class="layui-progress-bar layui-bg-blue" lay-percent=""></div></div></td>'
                    // , '<td><a onclick="toRoute(currDir ,3)" class="router-color">' + currDir + '</a></td>'
                    // , '<td>'
                    // , '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                    // , '</td>'
                    , '</tr>'].join(''));
                that.elemList.append(tr);
                element.render('progress');
            });
        }
        , done: function (res, index, upload) {
            if (res.code === 200) {
                delete this.files[index]; //删除文件队列已经上传成功的文件
                layer.msg(res.msg)
                toRoute(currDir, 3)
                return;
            }
            this.error(index, upload);
        }
        , allDone: function (obj) { //多文件上传完毕后的状态回调
            console.log(obj)
        }
        , error: function (index, upload) { //错误回调
            var that = this;
            var tr = that.elemList.find('tr#upload-' + index)
                , tds = tr.children();
            tds.eq(3).find('.demo-reload').removeClass('layui-hide');
        }
        , progress: function (n, elem, e, index) {
            element.progress('progress-demo-' + index, n + '%');
        }
    });
});