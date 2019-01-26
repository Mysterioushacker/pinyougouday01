//定义业务服务
app.service("goodsService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../goods/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../goods/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../goods/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../goods/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../goods/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../goods/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../goods/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };

    this.updateStatus = function (selectedIds, status) {
        return $http.get("../goods/updateStatus.do?ids=" + selectedIds + "&status=" + status);
    };
});