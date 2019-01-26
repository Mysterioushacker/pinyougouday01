//定义业务服务
app.service("typeTemplateService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../typeTemplate/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../typeTemplate/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../typeTemplate/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../typeTemplate/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../typeTemplate/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../typeTemplate/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../typeTemplate/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };

    this.findSpecList = function (id) {
        return $http.get("../typeTemplate/findSpecList.do?id=" + id);
    };

});