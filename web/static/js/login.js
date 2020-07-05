$(document).ready(function () {
    new Vue({
        el: "#login-div",
        data: function(){
            return {
                swc_left: '0',
            }
        },
        computed: {
            switch_left: function () {
                return {'left': this.swc_left};
            }
        },
        methods: {
            switch_: function (e) {
                let btn = e.target;
                btn.parentElement.querySelector('button.active').className = '';
                btn.className = 'active';
                this.swc_left = (btn.innerText==='我是顾客')? '0': '-100%';
            }
        }
    })
});