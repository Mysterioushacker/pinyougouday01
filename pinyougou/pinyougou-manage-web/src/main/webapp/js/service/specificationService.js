//定义业务服务
app.service("specificationService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../specification/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../specification/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../specification/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../specification/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../specification/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../specification/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../specification/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };
    //查询规格列表
    this.selectOptionList = function () {
        return $http.get("../specification/selectOptionList.do");
    };

});