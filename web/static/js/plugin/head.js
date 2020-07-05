$(document).ready(function () {
    head_vue = new Vue({
        el: '#-head',
        data: {
            choose: 'goods'
        },
        computed: {
            cal_choose_what: function () {
                return (this.choose==='goods')? '商品':'店铺'
            }
        },
        methods: {
            go_search: function () {
                location.href = path+'/list?at='+this.choose+'&t=search&s='+this.$el.querySelector('.search > input').value;
            },
            remind: function (s) {
                this.$refs['head_remind'].start(s)
            }
        }
    })
});