const PROJECT_PATH='/kuangshanJava'
function fail(XHR) {
    if(XHR.status===450){
        window.location.href=PROJECT_PATH+'/access_rejected.jsp'
    }
}

function ajaxPost(url,data,success) {
    $.ajax({
        type: 'post',
        url,
        data,
        dataType: 'json',
    }).done(success).fail(fail)
}
function ajaxPut(url,data,success) {
    $.ajax({
        type: 'put',
        url,
        data,
        dataType: 'json',
    }).done(success).fail(fail)
}
function ajaxDelete(url,data,success) {
    $.ajax({
        type: 'delete',
        url,
        data,
        dataType: 'json',
    }).done(success).fail(fail)
}
function ajaxGet(url,data,success) {
    $.ajax({
        type: 'get',
        url,
        data,
        dataType: 'json',
    }).done(success).fail(fail)
}
function ajaxSubmit(url,data,success) {
    $.ajax({
        type: 'post',
        url,
        data:JSON.stringify(data),
        contentType: 'text/javascript;charset=utf-8',
        dataType: 'json',
    }).done(success).fail(fail)
}
function getConditions(_params) {
    let params = [];
    for (col of _params) {
        switch (col.type) {
            case 'text':
                if (col.value && col.value.replace(/ /g, '')) {
                    col.value = col.value.replace(/ /g, '');
                    params.push(` ${col.model} like '%${col.value}%' `);
                }
                break
            case 'time':
                if (col.value && col.value[0] && col.value[1]) {
                    params.push(` ${col.model} between '${moment(col.value[0]).format('YYYY-MM-DD')}' and '${moment(col.value[1]).format('YYYY-MM-DD')}'`);
                }
                break
            case 'select':
                if (col.value) {
                    params.push(` ${col.model} = '${col.value}' `);
                }
                break
            case 'number':
                if (col.value && Number(col.value) && col.operator) {
                    params.push(` ${col.model} ${col.operator} ${Number(col.value)} `);
                }
                break
        }
    }
    return params.join('and')
}