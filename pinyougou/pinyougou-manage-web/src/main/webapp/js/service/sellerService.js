//定义业务服务
app.service("sellerService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../seller/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../seller/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../seller/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../seller/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../seller/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../seller/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../seller/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };
});