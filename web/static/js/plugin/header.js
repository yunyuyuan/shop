let path = document.querySelector('meta[name="path"]').getAttribute('content');
let check_mail_s = '^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$';
let head_vue;
function para_to_map() {
    let map = {};
    let lis = location.search.match(/[?&](.+?)=(.*?)(?=&|$)/g);
    if (lis != null) {
        for (let m of lis) {
            map[m.replace(/[?&](.+?)=.*/, '$1')] = m.replace(/[?&].+?=(.*)/, '$1');
        }
    }
    return map;
}
// 获取url参数带默认值
function get_para_default(paras, s, default_) {
    return (typeof paras[s]!="undefined")?paras[s]:default_;
}
// 监听上传进度
function listen_upload_progress(callback) {
    let xhr = new XMLHttpRequest();
    xhr.upload.addEventListener('progress', callback);
    return xhr;
}
// blur
function win_blur(element, callback, close_btn) {
    function fire(e){
        let elem = e.target;
        while (elem) {
            if (elem === element) {
                return;
            }
            elem = elem.parentElement;
        }
        $(document).unbind('click', fire);
        callback(element);
    }
    $(document).bind('click', fire);
    if (close_btn !== undefined) {
        $(close_btn).click(function () {
            $(document).unbind('click', fire);
            callback(element);
        });
    }
}
// ajax函数
function ajax_(callback, url, data, option){
    let options = {
        success: function (r) {
            callback(r)
        },
        url: path+url, type: 'POST', dataType: 'json',
        data: data
    };
    if (typeof options!='undefined'){
        Object.assign(options, option);
    }
    $.ajax(options)
}