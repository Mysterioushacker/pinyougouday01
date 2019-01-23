//定义业务服务
app.service("specificationOptionService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../specificationOption/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../specificationOption/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../specificationOption/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../specificationOption/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../specificationOption/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../specificationOption/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../specificationOption/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };
});