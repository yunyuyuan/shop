Vue.component("login", {
    props: ['tp'],
    data: function(){
        return {
            name: '',
            email: '',
            pwd: '',
            code: '',
            at_register: false
        }
    },
    template: "<div :class='tp'>" +
        "<div>" +
        "   <div class='underline-input' v-show='at_register'><input class='input-name' @keyup.enter='to_next(\"email\")' :placeholder='cal_name' v-model='name' maxlength='16'/><span></span></div>" +
        "   <div class='underline-input'><input class='input-email' @keyup.enter='to_next(\"pwd\")' placeholder='电子邮箱' v-model='email'/><span></span></div>" +
        "   <div class='underline-input'><input class='input-pwd' @keyup.enter='to_next(\"submit\")' type='password' placeholder='输入密码' v-model='pwd' maxlength='20'/><span></span></div>" +
        "   <div class='code' v-show='at_register'>" +
        "       <div class='underline-input'><input class='input-code' @keyup.enter='to_next(\"submit\")' placeholder='邮箱验证码' v-model='code' maxlength='4'/><span></span></div>" +
        "       <button @click='send_code'>获取</button>" +
        "   </div>" +
        "   <div class='submit'>" +
        "       <button @click='do_submit' :register='at_register'>{{at_what}}</button>" +
        "       <a @click='switch_'>{{forward_what}}</a>" +
        "   </div>" +
        "</div>" +
        "</div>",
    computed: {
        cal_name: function () {
            return (this.$props['tp']==='customer')? '昵称': '店铺名'
        },
        forward_what: function () {
            return (this.at_register)? '前往登录': '前往注册'
        },
        at_what: function () {
            return (this.at_register)? '注册': '登录'
        }
    },
    methods: {
        switch_: function () {
            this.at_register = !this.at_register;
        },
        send_code: function(e){
            let div = e.target.parentElement.parentElement;
            let datas = check_input(div, this.at_register, this);
            if (!datas) return;
            let email = datas[1];
            let get_btn = div.querySelector('div.code button');
            get_btn.setAttribute('disabled', '');
            get_btn.innerText = '获取中...';
            ajax_(function (r) {
                    if (r['state'] === 'success'){
                        let count = 60;
                        let interval = setInterval(function () {
                            get_btn.innerText = count+'s后';
                            count--;
                            if (count === 0){
                                clearInterval(interval);
                                get_btn.removeAttribute('disabled');
                                get_btn.innerText = '获取';
                            }
                        }, 1000)
                    }else{
                        head_vue.remind(r);
                        get_btn.innerText = '获取';
                        get_btn.removeAttribute('disabled');
                    }
                }, '/public_', {'key': 'send_code', 'tp': this.$props['tp'], 'email': email})
        },
        do_submit: function (e) {
            let btn = e.target;
            let div = btn.parentElement.parentElement;
            let data;
            let datas = check_input(div, this.at_register, this);
            if (!datas) return;
            let [name, email, pwd] = datas;
            btn.setAttribute('disabled', '');
            btn.innerText = '验证中...';
            if (this.at_register){
                let code = this.code;
                if (code.length !== 4){
                    alert('验证码为4位');
                    btn.innerText = '注册';
                    return
                }
                data = {'key': 'register', 'tp': this.$props['tp'], 'name': name, 'email': email, 'pwd': hex_md5(pwd), 'code': code};
            }else{
                data = {'key': 'login', 'tp': this.$props['tp'], 'email': email, 'pwd': hex_md5(pwd)};
            }
            ajax_(function (r) {
                if (r['state'] === 'success'){
                    btn.innerText = '正在跳转';
                    let map = para_to_map();
                    if (map.hasOwnProperty('url')){
                        location.href = map['url'];
                    }else{
                        location.href = path+'/';
                    }
                    return
                }
                head_vue.remind(r);
                btn.innerText = '登录';
                btn.removeAttribute('disabled');
            }, '/public_', data)
        },
        to_next: function (s) {
            if (s==='submit'){
                if (this.at_register){
                    this.$el.querySelector("input.input-code").focus();
                }
                else{
                    this.$el.querySelector(".submit button").click();
                }
            }else{
                this.$el.querySelector("input.input-"+s).focus();
            }
        },
    }
});
function check_input(div, is_register, vue_) {
    let name = vue_.name,
        email = vue_.email,
        pwd = vue_.pwd;
    if (is_register){
        if (!(4 <= name.length && name.length <= 16)){
            alert('昵称4-16位');
            return false
        }
    }
    if (email.search(check_mail_s) === -1){
        alert('电子邮箱格式错误');
        return false
    }
    if (!(4 <= pwd.length && pwd.length <= 16)){
        alert('密码4-16位');
        return false
    }
    return [name, email, pwd];
}