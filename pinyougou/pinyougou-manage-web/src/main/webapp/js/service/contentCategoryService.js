//定义业务服务
app.service("contentCategoryService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../contentCategory/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../contentCategory/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../contentCategory/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../contentCategory/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../contentCategory/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../contentCategory/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../contentCategory/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };
});