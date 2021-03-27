$(function() {

  var id = $('#id').length > 0 ? $('#id').val() : '';
  var path = $('#path').length > 0 ? $('#path').val() : '';
  var tablename = $('#tablename').length > 0 ? $('#tablename').val() : '';
  var idname = $('#idname').length > 0 ? $('#idname').val() : '';
  if($('#btn_upload').length>0){
      $('#btn_upload').uploadify({
          'swf': '../../Scripts/jquery.uploadify-v3/uploadify.swf',
          'uploader': getProjectPath() + '/FileUpload?path=' + encodeURI(path),
          'multi': true, // 是否允许多选
          'auto': true, // 是否允许自动上传
          'buttonText': '上传',
          'width': 50,
          'fileTypeExts': '*.*', // 指定上传文件类型
          'fileObjName': 'filedata',
          'queueID': 'fileQueue',
          'queueSizeLimit': 100, // 允许同时上传文件数量
          'uploadLimit': 1000, // 允许上传文件总数，指打开一次浏览器
          'fileSizeLimit': '2048MB', // 限制单个文件大小，限制IIS大小请到Web.Config修改
          'removeCompleted': false, // 上传完成后是否自动消失
          'onUploadSuccess': function(file, data, response) {// 每个文件上传完毕
              $('.uploadify-queue-item').each(function() {
                  if ($(this).attr('id') == file.id) {
                      $(this).attr('id', data);
                      return false;
                  }
              });

          },

      });
  }


  $("body").on("click", ".cancel a", function() {
    if (confirm("您确定要删除吗?")) {
      var _this = $(this);
      var realname = _this.parents('.uploadify-queue-item').attr('id');
      var allfilename = getAllFileName(realname);
      // alert(getUrlVars()["path"]);
        //       // return;
      $.post(getProjectPath() + '/FileDelete', {
        allfilename: encodeURI(allfilename),
        realname: encodeURI(realname),
        id: id,
        path: path,
        tablename: tablename,
        idname: idname,
      }, function(data) {
        switch (data) {
        case '0':
          alert('删除成功！');
          _this.parents(".uploadify-queue-item").remove();
          break;
        case '1':
          alert("删除过程中出错！");
          break;
        case '2':
          alert('目标文件不存在或，已删除此条文件记录。');
          _this.parents(".uploadify-queue-item").remove();
          break;
        case '3':
          alert('更新数据库出错！');
          break;
        }
      });
    }
  });

  $('#btn_view').click(
          function() {
            var tableid = $('input.radio:checked').val();
            if (tableid != undefined) {
              $.ajax({
                url: getProjectPath() + '/CheckSingleFile',
                type: "post",
                data: {
                  path: encodeURI(path),
                  tablename: tablename,
                  idname: idname,
                  id: tableid,
                },
                success: function(data) {
                  //alert(path);
                  if (data != "") {
                    if(checkExtSupprt(data)){
                      window.open(getProjectPath() + '/FileSubmitView?path='
                              + encodeURI(path) + '&realname=' + encodeURI(data));
                    }else{
                      qBox.iFLoad({
                        title: '文件浏览',
                        src: getProjectPath() + '/FileView?path='
                                + encodeURI(path) + '&tablename=' + tablename
                                + '&idname=' + idname + '&id=' + tableid,
                        w: 400,
                        h: 270
                      });
                    }
                    
                    
                  } else {
                    qBox.iFLoad({
                      title: '文件浏览',
                      src: getProjectPath() + '/FileView?path='
                              + encodeURI(path) + '&tablename=' + tablename
                              + '&idname=' + idname + '&id=' + tableid,
                      w: 400,
                      h: 270
                    });
                  }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                  console.log(XMLHttpRequest.status);
                  console.log(XMLHttpRequest.readyState);
                  console.log(textStatus);
                },

              });

            } else {
              alert("请选择您要浏览的记录，点击单选按钮选中！");
            }
          });

  $("#btn_download").click(
          function() {
            var tableid = $('input.radio:checked').val();
            if (tableid != undefined) {
              //alert(encodeURI(path));
              $.ajax({
                url: getProjectPath() + '/CheckSingleFile',
                type: "post",
                data: {
                  path: encodeURI(path),
                  tablename: tablename,
                  idname: idname,
                  id: tableid,
                },
                /* dataType: 'json', */
                success: function(data) {
                  //alert(encodeURI(path));
                  if (data != "") {
                    httpPost(getProjectPath() + '/FileSubmitDownload', {
                      path: encodeURI(path),
                      realname: data
                    });
                  } else {
                    qBox.iFLoad({
                      title: '文件下载',
                      src: getProjectPath() + '/FileDownload?path='
                              + encodeURI(path) + '&tablename=' + tablename
                              + '&idname=' + idname + '&id=' + tableid,
                      w: 400,
                      h: 270
                    });
                  }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                  console.log(XMLHttpRequest.status);
                  console.log(XMLHttpRequest.readyState);
                  console.log(textStatus);
                },

              });

            } else {
              alert("请选择您要下载的记录，点击单选按钮选中！");
            }
          });
  $("#btn_submit_download").click(function() {
    var realname = $('#fileList').val();
    if (realname != undefined) {
      //alert(path);
      httpPost(getProjectPath() + '/FileSubmitDownload', {
        path: encodeURI(path),
        realname: realname
      });
    } else {
      alert("请选择您要下载的记录，点击该行选中！");
    }
  });
  $("#btn_submit_view").click(
          function() {
            var realname = $('#fileList').val();
            //alert(realname);
            if (realname != undefined) {
              //alert(path);
              if(checkExtSupprt(realname)){
                window.open(getProjectPath() + '/FileSubmitView?path='
                        + encodeURI(path) + '&realname=' + encodeURI(realname));
              }
              
            } else {
              alert("请选择您要浏览的记录，点击该行选中！");
            }
          });

});

