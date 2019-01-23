//定义业务服务
app.service("contentService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../content/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../content/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../content/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../content/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../content/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../content/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../content/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };
});