//定义业务服务
app.service("itemCatService",function ($http) {
    //加载列表数据
    this.findAll = function(){
        return $http.get("../itemCat/findAll.do");
    };

    this.findPage = function (page, rows) {
        return $http.get("../itemCat/findPage.do?page=" + page + "&rows=" + rows);
    };

    this.add = function (entity) {
        return $http.post("../itemCat/add.do",entity);
    };

    this.update = function (entity) {
        return $http.post("../itemCat/update.do",entity);
    };

    this.findOne = function (id) {
        return $http.get("../itemCat/findOne.do?id=" + id);
    };

    this.delete = function (selectedIds) {
        return $http.get("../itemCat/delete.do?ids=" + selectedIds);
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../itemCat/search.do?page=" + page + "&rows=" + rows, searchEntity);

    };

    this.findByParentId = function (parentId) {
        return $http.get("../itemCat/findByParentId.do?parentId=" + parentId);
    };

});