function httpPost(URL, PARAMS) {
  var temp = document.createElement("form");
  temp.action = URL;
  temp.method = "post";
  temp.style.display = "none";

  for ( var x in PARAMS) {
    var opt = document.createElement("textarea");
    opt.name = x;
    opt.value = PARAMS[x];
    temp.appendChild(opt);
  }

  document.body.appendChild(temp);
  temp.submit();

  return temp;
}
function getAllFileName() {
  var allfilename = '';
  $('.uploadify-queue-item').each(function() {
    allfilename += $(this).attr('id') + '?';
  });
  allfilename = allfilename.replace(/(^\?)|(\?$)/g, '');
  return allfilename;
}
function getAllFileName(exclude_realname) {
  var allfilename = '';
  $('.uploadify-queue-item').each(function() {
    if ($(this).attr('id') != exclude_realname) {
      allfilename += $(this).attr('id') + '?';
    }
  });
  allfilename = allfilename.replace(/(^\?)|(\?$)/g, '');
  return allfilename;
}
function getProjectPath() {
  // 获取当前网址，如： http://localhost:8080/ems/Pages/Basic/Person.jsp
  var curWwwPath = window.document.location.href;
  // 获取主机地址之后的目录，如： /ems/Pages/Basic/Person.jsp
  var pathName = window.document.location.pathname;
  var pos = curWwwPath.indexOf(pathName);
  // 获取主机地址，如： http://localhost:8080
  var localhostPath = curWwwPath.substring(0, pos);
  // 获取带"/"的项目名，如：/ems
  var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
  return (projectName);
}
function checkExtSupprt(filename){
  if(filename.indexOf('.')<0){
    return false;
  }else{
    var ext=filename.substr(filename.lastIndexOf('.')+1).toLowerCase();
    var ext_list=['pdf','ppt','pptx','txt','doc','docx','xls','xlsx','gif','png','jpg','jpeg','mp4'];
    if(ext_list.indexOf(ext)<0){
        alert('不支持在线浏览此类型文件，请点击下载查看。目前支持的格式：'+ext_list);
        return false;
    }else{
      return true;
    }
  }
}
