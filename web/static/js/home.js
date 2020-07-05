$(document).ready(function () {
    let vue_ = new Vue({
        el: '#home',
        data: {
            data_lis:[['最新上架', []], ['热门商品', []]]
        },
    });
    ajax_(function (r) {
        if (r['state']==='success'){
            vue_.data_lis[0].splice(1, 1, r['data'][0]);
            vue_.data_lis[1].splice(1, 1, r['data'][1]);
        }
    }, 'public_', {'key': 'get_home'})
});