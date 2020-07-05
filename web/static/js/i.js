$(document).ready(function () {
    let bounce_timer = true;
    let i_menu = $('#i-menu');
    let root = $(document.documentElement);
    // 调整菜单
    function handle_menu(){
        if (root.css('--phone').search('false') !== -1){
            if (document.querySelector('#i-container').getBoundingClientRect().top < document.querySelector('#-head').offsetHeight-document.querySelector('#i-head').offsetHeight){
                i_menu.addClass('fix');
            }else{
                i_menu.removeClass('fix');
            }
        }
    }
    $(window).scroll(function () {
        if (bounce_timer){
            bounce_timer = false;
            setTimeout(function () {
                bounce_timer = true;
                handle_menu();
            }, 100);
            handle_menu();
        }
    });
    handle_menu();
    let info = ["unknown", "default.jpg"];
    ajax_(function (r) {
            info = r['data'];
        },"/public_", {'key': 'get_account_info'},
        {complete: function(){
            new Vue({
                el: "#i-head",
                data: {
                    uploading: false,
                    upload_percent: 0,
                    user_name: info[0],
                    cover: path+'/static/img/'+document.querySelector('#i-body').getAttribute('data-who')+'/'+info[1],
                },
                methods: {
                    change_cover: function(){
                        let vue_ = this;
                        let file = this.$el.querySelector('input[type=file]').files[0];
                        if (typeof file!='undefined'){
                            if (file.size/1024**2 < 5){
                                let form = new FormData();
                                form.append('f', file);
                                form.append('key', 'change_cover');
                                vue_.uploading = true;
                                ajax_(function (r) {
                                    if (r['state'] === 'success') {
                                        head_vue.remind({'state': 'success', 'msg': '修改成功'})
                                    }
                                    location.reload();
                                }, '/public_', form, {processData: false, contentType: false,
                                    xhr: function (){return listen_upload_progress(function (e) {
                                        vue_.upload_percent = e.loaded / e.total;
                                    })}})
                            }else{
                                alert('图片需小于5M')
                            }
                        }
                    },
                    change_name: function (e) {
                        let v = this.user_name;
                        if (v.length >=4 && v.length <= 16){
                            e.target.setAttribute('disabled', '');
                            ajax_(function (r) {
                                    if (r['state'] === 'error'){
                                        head_vue.remind({'state': 'error', 'msg': '店铺名重复'})
                                    }else{
                                        head_vue.remind({'state': 'success', 'msg': '修改成功'})
                                    }
                                }, '/public_', {'key': 'change_name', 'name': v}, {complete: function () {
                                    e.target.removeAttribute('disabled');
                                }})
                        }else{
                            head_vue.remind({'state': 'error', 'msg': '昵称4-16位'})
                        }
                    },
                    logout: function (e) {
                        if (!confirm('确认登出?')){return}
                        e.target.setAttribute('disabled', '');
                        ajax_(function (r) {
                                if (r['state']==='success') location.href = path+'/login?url='+location.href;
                            }, '/public_', {'key': 'logout'})
                    }
                }
            })
        },
    });
});